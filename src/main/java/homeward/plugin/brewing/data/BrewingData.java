package homeward.plugin.brewing.data;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

public class BrewingData implements Serializable {

    //左三
    private ItemStack substrate;
    private ItemStack restriction;
    private ItemStack yest;

    //是否正在进行酿造
    private boolean isValid;
    //酿造类型
    private String brewingType;
    //期望产出
    private Integer expectOutPut;
    //实际产出
    private Integer actualOutPut;

    //当前进度
    private Integer currentState;
    //总共进度
    private Integer totalState;

    //酿造完成后 酒桶内部储存的产出物品数量
    private Integer storageOutPutItems;
    //酿造完成后 酒桶内部储存的产出物品类型
    private Object outPutItems;




}
