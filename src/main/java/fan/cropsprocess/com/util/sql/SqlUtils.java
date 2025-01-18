package fan.cropsprocess.com.util.sql;

import java.io.Serializable;
import java.util.List;

public interface SqlUtils {
    <T extends Serializable> T getData(Object key, Class<T> type);

    <T extends Serializable> void setData(Object key, T data);

    <T extends Serializable> void delData(Object key, Class<T> type);

    <T extends Serializable> Boolean existData(Object key, Class<T> type);

    <T extends Serializable> List<T> allData(Class<T> type);
}
