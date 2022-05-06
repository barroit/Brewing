package homeward.plugin.brewing.beans;

import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class BarrelInventoryData implements Serializable {
    private ItemStack substrate;
    private ItemStack restriction;
    private ItemStack yeast;

    private boolean hasSubstrate = false;
    private boolean hasRestriction = false;
    private boolean hasYeast = false;
    private boolean isBrewing = false;

    private String brewingType;
    private Object outPutItems;

    private int expectOutPut;
    private int actualOutPut;
    private Integer storedOutPutItems;

    private int brewingTime;
    private int currentBrewingTime;
}