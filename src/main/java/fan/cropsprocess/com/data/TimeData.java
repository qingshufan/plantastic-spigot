package fan.cropsprocess.com.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class TimeData implements Serializable {
    private static final long serialVersionUID = 131208242304L;
    private int period, delay;
    private String show;
}
