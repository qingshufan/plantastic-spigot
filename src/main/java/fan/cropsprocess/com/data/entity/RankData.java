package fan.cropsprocess.com.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankData implements Serializable {
    private static final long serialVersionUID = 131208242303L;
    private UUID uuid;
    private int prevalenceLevel, resistanceLevel, yieldLevel;
    private Map<String, Boolean> books;
    private int hoeLevel, shovelLevel, bottleLevel, nutritionLevel, growLevel, harvestLevel;
}
