package fan.cropsprocess.com.data.disease;

public enum CropDisease {
    WHEAT_RUST("小麦锈病"),
    LEAF_BLIGHT("叶枯病"),
    POWDERY_MILDEW("白粉病"),
    FUSARIUM_HEAD_BLIGHT("赤霉病");

    private final String name;

    CropDisease(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
