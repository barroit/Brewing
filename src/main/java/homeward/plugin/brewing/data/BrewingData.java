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

    public BrewingData() {
        super();
    }



    public ItemStack getSubstrate() {
        return substrate;
    }

    public void setSubstrate(ItemStack substrate) {
        this.substrate = substrate;
    }

    public ItemStack getRestriction() {
        return restriction;
    }

    public void setRestriction(ItemStack restriction) {
        this.restriction = restriction;
    }

    public ItemStack getYest() {
        return yest;
    }

    public void setYest(ItemStack yest) {
        this.yest = yest;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getBrewingType() {
        return brewingType;
    }

    public void setBrewingType(String brewingType) {
        this.brewingType = brewingType;
    }

    public Integer getExpectOutPut() {
        return expectOutPut;
    }

    public void setExpectOutPut(Integer expectOutPut) {
        this.expectOutPut = expectOutPut;
    }

    public Integer getActualOutPut() {
        return actualOutPut;
    }

    public void setActualOutPut(Integer actualOutPut) {
        this.actualOutPut = actualOutPut;
    }

    public Integer getCurrentState() {
        return currentState;
    }

    public void setCurrentState(Integer currentState) {
        this.currentState = currentState;
    }

    public Integer getTotalState() {
        return totalState;
    }

    public void setTotalState(Integer totalState) {
        this.totalState = totalState;
    }

    public Integer getStorageOutPutItems() {
        return storageOutPutItems;
    }

    public void setStorageOutPutItems(Integer storageOutPutItems) {
        this.storageOutPutItems = storageOutPutItems;
    }

    public Object getOutPutItems() {
        return outPutItems;
    }

    public void setOutPutItems(Object outPutItems) {
        this.outPutItems = outPutItems;
    }
}
