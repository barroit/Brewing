package homeward.plugin.brewing.data;

import java.io.Serializable;

public class BrewingData implements Serializable {
    public String info;

    public BrewingData(String info) {
        this.info = info;
    }
}
