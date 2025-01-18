package fan.cropsprocess.com.listener.base;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class NoClickInventoryListener implements Listener {
    @EventHandler
    public void click(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (event.getView().getTitle().contains("§")) {
            Player player = (Player) event.getWhoClicked();
            player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 1);
            event.setCancelled(true);
        }
    }
}
