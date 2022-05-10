package homeward.plugin.brewing.beans;

import dev.lone.itemsadder.api.CustomStack;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class BarrelInventoryData implements Serializable {
    private ItemStack substrateSlot;
    private ItemStack restrictionSlot;
    private ItemStack yeastSlot;

    private ItemStack substrate;
    private ItemStack restriction;
    private ItemStack yeast;

    private boolean hasSubstrate = false;
    private boolean hasRestriction = false;
    private boolean hasYeast = false;
    private boolean isBrewing = false;

    private String brewingType;
    private String outPutItems;

    private int expectOutPut;
    private int actualOutPut;
    private Integer storedOutPutItems;

    private int brewingTime;
    private int currentBrewingTime;

    private boolean initialize = false;
}