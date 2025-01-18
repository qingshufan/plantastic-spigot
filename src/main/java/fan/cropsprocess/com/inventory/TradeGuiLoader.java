package fan.cropsprocess.com.inventory;

import fan.cropsprocess.com.util.CpCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class TradeGuiLoader {

    // 加载商店选择界面
    public static Inventory loadShopSelector(String fileName) {
        Yaml yaml = new Yaml();
        InputStream inputStream = TradeGuiLoader.class.getClassLoader().getResourceAsStream(fileName);

        Map<String, Object> config = yaml.load(inputStream);
        Map<String, Object> markets = (Map<String, Object>) config.get("markets");
        Map<String, Object> shopSelector = (Map<String, Object>) markets.get("shopSelector");

        String invTitle = (String) shopSelector.get("invTitle");
        invTitle = CpCore.cpcore.convertColorSign(invTitle);
        int invSize = (Integer) shopSelector.get("invSize");

        Inventory inventory = Bukkit.createInventory(null, invSize, invTitle);

        // 配置商店选择界面的物品
        Map<String, Object> contents = (Map<String, Object>) shopSelector.get("contents");
        for (Map.Entry<String, Object> entry : contents.entrySet()) {
            String shopName = entry.getKey();
            Map<String, Object> shopDetails = (Map<String, Object>) entry.getValue();

            String name = (String) shopDetails.get("name");
            String itemType = (String) shopDetails.get("itemType");
            List<String> lore = (List<String>) shopDetails.get("lore");
            int index = (Integer) shopDetails.get("index");

            Material material = Material.getMaterial(itemType);
            ItemStack itemStack = new ItemStack(material);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(CpCore.cpcore.convertColorSign(name));
            meta.setLore(CpCore.cpcore.convertColorSign(lore));
            itemStack.setItemMeta(meta);

            inventory.setItem(index, itemStack);
        }

        return inventory;
    }

    // 加载指定商店的物品
    public static Inventory loadShop(String shopName, String fileName) {
        Yaml yaml = new Yaml();
        InputStream inputStream = TradeGuiLoader.class.getClassLoader().getResourceAsStream(fileName);

        Map<String, Object> config = yaml.load(inputStream);
        Map<String, Object> markets = (Map<String, Object>) config.get("markets");
        Map<String, Object> shopData = (Map<String, Object>) markets.get(shopName);

        String invTitle = (String) shopData.get("invTitle");
        invTitle = CpCore.cpcore.convertColorSign(invTitle);
        int invSize = (Integer) shopData.get("invSize");

        Inventory inventory = Bukkit.createInventory(null, invSize, invTitle);

        // 配置商店界面的物品
        Map<String, Object> contents = (Map<String, Object>) shopData.get("contents");
        for (Map.Entry<String, Object> entry : contents.entrySet()) {
            String itemName = entry.getKey();
            Map<String, Object> itemDetails = (Map<String, Object>) entry.getValue();

            String name = (String) itemDetails.get("name");
            String itemType = (String) itemDetails.get("itemType");
            int price = (Integer) itemDetails.get("price");
            List<String> lore = (List<String>) itemDetails.get("lore");
            int index = (Integer) itemDetails.get("index");

            Material material = Material.getMaterial(itemType);
            ItemStack itemStack = new ItemStack(material);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(CpCore.cpcore.convertColorSign(name));
            meta.setLore(CpCore.cpcore.convertColorSign(lore));
            itemStack.setItemMeta(meta);

            // 设置物品到指定位置
            inventory.setItem(index, itemStack);
        }

        return inventory;
    }

    // 获取物品价格
    public static int getItemPrice(ItemStack itemStack) {
        Yaml yaml = new Yaml();
        InputStream inputStream = TradeGuiLoader.class.getClassLoader().getResourceAsStream("market.yml");

        if (inputStream == null) {
            return 0; // 如果文件加载失败，返回0
        }

        Map<String, Object> config = yaml.load(inputStream);
        Map<String, Object> markets = (Map<String, Object>) config.get("markets");

        // 循环处理所有商店
        for (Map.Entry<String, Object> entry : markets.entrySet()) {
            Map<String, Object> shop = (Map<String, Object>) entry.getValue();
            Map<String, Object> contents = (Map<String, Object>) shop.get("contents");

            for (Map.Entry<String, Object> itemEntry : contents.entrySet()) {
                Map<String, Object> itemDetails = (Map<String, Object>) itemEntry.getValue();
                String itemType = (String) itemDetails.get("itemType");
                Object priceObj = itemDetails.get("price");

                // 确保物品有价格配置，并且价格不为null
                if (priceObj instanceof Integer) {
                    int price = (Integer) priceObj;
                    if (itemType.equals(itemStack.getType().toString())) {
                        return price; // 返回物品的价格
                    }
                }
            }
        }

        // 如果未找到价格，返回0
        return 0;
    }
}
