package fan.cropsprocess.com.listener.tool;

import fan.cropsprocess.com.data.crud.CropDataCrud;
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

public class ScissorsHarvestingListener implements Listener {

    // 用于记录第一次点击和第二次点击的位置
    private Location firstClick = null;
    private Location secondClick = null;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // 获取玩家
        Player player = event.getPlayer();
        // 确保点击的是主手（避免副手）
        if (Objects.equals(event.getHand(), EquipmentSlot.OFF_HAND)) return;

        YamlConfiguration langConfig = CpCore.cpcore.configs.get("language.yml");
        // 获取玩家手持物品
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        // 检查是否为左键点击
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null || clickedBlock.getType().equals(Material.AIR)) return;
            // 判断玩家是否使用剪刀
            if (itemInHand.getType() == Material.SHEARS) {
                event.setCancelled(true);
                RankDataCrud rankDataCrud = CpCore.cpconfig.rankDataCrud;
                int maxLevel = rankDataCrud.existRankData(player.getUniqueId()) ? rankDataCrud.getRankData(player.getUniqueId()).getHarvestLevel() : 0;
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
                    // 将点击的方块、左边和右边三个方块破坏
                    for (int i = -1; i <= 1; i++) {
                        Block blockToChange = world.getBlockAt(x + i, y, z);
                        if (!blockToChange.getType().equals(Material.AIR)) {
                            // 判断是否为作物
                            if (CpCore.cpcore.isCrop(blockToChange)) {
                                CropDataCrud cropDataCrud = CpCore.cpconfig.cropDataCrud;
                                //作物破坏和普通不一样
                                if (cropDataCrud.existCropData(blockToChange.getLocation())) {
                                    CpCore.cpcore.breakCrop(cropDataCrud.getCropData(blockToChange.getLocation()), player.getUniqueId());
                                } else {
                                    blockToChange.breakNaturally(player.getInventory().getItemInMainHand());
                                }
                            }
                            player.getWorld().spawnParticle(Particle.CRIT, blockToChange.getLocation().add(0, 0.5, 0), 10);
                            amt++;
                        }
                    }
                }
                CpCore.cpcore.sendActionBar(player, langConfig.getString("tool.scissors")
                        .replace("%lv%", (maxLevel) + "")
                        .replace("%amt%", (amt) + "")
                        .replace("%maxAmt%", (3 * maxLevel) + "")
                );

            }
        }
        // 检查玩家是否右键点击
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {

            // 判断玩家是否使用剪刀
            if (itemInHand.getType() == Material.SHEARS) {

                event.setCancelled(true); // 防止默认行为
                // 获取点击的方块
                Block block = event.getClickedBlock();

                if (block != null) {
                    // 获取块的类型
                    Material blockType = block.getType();

                    // 如果没有记录第一次点击位置，保存为第一次点击
                    if (firstClick == null) {
                        firstClick = block.getLocation();
                        player.sendMessage("请再点击一次以确定收割区域。");
                        return;
                    }
                    // 保存第二次点击位置
                    secondClick = block.getLocation();

                    // 执行批量收割
                    performHarvest(player);
                    CpCore.cpcore.sendTtile(player, langConfig.getString("magic.scissors"));
                    // 清空第一次和第二次点击记录，准备下一次收割
                    firstClick = null;
                    secondClick = null;

                }
            }
        }
    }

    // 执行批量收割
    private void performHarvest(Player player) {
        if (firstClick == null || secondClick == null) {
            return; // 确保有两个点击位置
        }

        // 获取第一次点击和第二次点击的坐标
        int minX = Math.min(firstClick.getBlockX(), secondClick.getBlockX());
        int maxX = Math.max(firstClick.getBlockX(), secondClick.getBlockX());
        int minZ = Math.min(firstClick.getBlockZ(), secondClick.getBlockZ());
        int maxZ = Math.max(firstClick.getBlockZ(), secondClick.getBlockZ());
        int y = firstClick.getBlockY(); // 假设 Y 轴不变

        // 遍历矩形区域内的方块
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Block block = player.getWorld().getBlockAt(x, y, z);

                // 判断是否为作物
                if (CpCore.cpcore.isCrop(block)) {
                    CropDataCrud cropDataCrud = CpCore.cpconfig.cropDataCrud;
                    //作物破坏和普通不一样
                    if (cropDataCrud.existCropData(block.getLocation())) {
                        CpCore.cpcore.breakCrop(cropDataCrud.getCropData(block.getLocation()), player.getUniqueId());
                    } else {
                        block.breakNaturally(player.getInventory().getItemInMainHand());
                    }
                }
            }
        }
    }


}
