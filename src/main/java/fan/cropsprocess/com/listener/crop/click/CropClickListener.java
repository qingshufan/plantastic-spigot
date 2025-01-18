package fan.cropsprocess.com.listener.crop.click;

import fan.cropsprocess.com.data.entity.CropData;
import fan.cropsprocess.com.util.CpCore;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CropClickListener implements Listener {

    @EventHandler
    public void InteractBlock(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        //主手右键
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getHand().equals(EquipmentSlot.HAND)) {
            Block block = e.getClickedBlock();
            if (CpCore.cpconfig.cropDataCrud.existCropData(block.getLocation())) {
                CropData cropdata = CpCore.cpconfig.cropDataCrud.getCropData(block.getLocation());
                if (cropdata.isDelete()) {
                    return;
                }
                //空手右键查看信息
                //空手右键查看信息
                if (e.getItem() == null || e.getItem().getType().equals(Material.AIR)) {
                    YamlConfiguration yml = CpCore.cpcore.configs.get(cropdata.getFileName());

                    ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
                    BookMeta bookMeta = (BookMeta) book.getItemMeta();
                    bookMeta.setAuthor(p.getName());
                    bookMeta.setTitle("作物");
                    for (String page : yml.getConfigurationSection("page").getKeys(false)) {
                        String infos = "";
                        for (String info : yml.getStringList("page." + page)) {
                            infos += info
                                    .replace("%name%", cropdata.getName())
                                    .replace("%state%", CpCore.cpcore.getBad(cropdata))
                                    .replace("%grow%", CpCore.cpcore.getGrow(block, cropdata))
                                    .replace("%water%", cropdata.getWater() + "%")
                                    .replace("%nutrition%", cropdata.getNutrition() + "%")
                                    .replace("%high%", cropdata.isHigh() ? "株高" : "株矮")
                                    .replace("%ownGreenHouse%", CpCore.cpcore.chineseBoolean(cropdata.isOwnGreenHouse()))
                                    .replace("%soliType%", cropdata.getSoilType().getName())
                                    .replace("%fallChance%", CpCore.cpcore.getPercent(
                                            yml.getDouble("grow.info." + CpCore.cpcore.getGrow(block, cropdata) + ".fallChance"))
                                    )
                                    .replace("%diseaseChance%", CpCore.cpcore.getPercent(
                                            yml.getDouble("grow.info." + CpCore.cpcore.getGrow(block, cropdata) + ".diseaseChance"))
                                    )
                                    + "\n";
                        }
                        bookMeta.addPage(infos);
                    }
                    book.setItemMeta(bookMeta);

                    e.setCancelled(true);
                    p.openBook(book);
                } else {
                    ItemStack itemstack = e.getItem();
                    //催熟
                    if (itemstack.getType().equals(Material.BONE) && p.isOp()) {
//                        cropdata.setOp(true);
                        CpCore.cpconfig.cropDataCrud.setCropData(cropdata); //及时写入
                        block.applyBoneMeal(BlockFace.UP);
                    }
                    //灌溉
                    if (itemstack.getType().equals(Material.POTION)) {


                        YamlConfiguration yml = CpCore.cpcore.configs.get(cropdata.getFileName());
                        Ageable age = (Ageable) block.getBlockData();

                        Set<String> set = yml.getConfigurationSection("grow.info").getKeys(false);
                        int dh = age.getAge();

                        if ((age.getAge() + 1) >= set.size()) {
                            return;
                        }

                        itemstack.setAmount(itemstack.getAmount() - 1);
                        p.getInventory().addItem(new ItemStack(Material.GLASS_BOTTLE));
                        p.updateInventory();

                        List<String> list = new ArrayList<String>(set);
                        ConfigurationSection cs = yml.getConfigurationSection("grow.info." + list.get(dh));
                        cropdata.setWater(cropdata.getWater() + cs.getInt("bottle"));

                        CpCore.cpconfig.cropDataCrud.setCropData(cropdata); //及时写入
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
                        } else {
                            cropdata.setShortWater(false);
                            cropdata.setHigherWater(false);
                            CpCore.cpconfig.cropDataCrud.setCropData(cropdata); //及时写入
                        }

                        e.setCancelled(true);


                    }
                    //施肥
                    if (itemstack.getType().equals(Material.BONE_MEAL)) {

                        YamlConfiguration yml = CpCore.cpcore.configs.get(cropdata.getFileName());
                        Ageable age = (Ageable) block.getBlockData();

                        Set<String> set = yml.getConfigurationSection("grow.info").getKeys(false);
                        int dh = age.getAge();

                        if ((age.getAge() + 1) >= set.size()) {
                            return;
                        }

                        itemstack.setAmount(itemstack.getAmount() - 1);
                        p.updateInventory();
                        List<String> list = new ArrayList<String>(set);
                        ConfigurationSection cs = yml.getConfigurationSection("grow.info." + list.get(dh));
                        cropdata.setNutrition(cropdata.getNutrition() + cs.getInt("powder"));

                        CpCore.cpconfig.cropDataCrud.setCropData(cropdata); //及时写入
                        int resultNutrition = CpCore.cpmath.satisfy(cropdata.getNutrition(), cs.getString("nutrition"), "-");
                        if (resultNutrition != 0) {
                            if (resultNutrition == 1) {
                                cropdata.setShortNutrition(true);
                                cropdata.setHigherNutrition(false);
                            } else {
                                cropdata.setHigherNutrition(true);
                                cropdata.setShortNutrition(false);
                            }
                            e.setCancelled(true);
                            CpCore.cpconfig.cropDataCrud.setCropData(cropdata); //及时写入
                            return;
                        } else {
                            cropdata.setShortNutrition(false);
                            cropdata.setHigherNutrition(false);
                            CpCore.cpconfig.cropDataCrud.setCropData(cropdata); //及时写入
                        }

                        e.setCancelled(true);


                    }

                }
            }
        }
    }
}

