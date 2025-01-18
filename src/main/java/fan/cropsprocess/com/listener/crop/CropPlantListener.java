package fan.cropsprocess.com.listener.crop;

import fan.cropsprocess.com.data.disease.CropDisease;
import fan.cropsprocess.com.data.entity.CropData;
import fan.cropsprocess.com.data.entity.LocData;
import fan.cropsprocess.com.data.soil.Soil;
import fan.cropsprocess.com.data.soil.SoilType;
import fan.cropsprocess.com.util.CpCore;
import fan.cropsprocess.com.util.ItemUtils;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CropPlantListener implements Listener {
    @EventHandler
    public void BlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (e.isCancelled()) {
            return;
        }
        if (!e.canBuild()) {
            return;
        }
        Block block = e.getBlockPlaced();
        ItemStack itemcrops = e.getItemInHand();
        if (CpCore.cpcore.isCrop(itemcrops)) {

            String fileName = "config.yml";
            String cropname = CpCore.cpcore.getCropName(itemcrops.getType());
            for (String name : CpCore.cpcore.configs.keySet()) {
                YamlConfiguration yaml = CpCore.cpcore.configs.get(name);
                if (yaml.contains("name") && yaml.contains("lore")) {
                    List<ItemStack> items = ItemUtils.getConditionItems(p.getUniqueId(),
                            yaml.getString("name"), yaml.getStringList("lore"));
                    if (ItemUtils.hasItem(1, items)) {
                        fileName = name;
                        cropname = items.get(0).getItemMeta().getDisplayName();
                    }
                }
            }

            SoilType soilType = new Soil().getRandomSoilType();
            CropData cropdata = new CropData(cropname,
                    new LocData(block.getLocation()),
                    0, 0, false, false, true, true,
                    false, false, false, false, false, fileName,
                    0, 0, "", soilType, new ArrayList<CropDisease>(), Math.random() < 0.5 ? true : false,
                    false, false, p.getUniqueId(), System.currentTimeMillis(),
                    false);
            CpCore.cpconfig.cropDataCrud.setCropData(cropdata); //及时写入
            YamlConfiguration lang = CpCore.cpcore.configs.get("language.yml");
            CpCore.cpcore.sendActionBar(p, lang.getString("cropPlaceMsg")
                    .replace("%name%", cropname));
        }
    }


}
