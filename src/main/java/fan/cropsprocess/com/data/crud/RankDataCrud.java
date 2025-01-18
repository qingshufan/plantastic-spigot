package fan.cropsprocess.com.data.crud;

import fan.cropsprocess.com.data.entity.RankData;
import fan.cropsprocess.com.util.CpCore;

import java.util.List;
import java.util.UUID;

public class RankDataCrud {
    public RankData getRankData(UUID uuid) {
        return CpCore.cpconfig.redisImpl.getData(uuid.toString(), RankData.class);
    }

    public List<RankData> allRankData() {
        return CpCore.cpconfig.redisImpl.allData(RankData.class);
    }

    public void setRankData(RankData rankData) {
        CpCore.cpconfig.redisImpl.setData(rankData.getUuid().toString(), rankData);
    }

    public Boolean existRankData(UUID uuid) {
        return CpCore.cpconfig.redisImpl.existData(uuid.toString(), RankData.class);
    }

    public void delRankData(UUID uuid) {
        CpCore.cpconfig.redisImpl.delData(uuid.toString(), RankData.class);
    }
}
