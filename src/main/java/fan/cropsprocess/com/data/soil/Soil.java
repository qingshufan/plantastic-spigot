package fan.cropsprocess.com.data.soil;

import java.util.Random;

public class Soil {
    public SoilType getRandomSoilType() {
        SoilType[] soilTypes = SoilType.values();
        SoilType s = soilTypes[new Random().nextInt(soilTypes.length)];
//        Bukkit.broadcastMessage("土壤类型为：" + s.getName());
        return s;
    }
}
