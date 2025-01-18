package fan.cropsprocess.com.data.soil;

import org.bukkit.Bukkit;

import java.util.Random;

public enum SoilType {
    SALINE("盐性土壤"),
    ACIDIC("酸性土壤"),
    DRY("干性土壤");

    private final String name;

    SoilType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public SoilType getRandomSoilType() {
        SoilType[] soilTypes = SoilType.values();
        SoilType s = soilTypes[new Random().nextInt(soilTypes.length)];
        Bukkit.broadcastMessage("土壤类型为：" + s.getName());
        return s;
    }
}
