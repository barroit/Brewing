package homeward.plugin.brewing.beans;

import homeward.plugin.brewing.enumerates.BrewingType;
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
    private boolean hasSubstrate;
    private boolean hasRestriction;
    private boolean hasYeast;
    private boolean isBrewing;
    private BrewingType brewingType;
    private Object outPutItems;
    private int expectOutPut;
    private int actualOutPut;
    private Integer storedOutPutItems;
    private int brewingTime;
    private int currentBrewingTime;
}