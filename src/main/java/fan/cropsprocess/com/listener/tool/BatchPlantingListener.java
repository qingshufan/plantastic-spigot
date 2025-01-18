package fan.cropsprocess.com.listener.tool;

import fan.cropsprocess.com.data.crud.RankDataCrud;
import fan.cropsprocess.com.util.CpCore;
import fan.cropsprocess.com.util.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class BatchPlantingListener implements Listener {
    //记录起始位置和终止位置
    private Location firstClick = null;
    private Location secondClick = null;

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();


        YamlConfiguration langConfig = CpCore.cpcore.configs.get("language.yml");
        // 触发科技树
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null || clickedBlock.getType().equals(Material.AIR)) return;

            if (isSeed(item)) {
                RankDataCrud rankDataCrud = CpCore.cpconfig.rankDataCrud;
                int maxLevel = rankDataCrud.existRankData(player.getUniqueId()) ? rankDataCrud.getRankData(player.getUniqueId()).getGrowLevel() : 0;
                if (maxLevel == 0) {
                    return;
                }
                event.setCancelled(true);
                int sign = clickedBlock.getZ() - player.getLocation().getZ() < 0 ? -1 : 1;
                int amt = 0;
                List<Block> blocks = new ArrayList<>();
                for (int level = 0; level < maxLevel; level++) {
                    // 获取点击方块的位置
                    int x = clickedBlock.getX();
                    int y = clickedBlock.getY();
                    int z = clickedBlock.getZ() + level * sign;
                    // 获取世界对象
                    World world = clickedBlock.getWorld();
                    // 将点击的方块、左边和右边三个方块破坏
                    for (int i = -1; i <= 1; i++) {
                        Block blockToChange = world.getBlockAt(x + i, y, z);
                        // 检查是否是农田
                        if (blockToChange.getType() == Material.FARMLAND) {
                            //相对坐标
                            Block aboveBlock = blockToChange.getRelative(0, 1, 0);
                            Material type = item.getType();
                            // 只有当上方的方块为空气时才可以种植
                            if (aboveBlock.getType() == Material.AIR) {
                                blocks.add(aboveBlock);
                                amt++;
                            }
                        }
                    }
                }

                int totalSeedsRequired = amt;
                List<ItemStack> conditionItems = ItemUtils.getConditionItems(player.getUniqueId(), item.getItemMeta().getDisplayName(), item.getItemMeta().getLore());
                if (!ItemUtils.hasItem(totalSeedsRequired, conditionItems)) {
                    player.sendMessage("种子数量不足！");
                    return;
                }
                //执行效果
                for (Block aboveBlock : blocks) {
                    Material type = item.getType();
                    // 根据种子的类型决定种植的作物方块类型
                    if (type == Material.WHEAT_SEEDS) {
                        aboveBlock.setType(Material.WHEAT); // 小麦种子变为小麦作物
                    } else if (type == Material.CARROT) {
                        aboveBlock.setType(Material.CARROTS); // 胡萝卜种子变为胡萝卜作物
                    } else if (type == Material.POTATO) {
                        aboveBlock.setType(Material.POTATOES); // 土豆种子变为土豆作物
                    } else if (type == Material.BEETROOT_SEEDS) {
                        aboveBlock.setType(Material.BEETROOTS); // 甜菜根种子变为甜菜作物
                    } else if (type == Material.MELON_SEEDS) {
                        aboveBlock.setType(Material.MELON); // 西瓜种子变为西瓜作物
                    } else if (type == Material.PUMPKIN_SEEDS) {
                        aboveBlock.setType(Material.PUMPKIN); // 南瓜种子变为南瓜作物
                    }
                    CpCore.cpcore.plantCrop(aboveBlock, player.getUniqueId());
                    player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, aboveBlock.getLocation(), 10);
                }
                // 扣除种子
                ItemUtils.takeItem(totalSeedsRequired, conditionItems);

                CpCore.cpcore.sendActionBar(player, langConfig.getString("tool.grow")
                        .replace("%lv%", (maxLevel) + "")
                        .replace("%amt%", (amt) + "")
                        .replace("%maxAmt%", (3 * maxLevel) + "")
                );
            }
        }
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            // 检查玩家是否点击了空白地方
            if (clickedBlock == null || clickedBlock.getType() == Material.AIR) return; // 确保点击的是有效的方块

            if (isSeed(item)) {
                event.setCancelled(true);
                // 第一次点击
                if (firstClick == null) {
                    firstClick = clickedBlock.getLocation();
                    player.sendMessage("First point selected. Now click the second point.");
                    return;
                }

                // 第二次点击
                secondClick = clickedBlock.getLocation();

                // 计算种植区域的面积
                int areaWidth = Math.abs(firstClick.getBlockX() - secondClick.getBlockX()) + 1;
                int areaHeight = Math.abs(firstClick.getBlockZ() - secondClick.getBlockZ()) + 1;
                int totalSeedsRequired = areaWidth * areaHeight;

                List<ItemStack> conditionItems = ItemUtils.getConditionItems(player.getUniqueId(), item.getItemMeta().getDisplayName(), item.getItemMeta().getLore());
                if (!ItemUtils.hasItem(totalSeedsRequired, conditionItems)) {
                    player.sendMessage("种子数量不足！");
                    resetSelection();
                    return;
                }

                // 执行种植操作
                plantCrops(player, firstClick, secondClick, item.getType());
                CpCore.cpcore.sendTtile(player, langConfig.getString("magic.grow"));

                // 扣除种子
                ItemUtils.takeItem(totalSeedsRequired, conditionItems);
                resetSelection();
            }
        }
    }

    private boolean isSeed(ItemStack item) {
        if (item == null || item.getType().equals(Material.AIR)) {
            return false;
        }
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !item.getItemMeta().hasLore()) {
            return false;
        }
        Material type = item.getType();
        // 检查物品类型是否为种子
        return type == Material.WHEAT_SEEDS ||
                type == Material.CARROT ||
                type == Material.POTATO ||
                type == Material.BEETROOT_SEEDS ||
                type == Material.MELON_SEEDS ||
                type == Material.PUMPKIN_SEEDS;
    }

    private int countSeeds(PlayerInventory inventory, ItemStack item) {
        int seedCount = 0;

        if (item == null || item.getType() == Material.AIR) {
            return 0; // 如果传入的item为空或空物品，返回0
        }

        Material type = item.getType();  // 获取种子类型
        for (ItemStack item1 : inventory.getContents()) {
            if (item1 != null && item1.getType() == type) {  // 检查背包中的物品是否与传入的种子类型相同
                seedCount += item1.getAmount();  // 累加该物品的数量
            }
        }

        return seedCount;
    }


    private void plantCrops(Player player, Location firstClick, Location secondClick, Material type) {
        int minX = Math.min(firstClick.getBlockX(), secondClick.getBlockX());
        int maxX = Math.max(firstClick.getBlockX(), secondClick.getBlockX());
        int minZ = Math.min(firstClick.getBlockZ(), secondClick.getBlockZ());
        int maxZ = Math.max(firstClick.getBlockZ(), secondClick.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Block block = player.getWorld().getBlockAt(x, firstClick.getBlockY(), z);
                // 检查是否是农田
                if (block.getType() == Material.FARMLAND) {
                    //相对坐标
                    Block aboveBlock = block.getRelative(0, 1, 0);

                    // 只有当上方的方块为空气时才可以种植
                    if (aboveBlock.getType() == Material.AIR) {
                        // 根据种子的类型决定种植的作物方块类型
                        if (type == Material.WHEAT_SEEDS) {
                            aboveBlock.setType(Material.WHEAT); // 小麦种子变为小麦作物
                        } else if (type == Material.CARROT) {
                            aboveBlock.setType(Material.CARROTS); // 胡萝卜种子变为胡萝卜作物
                        } else if (type == Material.POTATO) {
                            aboveBlock.setType(Material.POTATOES); // 土豆种子变为土豆作物
                        } else if (type == Material.BEETROOT_SEEDS) {
                            aboveBlock.setType(Material.BEETROOTS); // 甜菜根种子变为甜菜作物
                        } else if (type == Material.MELON_SEEDS) {
                            aboveBlock.setType(Material.MELON); // 西瓜种子变为西瓜作物
                        } else if (type == Material.PUMPKIN_SEEDS) {
                            aboveBlock.setType(Material.PUMPKIN); // 南瓜种子变为南瓜作物
                        }
                        CpCore.cpcore.plantCrop(aboveBlock, player.getUniqueId());
                    }
                }
            }
        }
    }

    private void resetSelection() {
        firstClick = null;
        secondClick = null;
    }
}
