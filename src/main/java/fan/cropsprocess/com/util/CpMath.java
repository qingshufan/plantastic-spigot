package fan.cropsprocess.com.util;

import java.text.DecimalFormat;
import java.util.Random;

public class CpMath {
    public CpMath() {

    }

    public int satisfy(int value, String interval, String split) {
        int result = 0;

        int min = Integer.parseInt(interval.split(split)[0]);
        int max = Integer.parseInt(interval.split(split)[1]);
        if (value < min) result = 1;
        if (value > max) result = 2;
        return result;
    }

    public int randomInteger(String interval) {
        int result = 0;

        int min = Integer.parseInt(interval.split("-")[0]);
        int max = Integer.parseInt(interval.split("-")[1]);
        return new Random().nextInt(max - min) + min;
    }

    public double randomDouble(String range) {
        String[] parts = range.split("-"); // 去掉大括号并分割
        double min = Double.parseDouble(parts[0]);
        double max = Double.parseDouble(parts[1]);
        return min + (max - min) * new Random().nextDouble(); // 生成范围内的随机数
    }

    public double randomDouble(double min, double max) {
        return min + (max - min) * new Random().nextDouble(); // 生成范围内的随机数
    }

    public double round(double b) {
        DecimalFormat df = new DecimalFormat("#0.00");
        if (Double.isNaN(b)) {
            return 0.00;
        }
        return Double.valueOf(df.format(b));
    }

    public boolean random(double chance) {
        return (Math.random() <= chance);
    }

}	
