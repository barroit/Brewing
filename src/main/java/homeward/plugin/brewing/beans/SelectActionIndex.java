package homeward.plugin.brewing.beans;

import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public class SelectActionIndex implements Serializable {
    private String substrate;
    private String restriction;
    private String yeast;

    @Override
    public String toString() {
        return "{" +
                "\"substrate\"=\"" + substrate + '\"' +
                ", \"restriction\"=\"" + restriction + '\"' +
                ", \"yeast\"=\"" + yeast + '\"' +
                '}';
    }
}
