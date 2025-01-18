package fan.cropsprocess.com.util.sql;

import fan.cropsprocess.com.util.CpCore;
import fan.cropsprocess.com.util.SerializeUtil;
import redis.clients.jedis.Jedis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RedisImpl implements SqlUtils {
    public <T extends Serializable> T getData(Object key, Class<T> type) {
        Jedis jedis = null;
        T cachedData = null;
        try {
            jedis = CpCore.pool.getResource();
            byte[] data = jedis.get((type.getName() + "." + key.toString()).getBytes());
            if (data != null) {
                cachedData = (T) SerializeUtil.unserialize(data);
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return cachedData;
    }

    public <T extends Serializable> void setData(Object key, T data) {
        Jedis jedis = null;
        try {
            jedis = CpCore.pool.getResource();
            byte[] keys = (data.getClass().getName() + "." + key.toString()).getBytes();
            byte[] value = SerializeUtil.serialize(data);
            jedis.set(keys, value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public <T extends Serializable> void delData(Object key, Class<T> type) {
        Jedis jedis = null;
        try {
            jedis = CpCore.pool.getResource();
            byte[] keys = (type.getName() + "." + key.toString()).getBytes();
            jedis.del(keys);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public <T extends Serializable> Boolean existData(Object key, Class<T> type) {
        Jedis jedis = null;
        try {
            jedis = CpCore.pool.getResource();
            // 构造Redis中的键名
            String redisKey = type.getName() + "." + key.toString();
            // 检查键是否存在
            return jedis.exists(redisKey.getBytes());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public <T extends Serializable> List<T> allData(Class<T> type) {
        Jedis jedis = null;
        List<T> dataList = new ArrayList<>();
        try {
            jedis = CpCore.pool.getResource();
            // 获取所有键
            Set<byte[]> keys = jedis.keys((type.getName() + ".*").getBytes());
            if (keys != null) {
                for (byte[] key : keys) {
                    byte[] data = jedis.get(key);
                    if (data != null) {
                        T dataObj = (T) SerializeUtil.unserialize(data);
                        dataList.add(dataObj);
                    }
                }
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return dataList;
    }
}
