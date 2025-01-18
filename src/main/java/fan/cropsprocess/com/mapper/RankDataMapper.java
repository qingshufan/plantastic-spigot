package fan.cropsprocess.com.mapper;

import fan.cropsprocess.com.data.entity.RankData;
import fan.cropsprocess.com.util.sql.DataMapper;

import java.util.List;

public interface RankDataMapper extends DataMapper<RankData> {
    List<RankData> list();

    RankData get(String uuid);

    void set(RankData rankData);

    void del(String uuid);

    boolean exist(String uuid);

}
