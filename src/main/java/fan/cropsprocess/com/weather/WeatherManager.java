package fan.cropsprocess.com.weather;

import fan.cropsprocess.com.data.crud.CropDataCrud;
import fan.cropsprocess.com.data.entity.CropData;
import fan.cropsprocess.com.data.entity.LocData;
import fan.cropsprocess.com.util.CpCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WeatherManager {
    int testIndex = 1;
    private String currentWeather = "晴朗";   //初始天气为晴朗
    private Player player;

    public void startWeatherCycle() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // 判断是否为新一天
                long worldTime = Bukkit.getServer().getWorlds().get(0).getTime();
//                Bukkit.getConsoleSender().sendMessage("" + worldTime);
                if (worldTime >= 0 && worldTime < 100) { // Minecraft 时间从0到24000
                    CropDataCrud cropDataCrud = CpCore.cpconfig.cropDataCrud;
                    if (cropDataCrud.allCropData().isEmpty()) {
                        return;
                    }
                    // 新一天的清晨，随机改变天气
                    randomizeWeather();
                    Bukkit.broadcastMessage("§a新一天开始了！当前天气: " + currentWeather);
                }
            }
        }.runTaskTimer(CpCore.THIS, 0, 100); // 每5秒检查一次 (100 tick)
    }

    private void randomizeWeather() {
        String[] weathers = {"晴朗", "大风", "暴雨", "高温", "蝗虫灾害"};
        int index = new Random().nextInt(weathers.length);
        if (testIndex > 4) testIndex = 1;
        this.currentWeather = weathers[testIndex++];
        // 在游戏中模拟天气影响（可选）
        switch (currentWeather) {
            case "暴雨":
                Bukkit.getWorld("world").setStorm(true);    //下雪
                Bukkit.getWorld("world").setThundering(false);    //打雷
                doStorm();
                break;
            case "晴朗":
                Bukkit.getWorld("world").setStorm(false);
                Bukkit.getWorld("world").setThundering(false);
                break;
            case "大风":
                Bukkit.getWorld("world").setStorm(false);
                Bukkit.getWorld("world").setThundering(false);
                doStrongWind();
                break;
            case "高温":
                Bukkit.getWorld("world").setStorm(false); // 晴朗但伴随效果
                doHighTemperature();
                break;
            case "蝗虫灾害":
                Bukkit.getWorld("world").setStorm(false);
                Bukkit.getWorld("world").setThundering(false);
                doInsect();
                break;
        }
    }


    public void doStrongWind() {
        CropDataCrud cropDataCrud = CpCore.cpconfig.cropDataCrud;
        int amt = 0;
        for (CropData cropData : cropDataCrud.allCropData()) {
            LocData loc = cropData.getLoc();
            Block b = loc.toLocation().getWorld().getBlockAt((int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
            Ageable age = CpCore.cpcore.getAge(b);
            YamlConfiguration yml = CpCore.cpcore.configs.get(cropData.getFileName());
            Set<String> set = yml.getConfigurationSection("grow.info").getKeys(false);
            List<String> list = new ArrayList<String>(set);
            ConfigurationSection cs = yml.getConfigurationSection("grow.info." + list.get(age.getAge()));
            double fallChance = cs.getDouble("fallChance");
            if (cropData.isHigh()) {
                fallChance += 0.3; //株高更容易倒伏
            }
            if (CpCore.cpmath.random(fallChance)) {
                cropData.setFall(true);
                cropDataCrud.setCropData(cropData);
                final Location location = cropData.getLoc().toLocation();
                //执行5秒的特效
                new BukkitRunnable() {
                    int showAmt = 5;

                    @Override
                    public void run() {
                        if (showAmt <= 0) {
                            this.cancel();
                            return;
                        }
                        location.getWorld().spawnParticle(Particle.CLOUD, location.add(0, 0.5, 0), 20);
                        showAmt--;
                    }
                }.runTaskTimer(CpCore.THIS, 0, 20);
                amt++;
            }
        }
        YamlConfiguration langConfig = CpCore.cpcore.configs.get("language.yml");
        for (Player player : Bukkit.getOnlinePlayers()) {
            CpCore.cpcore.sendTtile(player, langConfig.getString("weather.wind")
                    .replace("%amt%", amt + "")
            );
            CpCore.cpcore.sendMsg(player, langConfig.getString("weather.wind")
                    .replace("%amt%", amt + "")
                    .replace("\n", "")
            );
        }

    }


    public void doStorm() {
        CropDataCrud cropDataCrud = CpCore.cpconfig.cropDataCrud;

        int amt = 0;
        //遍历每个作物
        for (CropData cropData : cropDataCrud.allCropData()) {
            //下雨使得每个作物水分和养分增加5
            cropData.setWater(cropData.getWater() + 5);
            cropData.setNutrition(cropData.getNutrition() + 5);
            cropDataCrud.setCropData(cropData);
            amt++;
        }

        YamlConfiguration langConfig = CpCore.cpcore.configs.get("language.yml");
        for (Player player : Bukkit.getOnlinePlayers()) {
            CpCore.cpcore.sendMsg(player, langConfig.getString("weather.storm")
                    .replace("%amt%", amt + "")
            );
        }


    }

    public void doInsect() {
        CropDataCrud cropDataCrud = CpCore.cpconfig.cropDataCrud;
        int amt = 0;
        for (CropData cropData : cropDataCrud.allCropData()) {
            if (!cropData.isInsect()) {
                cropData.setInsect(true);
                cropDataCrud.setCropData(cropData);
                final Location location = cropData.getLoc().toLocation();
                //执行5秒的特效
                new BukkitRunnable() {
                    int showAmt = 5;

                    @Override
                    public void run() {
                        if (showAmt <= 0) {
                            this.cancel();
                            return;
                        }
                        location.getWorld().spawnParticle(Particle.COMPOSTER, location, 1);
                        showAmt--;
                    }
                }.runTaskTimer(CpCore.THIS, 0, 20);
                amt++;
            }
        }
        YamlConfiguration langConfig = CpCore.cpcore.configs.get("language.yml");
        for (Player player : Bukkit.getOnlinePlayers()) {
            CpCore.cpcore.sendTtile(player, langConfig.getString("weather.insect")
                    .replace("%amt%", amt + "")
            );
            CpCore.cpcore.sendMsg(player, langConfig.getString("weather.insect")
                    .replace("%amt%", amt + "")
                    .replace("\n", "")
            );
        }
    }

    public void doHighTemperature() {
        CropDataCrud cropDataCrud = CpCore.cpconfig.cropDataCrud;
        int amt = 0;
        for (CropData cropData : cropDataCrud.allCropData()) {
            if (!cropData.isOwnGreenHouse()) {
                cropData.setTemperatureError(true);
                cropDataCrud.setCropData(cropData);
                final Location location = cropData.getLoc().toLocation();
                //执行5秒的火焰效果
                new BukkitRunnable() {
                    int showAmt = 5;

                    @Override
                    public void run() {
                        if (showAmt <= 0) {
                            this.cancel();
                            return;
                        }
                        location.getWorld().spawnParticle(Particle.FLAME, location.add(0, 0.5, 0), 20);
                        showAmt--;
                    }
                }.runTaskTimer(CpCore.THIS, 0, 20);
                amt++;
            }

        }
        YamlConfiguration langConfig = CpCore.cpcore.configs.get("language.yml");
        for (Player player : Bukkit.getOnlinePlayers()) {
            CpCore.cpcore.sendTtile(player, langConfig.getString("weather.highTemperature")
                    .replace("%amt%", amt + "")
            );
            CpCore.cpcore.sendMsg(player, langConfig.getString("weather.highTemperature")
                    .replace("%amt%", amt + "")
                    .replace("\n", "")
            );
        }
    }

    public String getCurrentWeather() {         //获取天气
        return currentWeather;
    }
}
