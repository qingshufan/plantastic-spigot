package fan.cropsprocess.com.listener;

import fan.cropsprocess.com.data.crud.RankDataCrud;
import fan.cropsprocess.com.data.entity.RankData;
import fan.cropsprocess.com.inventory.GuiLoader;
import fan.cropsprocess.com.inventory.MyInventory;
import fan.cropsprocess.com.inventory.TradeGuiLoader;
import fan.cropsprocess.com.util.CpCore;
import fan.cropsprocess.com.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarketListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return; // 确保点击的是玩家
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        YamlConfiguration guiYml = CpCore.cpcore.configs.get("market.yml");

        ConfigurationSection pathConfig = guiYml.getConfigurationSection("markets.seeds");
        ConfigurationSection contentsConfig = pathConfig.getConfigurationSection("contents");
        // 确保点击了的物品不是空的
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        // 判断是否为商店选择界面
        if (event.getView().getTitle().equals("§2选择商店")) {
            event.setCancelled(true); // 阻止玩家操作商店选择界面的物品

            // 根据玩家点击的物品来进入对应的商店
            if (clickedItem.getType() == Material.DIAMOND_PICKAXE) {
                Inventory toolsShop = TradeGuiLoader.loadShop("tools", "market.yml");
                player.openInventory(toolsShop); // 打开工具商店
            } else if (clickedItem.getType() == Material.WHEAT_SEEDS) {
                Inventory seedsShop = TradeGuiLoader.loadShop("seeds", "market.yml");
                MyInventory myInventory = GuiLoader.loader("skillTree.book");
                RankDataCrud rankDataCrud = CpCore.cpconfig.rankDataCrud;
                RankData rankData = rankDataCrud.existRankData(player.getUniqueId()) ? rankDataCrud.getRankData(player.getUniqueId()) :
                        new RankData();
                //保证第一次数据有UUID做主键
                rankData.setUuid(player.getUniqueId());
                Map<String, Boolean> books = rankData.getBooks() == null ? new HashMap<String, Boolean>() : rankData.getBooks();
                //读取所有图鉴物品一一比对
                for (String keyItem : contentsConfig.getKeys(false)) {
                    if (contentsConfig.getBoolean(keyItem + ".isBook") && books.containsKey(keyItem)) {
                        ItemStack itemStack = myInventory.getInventory().getItem(myInventory.getContentsConfig().getInt(keyItem + ".index"));
                        List<String> lores = itemStack.getItemMeta().getLore();
                        lores.remove(lores.size() - 1);
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setLore(lores);
                        itemStack.setItemMeta(itemMeta);
                        seedsShop.setItem(contentsConfig.getInt(keyItem + ".index"), itemStack);
                    }
                }
                player.openInventory(seedsShop); // 打开种子商店
            } else if (clickedItem.getType() == Material.WHEAT) {
                Inventory cropsShop = TradeGuiLoader.loadShop("crops", "market.yml");
                player.openInventory(cropsShop); // 打开农作物商店
            }
            return;
        }
        YamlConfiguration langConfig = CpCore.cpcore.configs.get("language.yml");
        // 判断是否为商店界面（工具商店、种子商店、农作物商店）
        if (event.getView().getTitle().equals(CpCore.cpcore.convertColorSign(langConfig.getString("shop.tools.name")))
                || event.getView().getTitle().equals(CpCore.cpcore.convertColorSign(langConfig.getString("shop.seeds.name")))) {
            // 如果点击的是玩家物品栏，不进行处理
            if (event.getClickedInventory().equals(player.getInventory())) return; // 防止点击玩家个人物品栏

            // 判断返回按钮
            if (clickedItem.getType() == Material.ARROW && clickedItem.getItemMeta() != null && clickedItem.getItemMeta().getDisplayName().equals("§c返回市场选择")) {
                openMarketSelector(player); // 返回商店选择界面
                event.setCancelled(true);
                return;
            }
            boolean isBook = false;
            boolean clickBook = false;
            String keyItemSel = "";
            if (event.getView().getTitle().equals(CpCore.cpcore.convertColorSign(langConfig.getString("shop.seeds.name")))) {
                RankDataCrud rankDataCrud = CpCore.cpconfig.rankDataCrud;
                RankData rankData = rankDataCrud.existRankData(player.getUniqueId()) ? rankDataCrud.getRankData(player.getUniqueId()) :
                        new RankData();
                //保证第一次数据有UUID做主键
                rankData.setUuid(player.getUniqueId());
                Map<String, Boolean> books = rankData.getBooks() == null ? new HashMap<String, Boolean>() : rankData.getBooks();

                //读取所有图鉴物品一一比对
                for (String keyItem : contentsConfig.getKeys(false)) {
                    if (event.getSlot() == contentsConfig.getInt(keyItem + ".index")) {
                        if (contentsConfig.getBoolean(keyItem + ".isBook")) {
                            clickBook = true;
                            if (!books.containsKey(keyItem)) isBook = true;
                            keyItemSel = keyItem;
                        }
                        break;
                    }
                }
            }
            if (isBook) { //没有解锁不能购买
                CpCore.cpcore.sendTtile(player, CpCore.cpcore.convertColorSign(langConfig.getString("market.lock")));
                player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                player.closeInventory();
                return;
            }
            // 获取点击物品的价格
            int itemPrice = TradeGuiLoader.getItemPrice(clickedItem);

            if (event.getView().getTitle().equals(CpCore.cpcore.convertColorSign(langConfig.getString("shop.seeds.name"))) && clickBook) {
                itemPrice = contentsConfig.getInt(keyItemSel + ".price");
            }
            if (itemPrice == 0) {
                CpCore.cpcore.sendMsg(player, CpCore.cpcore.convertColorSign(langConfig.getString("shop.noPrice")));
                player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                event.setCancelled(true);
                return;
            }

            // 处理点击类型（左键购买，右键出售）
            ClickType clickType = event.getClick();

            if (clickType == ClickType.LEFT) {
                // 左键购买物品
                int playerLevel = player.getLevel();
                if (playerLevel >= itemPrice) {
                    // 玩家金币足够，扣除金币并添加物品到背包
                    player.setLevel(playerLevel - itemPrice);
                    ItemStack itemToBuy = clickedItem;
                    player.getInventory().addItem(itemToBuy);
                    CpCore.cpcore.sendMsg(player, CpCore.cpcore.convertColorSign(langConfig.getString("shop.suCost"))
                            .replace("%type%", clickedItem.getItemMeta().getDisplayName())
                            .replace("%num%", "" + itemPrice));
                    player.playSound(player, Sound.ENTITY_VILLAGER_CELEBRATE, 1, 1);
                } else {
                    CpCore.cpcore.sendMsg(player, CpCore.cpcore.convertColorSign(langConfig.getString("shop.noCost")));
                    player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                }
            } else if (clickType == ClickType.RIGHT) {
                // 右键出售物品
                int totalSellPrice = 0;
                List<ItemStack> conditionItems = ItemUtils.getConditionAllItems(player.getUniqueId(),
                        clickedItem.getItemMeta().getDisplayName(),
                        clickedItem.getItemMeta().getLore());
                int amt = 0;
                // 检查玩家背包中的物品并计算出售价格
                for (ItemStack itemInInventory : conditionItems) {
                    totalSellPrice += itemPrice * itemInInventory.getAmount(); // 计算所有相同物品的出售价格
                    amt += itemInInventory.getAmount();
                }
                ItemUtils.takeItem(amt, conditionItems); //拿走所有

                if (totalSellPrice > 0) {
                    // 增加玩家金币并发送反馈
                    player.setLevel(player.getLevel() + totalSellPrice);
                    CpCore.cpcore.sendMsg(player, CpCore.cpcore.convertColorSign(langConfig.getString("shop.suItem"))
                            .replace("%type%", clickedItem.getItemMeta().getDisplayName())
                            .replace("%num%", "" + totalSellPrice));
                    player.playSound(player, Sound.ENTITY_VILLAGER_CELEBRATE, 1, 1);

                } else {
                    CpCore.cpcore.sendMsg(player, CpCore.cpcore.convertColorSign(langConfig.getString("shop.noItem")));
                    player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                }
            }

            // 阻止商店界面物品的其他操作
            event.setCancelled(true);
        } else if (event.getView().getTitle().equals(CpCore.cpcore.convertColorSign(langConfig.getString("shop.crops.name")))) {
            handleCropsShop(event, player, clickedItem, langConfig);
        }
    }

    private void handleCropsShop(InventoryClickEvent event, Player player, ItemStack clickedItem, YamlConfiguration langConfig) {
        // 如果点击了返回按钮
        if (clickedItem.getType() == Material.ARROW && clickedItem.getItemMeta() != null && clickedItem.getItemMeta().getDisplayName().equals("§c返回市场选择")) {
            openMarketSelector(player); // 返回商店选择界面
            event.setCancelled(true);
            return;
        }


        // 从market.yml中加载配置
        YamlConfiguration marketConfig = CpCore.cpcore.configs.get("market.yml");
        ConfigurationSection cropsSection = marketConfig.getConfigurationSection("markets.crops.contents");

        // 查找与点击物品匹配的内容
        String cropKey = null;
        for (String key : cropsSection.getKeys(false)) {
            String name = cropsSection.getString(key + ".name");
            if (event.getSlot() == cropsSection.getInt(key + ".index")) {
                cropKey = key;
                break;
            }
        }
        if (cropKey == null) {
            CpCore.cpcore.sendMsg(player, CpCore.cpcore.convertColorSign(langConfig.getString("shop.noPrice")));
            event.setCancelled(true);
            return;
        }
        // 获取农作物基础信息
        int basePrice = cropsSection.getInt(cropKey + ".price");
        String cropName = cropsSection.getString(cropKey + ".name").replaceAll("§.", "");

        // 右键出售物品
        double totalSellPrice = basePrice;
        List<ItemStack> conditionItems = ItemUtils.getConditionAllItems(player.getUniqueId(),
                "",
                "", clickedItem.getType()); //只检测Type
        int amt = 0;
        String regex = "&f[-+]?\\d*\\.?\\d+(?:[eE][-+]?\\d+)?";
        regex = CpCore.cpcore.convertColorSign(regex);
        final double[] WEIGHTS = {0.2, 0.3, 0.25, 0.15, 0.1}; //每个指标的金币权重
        // 检查玩家背包中的物品并计算出售价格
        for (ItemStack itemInInventory : conditionItems) {
            List<String> extraLoreList = itemInInventory.getItemMeta().getLore();
            boolean isMatch = false;
            int i = 0;
            for (String lore : extraLoreList) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(lore);
                if (matcher.find()) {
                    String range = matcher.group(); // 获取内容
                    String valueStr = range.substring(2); //跳过&f
                    Double value = Double.valueOf(valueStr);
                    totalSellPrice += value * itemInInventory.getAmount() * WEIGHTS[i++];
                    isMatch = true;
                }
            }
            if (isMatch) amt += itemInInventory.getAmount();
        }
        if (amt == 0) {
            CpCore.cpcore.sendMsg(player, CpCore.cpcore.convertColorSign(langConfig.getString("shop.noItem")));
            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }
        ItemUtils.takeItem(amt, conditionItems); //拿走所有

        if (totalSellPrice > 0) {
            // 增加玩家金币并发送反馈
            player.setLevel(player.getLevel() + (int) Math.round(totalSellPrice));
            CpCore.cpcore.sendMsg(player, CpCore.cpcore.convertColorSign(langConfig.getString("shop.suItem"))
                    .replace("%type%", cropName)
                    .replace("%num%", "" + (int) Math.round(totalSellPrice)));

            player.playSound(player, Sound.ENTITY_VILLAGER_CELEBRATE, 1, 1);
        } else {
            CpCore.cpcore.sendMsg(player, CpCore.cpcore.convertColorSign(langConfig.getString("shop.noItem")));
            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
        }
        event.setCancelled(true);
    }


    // 打开选择商店界面
    private void openMarketSelector(Player player) {
        Inventory marketSelector = TradeGuiLoader.loadShopSelector("market.yml");
        player.openInventory(marketSelector);
    }
}
