package fan.cropsprocess.com.listener.tool;

import fan.cropsprocess.com.data.crud.CropDataCrud;
import fan.cropsprocess.com.data.crud.RankDataCrud;
import fan.cropsprocess.com.data.entity.CropData;
import fan.cropsprocess.com.util.CpCore;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NuturingListener implements Listener {

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        YamlConfiguration langConfig = CpCore.cpcore.configs.get("language.yml");

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && item != null) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null || clickedBlock.getType().equals(Material.AIR)) return;
            // 判断玩家手持的是奶桶
            if (isWaterBottle(item)) {
                RankDataCrud rankDataCrud = CpCore.cpconfig.rankDataCrud;
                CropDataCrud cropDataCrud = CpCore.cpconfig.cropDataCrud;
                int maxLevel = rankDataCrud.existRankData(player.getUniqueId()) ? rankDataCrud.getRankData(player.getUniqueId()).getNutritionLevel() : 0;
                if (maxLevel == 0) return;
                int sign = clickedBlock.getZ() - player.getLocation().getZ() < 0 ? -1 : 1;
                int amt = 0;
                boolean isCrop = false;
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
                        Location blockToChangeLocation = blockToChange.getLocation();
                        //增加养分
                        if (cropDataCrud.existCropData(blockToChangeLocation)) {
                            CropData cropData = cropDataCrud.getCropData(blockToChangeLocation);
                            YamlConfiguration yml = CpCore.cpcore.configs.get(cropData.getFileName());
                            Ageable age = CpCore.cpcore.getAge(blockToChange);
                            if (age == null) {
                                continue;
                            }
                            Set<String> set = yml.getConfigurationSection("grow.info").getKeys(false);
                            List<String> list = new ArrayList<String>(set);
                            ConfigurationSection cs = yml.getConfigurationSection("grow.info." + list.get(age.getAge()));
                            cropData.setNutrition(cropData.getNutrition() + cs.getInt("powder"));
                            int resultNutrition = CpCore.cpmath.satisfy(cropData.getNutrition(), cs.getString("nutrition"), "-");
                            if (resultNutrition != 0) {
                                if (resultNutrition == 1) {
                                    cropData.setShortNutrition(true);
                                    cropData.setHigherNutrition(false);
                                } else {
                                    cropData.setHigherNutrition(true);
                                    cropData.setShortNutrition(false);
                                }
                                event.setCancelled(true);
                            } else {
                                cropData.setShortNutrition(false);
                                cropData.setHigherNutrition(false);
                            }
                            isCrop = true;
                            CpCore.cpconfig.cropDataCrud.setCropData(cropData); //及时写入
                            player.getWorld().spawnParticle(Particle.SNOWBALL, blockToChange.getLocation().add(0, 0.5, 0), 10);
                            amt++;
                        }
                    }
                }
                if (!isCrop) return;
                event.setCancelled(true);
                item.setAmount(item.getAmount() - 1);
                player.getInventory().addItem(new ItemStack(Material.BUCKET));
                player.updateInventory();
                CpCore.cpcore.sendActionBar(player, langConfig.getString("tool.nutrition")
                        .replace("%lv%", (maxLevel) + "")
                        .replace("%amt%", (amt) + "")
                        .replace("%maxAmt%", (3 * maxLevel) + "")
                );
            }
        }
    }

    // 检查玩家是否手持奶桶
    private boolean isWaterBottle(ItemStack item) {
        if (item.getType().equals(Material.MILK_BUCKET)) {
            return true;
        }
        return false;
    }
}
