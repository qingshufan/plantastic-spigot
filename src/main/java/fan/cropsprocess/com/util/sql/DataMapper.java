package fan.cropsprocess.com.util.sql;

import java.io.Serializable;
import java.util.List;

public interface DataMapper<T extends Serializable> {
    List<T> list();

    T get(Object key);

    void set(T data);

    void del(Object key);

    boolean exist(Object key);
}