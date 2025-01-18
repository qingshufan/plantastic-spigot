package fan.cropsprocess.com.util;

import fan.cropsprocess.com.Main;
import fan.cropsprocess.com.data.TimeData;
import fan.cropsprocess.com.data.disease.CropDisease;
import fan.cropsprocess.com.data.disease.Disease;
import fan.cropsprocess.com.data.entity.CropData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class CpCore {
    public static Main THIS;
    public static CpCore cpcore;
    public static CpConfig cpconfig;
    public static CpMath cpmath;
    public static Disease disease;
    public static JedisPool pool;
    public Map<String, YamlConfiguration> configs;
    public Map<UUID, TimeData> timemap;

    public Map<UUID, UUID> citizenmap;

    public boolean debug = true;
    private String prefix = "&3[妙趣种植行]&f: ";

    public CpCore() {
        configs = new HashMap<>();
        citizenmap = new HashMap<>();
        timemap = new HashMap<>();
        cpconfig = new CpConfig();
        cpmath = new CpMath();
        disease = new Disease();
    }

    public String getGrow(Block block, CropData cropdata) {
        YamlConfiguration yml = CpCore.cpcore.configs.get(cropdata.getFileName());
        Ageable age = (Ageable) block.getBlockData();

        Set<String> set = yml.getConfigurationSection("grow.info").getKeys(false);
        List<String> list = new ArrayList<String>(set);
        if ((age.getAge() + 1) >= set.size()) {
            return list.get(set.size() - 1);
        }
        return list.get(age.getAge());
    }

    public String chineseBoolean(boolean bool) {
        if (bool) return "是";
        return "否";
    }

    public String getPercent(double d) {
        return cpmath.round(d * 100d) + "%";
    }

    public String convertColorSign(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public List<String> convertColorSign(List<String> textList) {
        List<String> list = new ArrayList<>();
        for (String text : textList) {
            list.add(convertColorSign(text));
        }
        return list;
    }

    public String getBad(CropData cropdata) {
        YamlConfiguration yml = CpCore.cpcore.configs.get(cropdata.getFileName());

        List<String> state = yml.getStringList("state");
        String result = "\n";
        String sign = "\n";
        if (cropdata.isDeath()) {
            return state.get(1);
        }
        if (cropdata.isShortWater()) {
            result += state.get(2) + sign;
        }
        if (cropdata.isShortNutrition()) {
            result += state.get(3) + sign;
        }
        if (cropdata.isFall()) {
            result += state.get(4) + sign;
        }
        if (cropdata.isDisease()) {
            result += state.get(5) + sign;
        }
        if (cropdata.isHigherWater()) {
            result += state.get(6) + sign;
        }
        if (cropdata.isHigherNutrition()) {
            result += state.get(7) + sign;
        }
        if (cropdata.isTemperatureError()) {
            result += state.get(8) + sign;
        }
        for (CropDisease cropDisease : cropdata.getDiseases()) {
            result += cropDisease.getName() + sign;
        }
        if (cropdata.isInsect()) {
            result += state.get(9) + sign;
        }
        if (result.equals("\n")) {
            result = state.get(0);
        }
        return result;
    }


    public void loadRedis() {
        YamlConfiguration deployYml = configs.get("deploy.yml");
        pool = new JedisPool(deployYml.getString("redis.ip"), deployYml.getInt("redis.port"));
    }

    public void loadConfig(String fileName) {
        THIS.getDataFolder().mkdir();
        THIS.saveResource(fileName, debug);
        File configFile = new File(THIS.getDataFolder(), fileName);
        configs.put(configFile.getName(), YamlConfiguration.loadConfiguration(configFile));
    }

    public Ageable getAge(Block block) {
        if (block.getBlockData() instanceof Ageable) {
            Ageable age = (Ageable) block.getBlockData();
            return age;
        }
        return null;
    }

    public int getMaxAge(CropData cropData) {
        YamlConfiguration yml = CpCore.cpcore.configs.get(cropData.getFileName());
        Set<String> set = yml.getConfigurationSection("grow.info").getKeys(false);
        return set.size();
    }

    public void reloadConfig(YamlConfiguration yml, File file) {
        InputStream files = null;
        try {
            files = new FileInputStream(file);
            yml.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(files, StandardCharsets.UTF_8)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean isCrop(ItemStack item) {
        Material material = item.getType();
        switch (material) {
            case WHEAT_SEEDS:
                return true;
            case WHEAT:
                return true;
            case CARROT:
                return true;
            case CARROTS:
                return true;
            case BEETROOT_SEEDS:
                return true;
            case BEETROOTS:
                return true;
            case PUMPKIN_SEEDS:
                return true;
            case PUMPKIN_STEM:
                return true;
            case MELON_SEEDS:
                return true;
            case MELON_STEM:
                return true;
            case POTATO:
                return true;
            case POTATOES:
                return true;
            default:
                break;
        }
        return false;
    }

    public void breakCrop(CropData cropData, UUID uuid) {
        Block block = cropData.getLoc().toLocation().getBlock();
        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, Bukkit.getPlayer(uuid));
        Bukkit.getPluginManager().callEvent(blockBreakEvent); //触发事件
    }

    public void plantCrop(Block block, UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(block, block.getState(), block, player.getInventory().getItemInMainHand(), player, true);
        Bukkit.getPluginManager().callEvent(blockPlaceEvent);
    }

    public boolean isCrop(Block b) {
        if (b == null) return false;
        Material material = b.getType();
        boolean bt = false;
        switch (material) {
            case WHEAT:
                bt = true;
                break;
            case CARROTS:
                bt = true;
                break;
            case BEETROOTS:
                bt = true;
                break;
            case PUMPKIN_STEM:
                bt = true;
                break;
            case MELON_STEM:
                bt = true;
                break;
            case POTATOES:
                bt = true;
                break;
            default:
                break;
        }
        if (!cpconfig.cropDataCrud.existCropData(b.getLocation())) {
            bt = false;
        } else {
            if (cpconfig.cropDataCrud.getCropData(b.getLocation()).isDelete()) {
                bt = false;
            }
        }
        return bt;
    }

    public String getCropName(Material material) {
        switch (material) {
            case WHEAT_SEEDS:
                return "野生小麦";
            case WHEAT:
                return "野生小麦";
            case CARROT:
                return "野生胡萝卜";
            case CARROTS:
                return "野生胡萝卜";
            case BEETROOT_SEEDS:
                return "野生甜菜";
            case BEETROOTS:
                return "野生甜菜";
            case PUMPKIN_SEEDS:
                return "野生南瓜";
            case PUMPKIN_STEM:
                return "野生南瓜";
            case MELON_SEEDS:
                return "野生西瓜";
            case MELON_STEM:
                return "野生西瓜";
            case POTATO:
                return "野生马铃薯";
            case POTATOES:
                return "野生马铃薯";
            default:
                break;
        }
        return "无";
    }

    public void sendConsole(String msg) {
        Bukkit.getConsoleSender().sendMessage(convertColorSign(prefix + msg));
    }

    public void sendMsg(CommandSender sender, String msg) {
        sender.sendMessage(convertColorSign(prefix + msg));
    }

    public void sendMsg(Player sender, String msg) {
        sender.sendMessage(convertColorSign(prefix + msg));
    }

    public void sendTtile(Player sender, String msg) {
        msg = convertColorSign(msg);
        sender.sendTitle(msg.split("\n")[0], msg.split("\n")[1], 10, 70, 20);
    }

    public void sendActionBar(Player sender, String msg) {
        msg = convertColorSign(msg);
        BaseComponent[] baseComponents = new ComponentBuilder()
                .append(msg)
                .create();
        sender.spigot().sendMessage(ChatMessageType.ACTION_BAR, baseComponents);
    }
}
