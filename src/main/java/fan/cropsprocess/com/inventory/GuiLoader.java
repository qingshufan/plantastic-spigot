package fan.cropsprocess.com.inventory;

import fan.cropsprocess.com.util.CpCore;
import fan.cropsprocess.com.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GuiLoader {
    public static MyInventory loader(String path) {
        YamlConfiguration guiYml = CpCore.cpcore.configs.get("gui.yml");
        ConfigurationSection pathConfig = guiYml.getConfigurationSection(path);
        ConfigurationSection contentsConfig = pathConfig.getConfigurationSection("contents");
        String invTitle = CpCore.cpcore.convertColorSign(pathConfig.getString("invTitle"));
        Inventory inv = Bukkit.createInventory(null, 6 * 9, invTitle);
        //Default
        inv.setItem(0, new ItemStack(Material.BARRIER));
        //When used InventoryType, no InvSize
        if (!pathConfig.contains("invSize")) {
            inv = Bukkit.createInventory(null, InventoryType.valueOf(pathConfig.getString("invType")), invTitle);
        } else {
            inv = Bukkit.createInventory(null, pathConfig.getInt("invSize"), invTitle);
        }
        //Contents
        for (String itemKey : contentsConfig.getKeys(false)) {
            ConfigurationSection itemConfig = contentsConfig.getConfigurationSection(itemKey);
            ItemStack item = null;
            if (itemConfig.contains("item")) {
                YamlConfiguration cropItemConfig = CpCore.cpcore.configs.get(itemConfig.getString("item"));
                List<String> list = cropItemConfig.getStringList("lore");
                list.addAll(itemConfig.getStringList("lore"));
                item = ItemUtils.normalItem(cropItemConfig.getString("name"),
                        list,
                        Material.valueOf(cropItemConfig.getString("itemType")));
            } else {
                item = ItemUtils.normalItem(itemConfig.getString("name"),
                        itemConfig.getStringList("lore"),
                        Material.valueOf(itemConfig.getString("itemType")));
            }
            inv.setItem(itemConfig.getInt("index"), item);
        }
        return new MyInventory(inv, invTitle, contentsConfig);
    }
}
