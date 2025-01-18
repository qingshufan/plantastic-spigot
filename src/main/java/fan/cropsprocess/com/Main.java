package fan.cropsprocess.com;

import fan.cropsprocess.com.cmd.CpCommands;
import fan.cropsprocess.com.cmd.MarketCommands;
import fan.cropsprocess.com.listener.MarketListener;
import fan.cropsprocess.com.listener.base.NoClickInventoryListener;
import fan.cropsprocess.com.listener.crop.CropGrowListener;
import fan.cropsprocess.com.listener.crop.CropHarvestListener;
import fan.cropsprocess.com.listener.crop.CropPlantListener;
import fan.cropsprocess.com.listener.crop.click.CropClickListener;
import fan.cropsprocess.com.listener.crop.click.CropDelayListener;
import fan.cropsprocess.com.listener.skill.tree.SkillTreeBookListener;
import fan.cropsprocess.com.listener.skill.tree.SkillTreeGotoListener;
import fan.cropsprocess.com.listener.skill.tree.SkillTreeSeedListener;
import fan.cropsprocess.com.listener.skill.tree.SkillTreeToolListener;
import fan.cropsprocess.com.listener.tool.*;
import fan.cropsprocess.com.util.CpCore;
import fan.cropsprocess.com.weather.WeatherManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.UUID;

public class Main extends JavaPlugin {
    @Getter
    private static ClassPathXmlApplicationContext context;

    public void onEnable() {
        //spring
        // 获取当前插件的类加载器
        ClassLoader pluginClassLoader = this.getClass().getClassLoader();
        // 设置线程上下文类加载器为插件的类加载器
        Thread.currentThread().setContextClassLoader(pluginClassLoader);
        context = new ClassPathXmlApplicationContext("spring/spring.xml");

        CpCore.THIS = this;
        CpCore.cpcore = new CpCore();
        CpCore.cpcore.loadConfig("config.yml");
        CpCore.cpcore.loadConfig("language.yml");
        CpCore.cpcore.loadConfig("gui.yml");
        CpCore.cpcore.loadConfig("skillTree.yml");
        CpCore.cpcore.loadConfig("market.yml");
        CpCore.cpcore.loadConfig("deploy.yml");
        CpCore.cpcore.loadConfig("农作物配置/普通小麦.yml");
        CpCore.cpcore.loadConfig("农作物配置/紫玫瑰二号.yml");
        CpCore.cpcore.loadConfig("农作物配置/小偃六号.yml");

        CpCore.cpcore.loadRedis();
        CpCore.cpconfig.loadYmlFile();
        CpCore.cpconfig.loadCitizenFile();

        CpCore.cpconfig.loadCropData();


        Bukkit.getPluginCommand("cp").setExecutor(new CpCommands());
        Bukkit.getPluginCommand("market").setExecutor(new MarketCommands());

        Bukkit.getPluginManager().registerEvents(new CropClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new CropDelayListener(), this);
        Bukkit.getPluginManager().registerEvents(new CropGrowListener(), this);
        Bukkit.getPluginManager().registerEvents(new CropHarvestListener(), this);
        Bukkit.getPluginManager().registerEvents(new CropPlantListener(), this);
        Bukkit.getPluginManager().registerEvents(new NoClickInventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new MarketListener(), this);
        Bukkit.getPluginManager().registerEvents(new WateringListener(), this);
        Bukkit.getPluginManager().registerEvents(new NuturingListener(), this);
        Bukkit.getPluginManager().registerEvents(new ShovelAreaDestroyListener(), this);
        Bukkit.getPluginManager().registerEvents(new HoeTillingListener(), this);
        Bukkit.getPluginManager().registerEvents(new BatchPlantingListener(), this);
        Bukkit.getPluginManager().registerEvents(new ScissorsHarvestingListener(), this);
        Bukkit.getPluginManager().registerEvents(new GreenhouseEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new SkillTreeGotoListener(), this);
        Bukkit.getPluginManager().registerEvents(new SkillTreeSeedListener(), this);
        Bukkit.getPluginManager().registerEvents(new SkillTreeToolListener(), this);
        Bukkit.getPluginManager().registerEvents(new SkillTreeBookListener(), this);
        //天气改变线程
        new WeatherManager().startWeatherCycle();

    }


    public void onDisable() {
        for (UUID uuid : CpCore.cpcore.citizenmap.values()) {
            if (Bukkit.getEntity(uuid) != null) {
                Bukkit.getEntity(uuid).remove();
            }
        }
        CpCore.cpconfig.saveCropData();
        context.close();
    }
}
