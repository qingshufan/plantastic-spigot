package fan.cropsprocess.com.mapper;

import fan.cropsprocess.com.data.entity.CropData;
import fan.cropsprocess.com.data.entity.LocData;
import fan.cropsprocess.com.util.sql.DataMapper;

import java.util.List;

public interface CropDataMapper extends DataMapper<CropData> {
    List<CropData> list();

    CropData get(LocData locData);

    void set(CropData cropData);

    void del(LocData locData);

    boolean exist(LocData locData);

}
