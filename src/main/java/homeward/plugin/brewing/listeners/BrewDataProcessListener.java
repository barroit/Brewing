package homeward.plugin.brewing.listeners;

import de.tr7zw.nbtapi.NBTFile;
import homeward.plugin.brewing.data.BrewingBarrelData;
import homeward.plugin.brewing.events.BrewDataProcessEvent;
import homeward.plugin.brewing.utils.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Set;


public class BrewDataProcessListener implements Listener {

    /**
     * 监听数据处理事件 所有的插件生产逻辑在这一部分
     *
     * @param event 当前监听的事件
     */
    // @EventHandler(priority = EventPriority.HIGHEST)
    public void onDataProcess(BrewDataProcessEvent event) {
        try {
            //获取当前nbt文件
            NBTFile file = new NBTFile(new File(event.getWorld().getName(), "brew.nbt"));
            //获取所有的keys
            Set<String> keys = file.getKeys();
            for (String key : keys) {
                String stringObject = file.getObject(key, String.class);
                BrewingBarrelData currentKeyData = (BrewingBarrelData) ItemStackUtils.decodeObject(stringObject);

                //TODO 如果当前Data在插件内存GUI种已经打开则需要把 BrewingBarrelData 发送给 GUI让GUI进行强制刷新 (@Ba1oretto)

                //如果当前的酿造不合法，未启动或者酿造完成
                if (!currentKeyData.isValid()) {
                    return;
                } else {
                    //如果当前进度小于总进度 +1
                    if (currentKeyData.getCurrentState() < currentKeyData.getTotalState()) {
                        currentKeyData.setCurrentState(currentKeyData.getCurrentState() + 1);
                    }
                    //如果当前进度等于于总进度
                    /**
                     * valid设置为false
                     * 转移预期物品数量到输出格子
                     */
                    if (currentKeyData.getCurrentState() == currentKeyData.getTotalState()) {
                        currentKeyData.setValid(false); //valid设置为false

                        currentKeyData.setStorageOutPutItems(currentKeyData.getActualOutPut()); //设置输出格子的物品数量
                        currentKeyData.setOutPutItems(new ItemStack(Material.APPLE)); //设置输出格子的物品类型


                    }

                    //操作数据完成最后存回去
                    file.setObject(key, ItemStackUtils.encodeObject(currentKeyData));
                    file.save();

                }
            }


        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }


}
