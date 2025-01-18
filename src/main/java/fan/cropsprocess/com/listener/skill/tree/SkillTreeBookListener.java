package fan.cropsprocess.com.listener.skill.tree;

import fan.cropsprocess.com.data.crud.RankDataCrud;
import fan.cropsprocess.com.data.entity.RankData;
import fan.cropsprocess.com.inventory.GuiLoader;
import fan.cropsprocess.com.inventory.MyInventory;
import fan.cropsprocess.com.util.CpCore;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;


//种子图鉴
public class SkillTreeBookListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        MyInventory myInventory = GuiLoader.loader("skillTree.book");
        ConfigurationSection skillTreeConfig = CpCore.cpcore.configs.get("skillTree.yml").getConfigurationSection("skillTree.book");
        YamlConfiguration langConfig = CpCore.cpcore.configs.get("language.yml");
        if (event.getView().getTitle().equals(myInventory.getTitle())) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            RankDataCrud rankDataCrud = CpCore.cpconfig.rankDataCrud;
            RankData rankData = rankDataCrud.existRankData(player.getUniqueId()) ? rankDataCrud.getRankData(player.getUniqueId()) :
                    new RankData();
            //保证第一次数据有UUID做主键
            rankData.setUuid(player.getUniqueId());
            Map<String, Boolean> books = rankData.getBooks() == null ? new HashMap<String, Boolean>() : rankData.getBooks();

            //返回科技树主门户按钮
            if (event.getCurrentItem() != null && event.getSlot() == myInventory.getContentsConfig().getInt("go_back.index")) {
                Inventory inv = GuiLoader.loader("skillTree.hub").getInventory();
                player.openInventory(inv);
                return;
            }

            //左键解锁种子
            if (event.isLeftClick()) {
                //读取所有图鉴物品一一比对
                for (String keyItem : myInventory.getContentsConfig().getKeys(false)) {
                    if (event.getSlot() == myInventory.getContentsConfig().getInt(keyItem + ".index") && event.getSlot() != myInventory.getContentsConfig().getInt("go_back.index")) {
                        if (books.containsKey(keyItem)) {
                            CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.book.get")
                                    .replace("%type%", myInventory.getInventory().getItem(event.getSlot()).getItemMeta().getDisplayName())
                            );
                            return;
                        } else {
                            int result = player.getLevel() - skillTreeConfig.getInt(keyItem + ".cost");
                            if (result < 0) {
                                CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.noCost"));
                                return;
                            }
                            player.setLevel(result);
                            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                            books.put(keyItem, true);
                            player.closeInventory();
                            CpCore.cpcore.sendTtile(player, langConfig.getString("skillTree.book.suc")
                                    .replace("%type%", myInventory.getInventory().getItem(event.getSlot()).getItemMeta().getDisplayName())
                            );
                        }
                        break;
                    }
                }
                rankData.setBooks(books);
                rankDataCrud.setRankData(rankData);
            }
            //右键判断是否解锁
            else if (event.isRightClick()) {
                for (String keyItem : myInventory.getContentsConfig().getKeys(false)) {
                    if (event.getSlot() == myInventory.getContentsConfig().getInt(keyItem + ".index") && event.getSlot() != myInventory.getContentsConfig().getInt("go_back.index")) {
                        if (books.containsKey(keyItem)) {
                            CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.book.get")
                                    .replace("%type%", myInventory.getInventory().getItem(event.getSlot()).getItemMeta().getDisplayName())
                            );
                        } else {
                            CpCore.cpcore.sendMsg(player, langConfig.getString("skillTree.book.uget")
                                    .replace("%type%", myInventory.getInventory().getItem(event.getSlot()).getItemMeta().getDisplayName())
                            );
                        }
                        break;
                    }
                }
            }
        }
    }
}
