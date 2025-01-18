package fan.cropsprocess.com.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;

@Data
@AllArgsConstructor
public class MyInventory {
    private Inventory inventory;
    private String title;
    private ConfigurationSection contentsConfig;
}
