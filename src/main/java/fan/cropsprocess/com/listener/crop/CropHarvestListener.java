package fan.cropsprocess.com.listener.crop;

import fan.cropsprocess.com.data.crud.RankDataCrud;
import fan.cropsprocess.com.data.entity.CropData;
import fan.cropsprocess.com.data.entity.RankData;
import fan.cropsprocess.com.util.CpCore;
import fan.cropsprocess.com.util.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CropHarvestListener implements Listener {
    private static StringBuilder getProgressBar(double percentageChange) {
        int progressBarLength = 50; // 进度条长度
        int filledLength = (int) (progressBarLength * (percentageChange / 100));
        StringBuilder progressBar = new StringBuilder();

        // 填充进度条
        for (int i = 0; i < filledLength; i++) {
            progressBar.append("&a|"); // 已填充部分用绿色表示
        }
        for (int i = filledLength; i < progressBarLength; i++) {
            progressBar.append("&7|"); // 未填充部分用灰色表示
        }
        return progressBar;
    }

    @EventHandler
    public void BlockBreak(BlockBreakEvent e) {
        if (e.isCancelled()) {
            return;
        }
        Block block = e.getBlock();
        Player p = e.getPlayer();
        if (CpCore.cpcore.isCrop(block)) {
            if (CpCore.cpconfig.cropDataCrud.existCropData(block.getLocation())) {

                CropData cropdata = CpCore.cpconfig.cropDataCrud.getCropData(block.getLocation());

                if (cropdata.isDelete()) {
                    CpCore.cpconfig.cropDataCrud.delCropData(block.getLocation());
                    block.setType(Material.AIR);
                    return;
                }
                if (cropdata.isDeath()) {
                    CpCore.cpconfig.cropDataCrud.delCropData(block.getLocation());
                    block.setType(Material.AIR);
                    return;
                }
                YamlConfiguration lang = CpCore.cpcore.configs.get("language.yml");
                CpCore.cpcore.sendActionBar(p, lang.getString("cropBreakMsg")
                        .replace("%name%", cropdata.getName()));

                e.setDropItems(false);
                //检测是否成熟
                Ageable ageable = CpCore.cpcore.getAge(block);
                if (ageable == null) { //方块不是作物
                    CpCore.cpconfig.cropDataCrud.delCropData(block.getLocation());
                    block.setType(Material.AIR);
                    return;
                }
                //作物未成熟 无收获
                if (ageable.getAge() + 1 < CpCore.cpcore.getMaxAge(cropdata)) {
                    CpCore.cpconfig.cropDataCrud.delCropData(block.getLocation());
                    block.setType(Material.AIR);
                    return;
                }

                //作物成熟
                RankDataCrud rankDataCrud = CpCore.cpconfig.rankDataCrud;
                RankData rankData = rankDataCrud.existRankData(cropdata.getUuid()) ? rankDataCrud.getRankData(cropdata.getUuid()) : new RankData();

                YamlConfiguration cropConfig = CpCore.cpcore.configs.get(cropdata.getFileName());
                List<ItemStack> dropItemList = new ArrayList<>();
                ItemStack seed = ItemUtils.normalItem(cropConfig.getString("name"),
                        cropConfig.getStringList("lore"),
                        Material.valueOf(cropConfig.getString("itemType")));
                String amtRange = cropConfig.getString("grow.product.seed.amtRange");
                seed.setAmount(CpCore.cpmath.randomInteger(amtRange));
                dropItemList.add(seed); //种子产物

                List<String> cropLores = new ArrayList<>();
                cropLores.addAll(cropConfig.getStringList("grow.product.crop.item.lore")); //固定描述
                List<String> cropExtraList = cropConfig.getStringList("grow.product.crop.item.extra");
                String regex = "\\{[-+]?\\d*\\.?\\d+(?:[eE][-+]?\\d+)?-[-+]?\\d*\\.?\\d+(?:[eE][-+]?\\d+)?\\}";
                boolean showOnce = false;
                CpCore.cpcore.sendMsg(p, "&e收获结算：");
                for (String extraStr : cropExtraList) {
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(extraStr);
                    StringBuffer result = new StringBuffer();
                    if (matcher.find()) {
                        String range = matcher.group(); // 获取大括号内的内容
                        String parts[] = range.substring(1, range.length() - 1).split("-");
                        double originalMin = Double.parseDouble(parts[0]);
                        double originalMax = Double.parseDouble(parts[1]);
                        double min = originalMin;
                        double max = originalMax;
                        // 科技树提升增加产率5%
                        double techBoost = rankData.getYieldLevel() * 0.05 * (max - min);
                        min += techBoost;
                        min = Math.min(min, max); // 防止越界
                        if (!showOnce) {
                            // 计算科技树增益的百分比变化
                            double techBoostPercentage = (techBoost / (originalMax - originalMin) * 100);
                            String techBoostPercentageStr = String.format("%.2f", techBoostPercentage);

                            // 创建进度条
                            StringBuilder techBoostProgressBar = getProgressBar(techBoostPercentage);

                            // 发送消息
                            CpCore.cpcore.sendMsg(p, "&a科技树增益: &f" + techBoostProgressBar.toString() + " &f+" + techBoostPercentageStr + "%");
                        }

                        // 患一次病最高降低产率10%
                        double diseaseImpact = cropdata.getDiseases().size() * 0.1 * (max - min);
                        max -= diseaseImpact;
                        min -= diseaseImpact;
                        if (!showOnce) {
                            // 计算患病影响的百分比变化
                            double diseaseImpactPercentage = (diseaseImpact / (originalMax - originalMin) * 100);
                            String diseaseImpactPercentageStr = String.format("%.2f", diseaseImpactPercentage);

                            // 创建进度条
                            StringBuilder diseaseProgressBar = getProgressBar(diseaseImpactPercentage);

                            // 发送消息
                            CpCore.cpcore.sendMsg(p, "&a患病影响: &f" + diseaseProgressBar.toString() + " &f-" + diseaseImpactPercentageStr + "%");
                        }

                        long timeSub = System.currentTimeMillis() - cropdata.getMatureTime(); // 收获时间影响
                        // 定义一个衰减系数，可以根据需要调整这个值
                        double decayFactor = 0.00001;
                        // 使用指数衰减函数来调整最大和最小产量
                        double originalMaxAfterDisease = max;
                        max = max * Math.exp(-decayFactor * timeSub);
                        min = min * Math.exp(-decayFactor * timeSub);

                        if (!showOnce) {
                            // 计算百分比变化
                            double percentageChange = ((originalMaxAfterDisease - max) / originalMaxAfterDisease * 100);
                            String percentageChangeStr = String.format("%.2f", percentageChange);

                            // 创建进度条
                            StringBuilder progressBar = getProgressBar(percentageChange);

                            // 发送消息
                            CpCore.cpcore.sendMsg(p, "&a收获时机影响: &f" + progressBar.toString() + " &f" + percentageChangeStr + "%");
                        }

                        min = min < 0 ? Math.random() : min; // 扣到负就随机0-1
                        max = max < 0 ? Math.random() : max; // 扣到负就随机0-1
                        max = Math.max(min, max);

                        double randomValue = CpCore.cpmath.randomDouble(min, max);

                        matcher.appendReplacement(result, String.format("%.2f", randomValue)); // 替换为随机数，保留两位小数
                    }
                    matcher.appendTail(result); // 将剩余部分添加到结果中
                    cropLores.add(result.toString());
                    showOnce = true;
                }
                ItemStack crop = ItemUtils.normalItem(cropConfig.getString("grow.product.crop.item.name"),
                        cropLores,
                        Material.valueOf(cropConfig.getString("grow.product.crop.item.itemType")));
                dropItemList.add(crop); //种子产物

                Location location = cropdata.getLoc().toLocation();
                location.add(0, 0.5, 0); //坐标高一些
                for (ItemStack itemStack : dropItemList) {
                    e.getBlock().getWorld().dropItemNaturally(location, itemStack); //随机范围丢种子
                }

                CpCore.cpconfig.cropDataCrud.delCropData(block.getLocation());
                block.setType(Material.AIR);
            }
        }
    }


}
