package fan.cropsprocess.com.data.entity;

import fan.cropsprocess.com.data.disease.CropDisease;
import fan.cropsprocess.com.data.soil.SoilType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CropData implements Serializable {
    private static final long serialVersionUID = 131208242301L;
    private String name;
    private LocData loc;
    private int water, nutrition;
    private boolean fall, disease, shortWater, shortNutrition, death, higherWater, higherNutrition;
    private boolean delete = false;
    private boolean op = false;
    private String fileName;
    private int period, delay;
    private String show;
    private SoilType soilType;
    private List<CropDisease> diseases;
    private boolean high; //高矮性状
    private boolean ownGreenHouse; //是否拥有温棚
    private boolean temperatureError; //是否温度不正常
    private UUID uuid; //种植者
    private long matureTime; //成熟时间
    private boolean insect; //是否处于蝗虫灾害

    public void addDisease(CropDisease disease) {
        if (diseases == null) {
            diseases = new ArrayList<>();
        }
        if (!diseases.contains(disease)) {
            diseases.add(disease);
        }
    }
}
