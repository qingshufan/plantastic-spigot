package fan.cropsprocess.com.listener.skill.tree;

import fan.cropsprocess.com.data.entity.RankData;
import fan.cropsprocess.com.inventory.GuiLoader;
import fan.cropsprocess.com.inventory.MyInventory;
import fan.cropsprocess.com.util.CpCore;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

//GUI界面中鼠标点击监听
public class SkillTreeGotoListener implements Listener {
    //主门户的三个格子点击跳转
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        MyInventory myInventory = GuiLoader.loader("skillTree.hub");
        //主门户的三个格子点击跳转
        if (event.getView().getTitle().equals(myInventory.getTitle())) {

            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            //跳转至种子影响科技树界面
            if (event.getSlot() == myInventory.getContentsConfig().getInt("seed.index")) {
                Inventory inv = GuiLoader.loader("skillTree.seed").getInventory();
                player.openInventory(inv);
            }
            //跳转至种子图鉴界面
            if (event.getSlot() == myInventory.getContentsConfig().getInt("book.index")) {
                MyInventory inv = GuiLoader.loader("skillTree.book");
                //设置已解锁物品为附魔状态
                if (CpCore.cpconfig.rankDataCrud.existRankData(player.getUniqueId())) {
                    RankData rankData = CpCore.cpconfig.rankDataCrud.getRankData(player.getUniqueId());
                    Map<String, Boolean> books = rankData.getBooks() == null ? new HashMap<String, Boolean>() : rankData.getBooks();
                    for (String key : books.keySet()) {
                        if (books.get(key)) {
                            ItemStack itemStack = inv.getInventory().getItem(inv.getContentsConfig().getInt(key + ".index"));
                            itemStack.addUnsafeEnchantment(Enchantment.LOYALTY, 1);
                            inv.getInventory().setItem(inv.getContentsConfig().getInt(key + ".index"), itemStack);
                        }
                    }
                }
                player.openInventory(inv.getInventory());
            }
            //跳转至工具使用界面
            if (event.getSlot() == myInventory.getContentsConfig().getInt("tool.index")) {
                Inventory inv = GuiLoader.loader("skillTree.tool").getInventory();
                player.openInventory(inv);
            }
        }

    }
}
