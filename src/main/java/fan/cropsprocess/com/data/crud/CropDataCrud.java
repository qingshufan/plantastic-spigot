package fan.cropsprocess.com.data.crud;

import fan.cropsprocess.com.data.entity.CropData;
import fan.cropsprocess.com.data.entity.LocData;
import fan.cropsprocess.com.util.CpCore;
import org.bukkit.Location;

import java.util.List;

public class CropDataCrud {
    public CropData getCropData(Location location) {
        LocData locData = new LocData(location);
        return CpCore.cpconfig.redisImpl.getData(locData, CropData.class);
    }

    public List<CropData> allCropData() {
        return CpCore.cpconfig.redisImpl.allData(CropData.class);
    }

    public void setCropData(CropData cropData) {
        CpCore.cpconfig.redisImpl.setData(cropData.getLoc(), cropData);
    }

    public Boolean existCropData(Location location) {
        LocData locData = new LocData(location);
        return CpCore.cpconfig.redisImpl.existData(locData, CropData.class);
    }

    public void delCropData(Location location) {
        LocData locData = new LocData(location);
        CpCore.cpconfig.redisImpl.delData(locData, CropData.class);
    }
}
