package fan.cropsprocess.com.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocData implements Serializable {
    private static final long serialVersionUID = 131208242302L;
    private String worldName;
    private double x;
    private double y;
    private double z;

    public LocData(Location location) {
        this.worldName = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }
}
