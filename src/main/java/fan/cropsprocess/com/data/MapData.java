package fan.cropsprocess.com.data;

import org.bukkit.Location;

public class MapData {
    // 用来存储锄头点击的矩形起始位置
    Location selection1 = null;
    // 用来存储铲子的矩形起始位置
    Location selection2 = null;

    public Location GetLocation1() {
        return selection1;
    }

    public Location GetLocation2() {
        return selection2;
    }

    public void SetSelection1(Location location) {
        selection1 = location;
    }

    public void SetSelection2(Location location) {
        selection2 = location;
    }
}
