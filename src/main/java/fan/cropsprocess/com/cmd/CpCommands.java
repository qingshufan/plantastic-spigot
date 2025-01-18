package fan.cropsprocess.com.cmd;

import fan.cropsprocess.com.data.TimeData;
import fan.cropsprocess.com.data.crud.CropDataCrud;
import fan.cropsprocess.com.data.entity.CropData;
import fan.cropsprocess.com.inventory.GuiLoader;
import fan.cropsprocess.com.util.CpCore;
import fan.cropsprocess.com.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class CpCommands implements CommandExecutor {

    @Override
    // cp spawn citizen
    public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
        if (!sender.isOp()) {
            return false;
        }
        if (args.length == 4) {

            if (args[0].equalsIgnoreCase("time")) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    int period = 0, delay = 0;
                    try {
                        period = Integer.parseInt(args[1]);
                        delay = Integer.parseInt(args[2]);
                        String show = args[3];
                        CpCore.cpcore.timemap.put(p.getUniqueId(), new TimeData(period, delay, show));
                        CpCore.cpcore.sendMsg(sender, "请右键一个作物");
                    } catch (Exception e) {
                        CpCore.cpcore.sendMsg(sender, "时间必须为数字");
                    }
                    return true;
                }
            }
        }
        if (args.length == 3) {


            if (args[0].equalsIgnoreCase("spawn")) {
                final String name = args[1] + ".yml";
                Player p = Bukkit.getPlayer(args[2]);
                if (p == null) {
                    CpCore.cpcore.sendMsg(sender, "玩家不存在");
                    return true;
                }
                if (!CpCore.cpcore.configs.containsKey(name)) {
                    CpCore.cpcore.sendMsg(sender, "村民不存在");
                    return true;
                }
                Entity en2 = p.getWorld().spawnEntity(p.getLocation().add(0, 1, 0), EntityType.VILLAGER);
                final UUID uuid = en2.getUniqueId();
                CpCore.cpcore.citizenmap.put(en2.getUniqueId(), en2.getUniqueId());
                en2.setCustomNameVisible(true);
                YamlConfiguration yml = CpCore.cpcore.configs.get(name);
                new BukkitRunnable() {
                    int amount = 0;

                    public void run() {
                        try {

                            YamlConfiguration yml = CpCore.cpcore.configs.get(name);
                            List<String> story = yml.getStringList("story");
                            Entity en = Bukkit.getEntity(uuid);
                            if (en == null || en.isDead()) {
                                this.cancel();
                                return;
                            }
                            if (amount >= story.size()) {
                                ItemStack reward = ItemUtils.normalItem(yml.getString("item.name"),
                                        yml.getStringList("item.lore"), Material.valueOf(yml.getString("item.type")));
                                Item itementity = en.getWorld().dropItem(en.getLocation().add(0, 2, 0), reward);
                                itementity.setCustomName(yml.getString("item.name"));
                                itementity.setCustomNameVisible(true);
                                CpCore.cpcore.citizenmap.remove(en.getUniqueId());
                                en.remove();
                                this.cancel();
                                return;
                            }
                            en.setCustomName(story.get(amount));
                            amount++;
                        } catch (Exception e) {
                            CpCore.cpcore.sendConsole("村民异常");
                        }
                    }
                }.runTaskTimer(CpCore.THIS, 0, 20L * yml.getInt("delay"));
            }
        }
        if (args.length == 1) {
            //加速所有作物成长
            if (args[0].equalsIgnoreCase("grow")) {
                CropDataCrud cropDataCrud = CpCore.cpconfig.cropDataCrud;
                for (CropData cropData : cropDataCrud.allCropData()) {
                    Block block = cropData.getLoc().toLocation().getBlock();
                    Ageable ageable = (Ageable) block.getBlockData();
                    ageable.setAge(CpCore.cpcore.getMaxAge(cropData));
                    block.setBlockData(ageable);
                    block.applyBoneMeal(BlockFace.UP);
                }
                return true;
            }
            //加速时间
            if (args[0].equalsIgnoreCase("time")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.getWorld().getTime() >= 23800 || player.getWorld().getTime() == 0) {
                                player.getWorld().setTime(0);
                                this.cancel();
                                return;
                            }
                            player.getWorld().setTime(player.getWorld().getTime() + 200);
                            break;
                        }
                    }
                }.runTaskTimer(CpCore.THIS, 0, 1);
                return true;
            }
            //debug
            if (args[0].equalsIgnoreCase("gui")) {
                Inventory inv = GuiLoader.loader("skillTree.hub").getInventory();
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    player.openInventory(inv);
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("show")) {
                for (CropData cd : CpCore.cpconfig.cropDataCrud.allCropData()) {
                    Location loc = cd.getLoc().toLocation();
                    if (cd.getPeriod() == 0 && cd.getDelay() == 0) {
                        continue;
                    }
                    if (cd.getPeriod() <= 0 || cd.getDelay() <= 0) {
                        continue;
                    }
                    final int period = cd.getPeriod();
                    final int delay = cd.getDelay();
                    final Location loc2 = loc.clone();
                    new BukkitRunnable() {
                        int i = 0;

                        @Override
                        public void run() {
                            CropData cd = CpCore.cpconfig.cropDataCrud.getCropData(loc2);
                            try {
                                if (period * i >= delay) {
                                    this.cancel();
                                    return;
                                }
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fsk down " + cd.getShow());
                                Ageable age = (Ageable) loc2.getBlock().getBlockData();
                                if ((age.getAge() + 1) > age.getMaximumAge()) {
                                    age.setAge(0);
                                } else {
                                    age.setAge(age.getAge() + 1);
                                }
                                loc2.getBlock().setBlockData(age);
                                i++;
                            } catch (Exception e) {
                                CpCore.cpcore.sendMsg(Bukkit.getConsoleSender(), "延迟展示出现问题!");
                                e.printStackTrace();
                                this.cancel();
                            }
                        }

                    }.runTaskTimer(CpCore.THIS, 0, cd.getPeriod() * 20L);
                }
            }
            if (args[0].equalsIgnoreCase("reload")) {

                CpCore.cpcore.configs.clear();
                for (File configFile : CpCore.THIS.getDataFolder().listFiles()) {
                    if (configFile.getName().endsWith(".yml")) {
                        YamlConfiguration yml = YamlConfiguration.loadConfiguration(configFile);
                        CpCore.cpcore.reloadConfig(yml, configFile);
                        CpCore.cpcore.loadConfig(configFile.getName());
                    }
                }
                if (CpCore.cpconfig.ymlFile.listFiles() != null)
                    for (File configFile : CpCore.cpconfig.ymlFile.listFiles()) {
                        if (configFile.getName().endsWith(".yml")) {
                            YamlConfiguration yml = YamlConfiguration.loadConfiguration(configFile);
                            CpCore.cpcore.reloadConfig(yml, configFile);
                        }
                    }
                CpCore.cpconfig.loadYmlFile();

                if (CpCore.cpconfig.citizenFile.listFiles() != null)
                    for (File configFile : CpCore.cpconfig.citizenFile.listFiles()) {
                        if (configFile.getName().endsWith(".yml")) {
                            YamlConfiguration yml = YamlConfiguration.loadConfiguration(configFile);
                            CpCore.cpcore.reloadConfig(yml, configFile);
                        }
                    }

                CpCore.cpconfig.loadCitizenFile();

                CpCore.cpcore.sendMsg(sender, "重载完毕");
                return true;
            }
        }

        return false;
    }

}
