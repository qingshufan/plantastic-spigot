package fan.cropsprocess.com.util.sql;

import fan.cropsprocess.com.data.entity.CropData;
import fan.cropsprocess.com.data.entity.RankData;
import fan.cropsprocess.com.mapper.CropDataMapper;
import fan.cropsprocess.com.mapper.RankDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MysqlImpl implements SqlUtils {
    private final Map<Class<? extends Serializable>, DataMapper<? extends Serializable>> mappers = new HashMap<>();

    @Autowired
    public MysqlImpl(RankDataMapper rankDataMapper, CropDataMapper cropDataMapper) {
        mappers.put(RankData.class, rankDataMapper);
        mappers.put(CropData.class, cropDataMapper);
    }

    @Override
    public <T extends Serializable> T getData(Object key, Class<T> type) {
        DataMapper<T> mapper = (DataMapper<T>) mappers.get(type);
        if (mapper == null) {
            throw new IllegalArgumentException("Unsupported data type: " + type);
        }
        try {
            return type.cast(mapper.get(key));
        } catch (Exception e) {
            throw new RuntimeException("Error invoking get method", e);
        }
    }

    @Override
    public <T extends Serializable> void setData(Object key, T data) {

        DataMapper<T> mapper = (DataMapper<T>) mappers.get(data.getClass());
        if (mapper == null) {
            throw new IllegalArgumentException("Unsupported data type: " + data.getClass());
        }

        try {
            mapper.set(data);
        } catch (Exception e) {
            throw new RuntimeException("Error invoking set method", e);
        }
    }

    @Override
    public <T extends Serializable> void delData(Object key, Class<T> type) {

        DataMapper<T> mapper = (DataMapper<T>) mappers.get(type);
        if (mapper == null) {
            throw new IllegalArgumentException("Unsupported data type: " + type);
        }

        try {
            mapper.del(key);
        } catch (Exception e) {
            throw new RuntimeException("Error invoking del method", e);
        }
    }

    @Override
    public <T extends Serializable> Boolean existData(Object key, Class<T> type) {
        DataMapper<T> mapper = (DataMapper<T>) mappers.get(type);
        if (mapper == null) {
            throw new IllegalArgumentException("Unsupported data type: " + type);
        }
        try {
            return mapper.exist(key);
        } catch (Exception e) {
            throw new RuntimeException("Error invoking exist method", e);
        }
    }

    @Override
    public <T extends Serializable> List<T> allData(Class<T> type) {
        DataMapper<T> mapper = (DataMapper<T>) mappers.get(type);
        if (mapper == null) {
            throw new IllegalArgumentException("Unsupported data type: " + type);
        }

        try {
            return mapper.list();
        } catch (Exception e) {
            throw new RuntimeException("Error invoking findAll method", e);
        }
    }
}