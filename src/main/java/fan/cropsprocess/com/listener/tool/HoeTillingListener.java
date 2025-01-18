package fan.cropsprocess.com.listener.tool;

import fan.cropsprocess.com.data.crud.RankDataCrud;
import fan.cropsprocess.com.util.CpCore;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class HoeTillingListener implements Listener {
    private Location startLocation = null;
    private Location endLocation = null;

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        YamlConfiguration langConfig = CpCore.cpcore.configs.get("language.yml");
        // 确保点击的是主手（避免副手）
        if (Objects.equals(event.getHand(), EquipmentSlot.OFF_HAND)) return;

        // 检查是否为左键点击
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();

            if (clickedBlock == null || clickedBlock.getType().equals(Material.AIR)) return;
            // 如果玩家使用的是锄头（Hoe）
            if (isHoe(item)) {
                event.setCancelled(true);
                RankDataCrud rankDataCrud = CpCore.cpconfig.rankDataCrud;
                int maxLevel = rankDataCrud.existRankData(player.getUniqueId()) ? rankDataCrud.getRankData(player.getUniqueId()).getHoeLevel() : 0;
                if (maxLevel == 0) return;
                int sign = clickedBlock.getZ() - player.getLocation().getZ() < 0 ? -1 : 1;
                int amt = 0;
                for (int level = 0; level < maxLevel; level++) {
                    // 获取点击方块的位置
                    int x = clickedBlock.getX();
                    int y = clickedBlock.getY();
                    int z = clickedBlock.getZ() + level * sign;
                    // 获取世界对象
                    World world = clickedBlock.getWorld();
                    // 将点击的方块、左边和右边三个方块变成耕地方块
                    for (int i = -1; i <= 1; i++) {
                        Block blockToChange = world.getBlockAt(x + i, y, z);
                        if (blockToChange.getType().equals(Material.GRASS_BLOCK) || blockToChange.getType().equals(Material.DIRT)) {
                            blockToChange.setType(Material.FARMLAND);
                            player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, blockToChange.getLocation().add(0, 0.5, 0), 10);
                            amt++;
                        }
                    }
                }
                CpCore.cpcore.sendActionBar(player, langConfig.getString("tool.hoe")
                        .replace("%lv%", (maxLevel) + "")
                        .replace("%amt%", (amt) + "")
                        .replace("%maxAmt%", (3 * maxLevel) + "")
                );

            }
        }
        // 检查是否为右键点击
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null || clickedBlock.getType() == Material.AIR) return; // 确保点击的是有效的方块

            // 如果玩家使用的是锄头（Hoe）
            if (isHoe(item)) {
                event.setCancelled(true);
                if (startLocation == null) {
                    // 玩家第一次点击，记录为起点
                    startLocation = clickedBlock.getLocation();
                    player.sendMessage("点击第二个点来选择区域的终点，完成选择！");
                } else {
                    // 玩家第二次点击，记录为终点
                    endLocation = clickedBlock.getLocation();

                    // 执行将土方块变为耕地的操作
                    if (tillLandInRange(player, startLocation, endLocation)) {
                        CpCore.cpcore.sendTtile(player, langConfig.getString("magic.hoe"));
                    }

                    // 清除玩家的选择数据
                    startLocation = null;
                    endLocation = null;
                }
            }
        }
    }

    private boolean isHoe(ItemStack item) {
        if (item == null || item.getType().equals(Material.AIR)) {
            return false;
        }
        return item.getType().toString().endsWith("_HOE"); // 判断是否为锄头
    }

    private boolean tillLandInRange(Player player, Location start, Location end) {
        // 获取锄头的耕地范围，范围根据锄头的材质确定
        ItemStack hoe = player.getInventory().getItemInMainHand();
        int areaRadius = 2 * getHoeAreaSize(hoe) + 1;
        // 可以根据锄头的材质设置不同范围

        // 计算起点和终点的坐标
        int startX = Math.min(start.getBlockX(), end.getBlockX());
        int startY = Math.min(start.getBlockY(), end.getBlockY());
        int startZ = Math.min(start.getBlockZ(), end.getBlockZ());
        int endX = Math.max(start.getBlockX(), end.getBlockX());
        int endY = Math.max(start.getBlockY(), end.getBlockY());
        int endZ = Math.max(start.getBlockZ(), end.getBlockZ());

        if ((endX - startX) > areaRadius || (endZ - startZ) > areaRadius) {
            return false;
        }

        // 循环遍历矩形区域内的所有方块
        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                for (int y = startY; y <= endY; y++) {
                    Block currentBlock = start.getWorld().getBlockAt(x, y, z);
                    if (currentBlock.getType().equals(Material.GRASS_BLOCK) || currentBlock.getType().equals(Material.DIRT)) {
                        // 将土方块或草方块转换为耕地
                        currentBlock.setType(Material.FARMLAND);
                    }
                }
            }
        }

        return true;
    }

    private int getHoeAreaSize(ItemStack hoe) {
        switch (hoe.getType()) {
            case WOODEN_HOE:
                return 1;
            case STONE_HOE:
                return 2;
            case IRON_HOE:
                return 3;
            case GOLDEN_HOE:
                return 4;
            case DIAMOND_HOE:
                return 5;
            case NETHERITE_HOE:
                return 6;
            default:
                return 1;
        }
    }
}
