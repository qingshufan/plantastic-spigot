package fan.cropsprocess.com.listener.crop;

import fan.cropsprocess.com.data.crud.RankDataCrud;
import fan.cropsprocess.com.data.disease.CropDisease;
import fan.cropsprocess.com.data.entity.CropData;
import fan.cropsprocess.com.data.entity.RankData;
import fan.cropsprocess.com.data.soil.SoilType;
import fan.cropsprocess.com.util.CpCore;
import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CropGrowListener implements Listener {
    @EventHandler
    public void grow(BlockGrowEvent e) {
        Block block = e.getBlock();
        if (CpCore.cpcore.isCrop(block)) {
            CropData cropdata = CpCore.cpconfig.cropDataCrud.getCropData(block.getLocation());

            YamlConfiguration yml = CpCore.cpcore.configs.get(cropdata.getFileName());

            Ageable age = (Ageable) block.getBlockData();

            Set<String> set = yml.getConfigurationSection("grow.info").getKeys(false);
            int dh = age.getAge();

            if ((age.getAge() + 1) >= set.size()) {
                return;
            }
            if (cropdata.isDeath()) {
                e.setCancelled(true);
                return;
            }
            block.getWorld().playEffect(block.getLocation(), Effect.VILLAGER_PLANT_GROW, 10);
            //测试
            if (!cropdata.isOp()) {

                RankDataCrud rankDataCrud = CpCore.cpconfig.rankDataCrud;
                RankData rankData = rankDataCrud.existRankData(cropdata.getUuid()) ? rankDataCrud.getRankData(cropdata.getUuid()) : new RankData();
                //处理死亡概率
                double deathChance = getDeathChance(cropdata, rankData);
                deathChance = deathChance < 0 ? 0 : deathChance;
                deathChance = deathChance > 1 ? 1 : deathChance;
                //处理死亡惩罚
                if (CpCore.cpmath.random(deathChance)) {
                    cropdata.setDeath(true);
                    CpCore.cpconfig.cropDataCrud.setCropData(cropdata);
                    e.setCancelled(true);
                    return;
                }


                List<String> list = new ArrayList<String>(set);
                ConfigurationSection cs = yml.getConfigurationSection("grow.info." + list.get(dh));

                //处理水分
                int resultWater = CpCore.cpmath.satisfy(cropdata.getWater(), cs.getString("water"), "-");
                if (resultWater != 0) {
                    if (resultWater == 1) {
                        cropdata.setShortWater(true);
                        cropdata.setHigherWater(false);
                    } else {
                        cropdata.setHigherWater(true);
                        cropdata.setShortWater(false);
                    }
                    CpCore.cpconfig.cropDataCrud.setCropData(cropdata); //及时写入
                    e.setCancelled(true);
                    return;
                }
                //处理养分
                int resultNutrition = CpCore.cpmath.satisfy(cropdata.getNutrition(), cs.getString("nutrition"), "-");
                if (resultNutrition != 0) {
                    if (resultNutrition == 1) {
                        cropdata.setShortNutrition(true);
                        cropdata.setHigherNutrition(false);
                    } else {
                        cropdata.setHigherNutrition(true);
                        cropdata.setShortNutrition(false);
                    }
                    CpCore.cpconfig.cropDataCrud.setCropData(cropdata); //及时写入
                    e.setCancelled(true);
                    return;
                }

                //倒伏
                if (!cropdata.isFall() && CpCore.cpmath.random(cs.getDouble("fallChance"))) {
                    cropdata.setFall(true);
                }

                //患病
                //基础患病率
                double sickChance = cs.getDouble("diseaseChance");
                //处理科技树
                sickChance -= rankData.getPrevalenceLevel() * 0.05; //每级减少5%的患病率
                sickChance = sickChance < 0 ? 0 : sickChance;
                if (CpCore.cpmath.random(sickChance)) {
                    // 随机选择一种疾病
                    CropDisease disease = CpCore.disease.getRandomDisease();
                    cropdata.addDisease(disease);
                    // 设置作物为患病状态
                    cropdata.setDisease(true);
                    // 根据不同的疾病类型对作物进行处理
                    CpCore.disease.handleDiseaseEffect(cropdata, disease);
                }

                cropdata.setWater(0);
                cropdata.setNutrition(0);
                cropdata.setShortNutrition(true);
                cropdata.setShortWater(true);
                CpCore.cpconfig.cropDataCrud.setCropData(cropdata); //及时写入
            }


            e.setCancelled(true);
            if ((age.getAge() + 1) == set.size()) { //成熟
                age.setAge(age.getMaximumAge());
                cropdata.setMatureTime(System.currentTimeMillis()); //设置成熟时间
            } else {
                age.setAge(age.getAge() + 1);
            }
            block.setBlockData(age);


        }
    }

    private double getDeathChance(CropData cropdata, RankData rankData) {
        double deathChance = 0;
        //患病小概率死亡
        if (cropdata.isDisease()) {
            deathChance += cropdata.getDiseases().size() * 0.01;
        }
        if (cropdata.isShortNutrition() || cropdata.isHigherNutrition()) {
            double base = 0.3;
            if (cropdata.getSoilType().equals(SoilType.SALINE)) base = 0.1; //盐性土地可以少一些营养
            deathChance += base - (rankData.getResistanceLevel()) * 0.01; //抗逆性等级抵消1%
        }
        if (cropdata.isShortWater() || cropdata.isHigherWater()) {
            double base = 0.3;
            if (cropdata.getSoilType().equals(SoilType.DRY)) base = 0.8; //干性土壤对水分更加敏感
            deathChance += base - (rankData.getResistanceLevel()) * 0.01; //抗逆性等级抵消1%
        }
        if (cropdata.isTemperatureError()) {
            deathChance += 0.3 - (rankData.getResistanceLevel()) * 0.02;
            ; //抗逆性等级抵消1%
        }
        if (cropdata.isInsect()) {//蝗虫影响
            double base = 0.5;
            deathChance += base - (rankData.getResistanceLevel()) * 0.05; //抗逆性等级抵消5%
        }
        return deathChance;
    }

}
