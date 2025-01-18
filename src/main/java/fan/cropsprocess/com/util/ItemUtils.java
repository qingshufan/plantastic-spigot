package fan.cropsprocess.com.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemUtils {

    public static ItemStack normalItem(String name, List<String> lores, Material type) {
        ItemStack jewl = new ItemStack(type, 1);
        ItemMeta jewls = jewl.getItemMeta();
        jewls.setDisplayName(CpCore.cpcore.convertColorSign(name));
        jewls.setLore(CpCore.cpcore.convertColorSign(lores));
        jewl.setItemMeta(jewls);
        return jewl;
    }


    public static List<ItemStack> getConditionItems(UUID uuid, String name, String lore) {
        Player p = Bukkit.getPlayer(uuid);

        name = name.equalsIgnoreCase("null") ? "" : name;

        lore = lore.equalsIgnoreCase("null") ? "" : lore;

        List<ItemStack> items = new ArrayList<>();


        for (ItemStack item : Collections.singletonList(p.getInventory().getItemInMainHand())) {
            if (item == null || !item.hasItemMeta()) {
                continue;
            }
            if (item.getItemMeta().hasDisplayName() && !item.getItemMeta().getDisplayName().contains(name)) {
                continue;
            }

            if (!lore.equals("") && item.getItemMeta().hasLore() && !hasString(item.getItemMeta().getLore(), lore)) {
                continue;
            }

            items.add(item);

            continue;
        }
        return items;
    }

    public static List<ItemStack> getConditionItems(UUID uuid, String name, List<String> lore) {
        Player p = Bukkit.getPlayer(uuid);

        name = name.equalsIgnoreCase("null") ? "" : CpCore.cpcore.convertColorSign(name);
        lore = lore == null ? new ArrayList<String>() : CpCore.cpcore.convertColorSign(lore);

        List<ItemStack> items = new ArrayList<>();


        for (ItemStack item : Collections.singletonList(p.getInventory().getItemInMainHand())) {
            if (item == null || !item.hasItemMeta()) {
                continue;
            }
            if (item.getItemMeta().hasDisplayName() && !item.getItemMeta().getDisplayName().contains(name)) {
                continue;
            }
            if (item.getItemMeta().hasLore() && !new HashSet<>(item.getItemMeta().getLore()).containsAll(lore)) {
                continue;
            }

            items.add(item);

            continue;
        }
        return items;
    }

    public static List<ItemStack> getConditionAllItems(UUID uuid, String name, String lore, Material type) {
        Player p = Bukkit.getPlayer(uuid);

        name = name.equalsIgnoreCase("null") ? "" : name;

        lore = lore.equalsIgnoreCase("null") ? "" : lore;

        List<ItemStack> items = new ArrayList<>();


        for (ItemStack item : p.getInventory().getContents()) {
            if (item == null || !item.hasItemMeta()) {
                continue;
            }
            if (!item.getItemMeta().hasDisplayName() || !item.getItemMeta().hasLore()) {
                continue;
            }
            if (!item.getType().equals(type)) {
                continue;
            }
            if (item.getItemMeta().hasDisplayName() && !item.getItemMeta().getDisplayName().contains(name)) {
                continue;
            }

            if (!lore.equals("") && item.getItemMeta().hasLore() && !hasString(item.getItemMeta().getLore(), lore)) {
                continue;
            }

            items.add(item);

        }
        return items;
    }

    public static List<ItemStack> getConditionAllItems(UUID uuid, String name, List<String> lore) {
        Player p = Bukkit.getPlayer(uuid);

        name = name.equalsIgnoreCase("null") ? "" : CpCore.cpcore.convertColorSign(name);
        lore = lore == null ? new ArrayList<String>() : CpCore.cpcore.convertColorSign(lore);

        List<ItemStack> items = new ArrayList<>();


        for (ItemStack item : p.getInventory().getContents()) {
            if (item == null || !item.hasItemMeta()) {
                continue;
            }
            if (item.getItemMeta().hasDisplayName() && !item.getItemMeta().getDisplayName().contains(name)) {
                continue;
            }
            if (item.getItemMeta().hasLore() && !new HashSet<>(item.getItemMeta().getLore()).containsAll(lore)) {
                continue;
            }

            items.add(item);

            continue;
        }
        return items;
    }

    public static Boolean hasItem(int amount, List<ItemStack> items) {

        boolean b = false;

        int hasAmount = getItemAmount(items);


        if (hasAmount >= amount) {
            b = true;
        }

        return b;
    }

    public static void takeItem(int a, List<ItemStack> items) {

        for (int i = 0; i < items.size(); i++) {

            if (i >= items.size()) {
                break;
            }

            ItemStack item = items.get(i);

            if (item.getAmount() <= a) {
                a -= item.getAmount();
                item.setAmount(0);
                items.remove(i);
                i--;
            } else {
                item.setAmount(item.getAmount() - a);
                break;
            }

        }
    }

    private static Integer getItemAmount(List<ItemStack> items) {

        int amount = 0;

        for (ItemStack item : items) {

            amount += item.getAmount();

        }

        return amount;
    }

    public static Boolean hasString(List<String> lores, String str) {

        boolean b = false;

        for (String lore : lores) {
            if (lore.contains(str)) {
                b = true;
                break;
            }
        }

        return b;
    }
}
