package fan.cropsprocess.com.data.disease;

import fan.cropsprocess.com.data.entity.CropData;

import java.util.Random;

public class Disease {
    public boolean shouldGetDiseased() {
        return new Random().nextDouble() < 0.1;  // 10%的几率患病
    }

    public CropDisease getRandomDisease() {
        CropDisease[] diseases = CropDisease.values();
        return diseases[new Random().nextInt(diseases.length)];
    }

    public void handleDiseaseEffect(CropData cropData, CropDisease disease) {
        switch (disease) {
            case WHEAT_RUST:
                // 小麦锈病：减少作物的成长速度
                cropData.setDelay(cropData.getDelay() + 10);  // 增加成长延迟
                break;
            case LEAF_BLIGHT:
                // 叶枯病：减少作物的水分吸收
                cropData.setWater(cropData.getWater() - 5);  // 减少水分
                break;
            case POWDERY_MILDEW:
                // 白粉病：减少作物的营养吸收
                cropData.setNutrition(cropData.getNutrition() - 5);  // 减少营养
                break;
            case FUSARIUM_HEAD_BLIGHT:
                // 赤霉病：降低作物的健康值
                cropData.setDelay(cropData.getDelay() + 5);  // 增加成长延迟
                break;
        }
    }
}
