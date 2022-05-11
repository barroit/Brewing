package homeward.plugin.brewing.listeners;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.tr7zw.nbtapi.NBTFile;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.StorageGui;
import homeward.plugin.brewing.Brewing;
import homeward.plugin.brewing.beans.BarrelInventoryData;
import homeward.plugin.brewing.events.BrewDataProcessEvent;
import homeward.plugin.brewing.utils.HomewardUtils;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class BrewDataProcessListener implements Listener {
    NBTFile file;

    /**
     * 监听数据处理事件 所有的插件生产逻辑在这一部分
     *
     * @param event 当前监听的事件
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    @SneakyThrows
    public void onDataProcess(BrewDataProcessEvent event) {
        //获取当前nbt文件
        NBTFile file = new NBTFile(new File(event.getWorld().getName(), "brew.nbt"));
        //获取所有的keys
        Set<String> barrelLocations = file.getKeys();
        barrelLocations.forEach(s -> updateData(s, event));

        // for (String key : keys) {
        //
        //     String stringObject = file.getObject(key, String.class);
        //     BrewingBarrelData currentKeyData = (BrewingBarrelData) ItemStackUtils.decodeObject(stringObject);
        //
        //     //TODO 如果当前Data在插件内存GUI种已经打开则需要把 BrewingBarrelData 发送给 GUI让GUI进行强制刷新 (@Ba1oretto)
        //
        //     //如果当前的酿造不合法，未启动或者酿造完成
        //     if (!currentKeyData.isValid()) {
        //         return;
        //     } else {
        //         //如果当前进度小于总进度 +1
        //         if (currentKeyData.getCurrentState() < currentKeyData.getTotalState()) {
        //             currentKeyData.setCurrentState(currentKeyData.getCurrentState() + 1);
        //         }
        //         //如果当前进度等于于总进度
        //         /**
        //          * valid设置为false
        //          * 转移预期物品数量到输出格子
        //          */
        //         if (currentKeyData.getCurrentState() == currentKeyData.getTotalState()) {
        //             currentKeyData.setValid(false); //valid设置为false
        //
        //             currentKeyData.setStorageOutPutItems(currentKeyData.getActualOutPut()); //设置输出格子的物品数量
        //             currentKeyData.setOutPutItems(new ItemStack(Material.APPLE)); //设置输出格子的物品类型
        //
        //
        //         }
        //
        //         //操作数据完成最后存回去
        //         file.setObject(key, ItemStackUtils.encodeObject(currentKeyData));
        //         file.save();
        //
        //     }
        // }
    }

    @SneakyThrows
    private void updateData(final String stringLocation, final BrewDataProcessEvent event) {
        if (file == null) {
            file = new NBTFile(new File(event.getWorld().getName(), "brew.nbt"));
        }

        byte[] bytesData = file.getByteArray(stringLocation);

        BarrelInventoryData data = (BarrelInventoryData) HomewardUtils.deserializeBytes(bytesData.length == 0 ? null : bytesData);
        if (data == null) return;

        if (data.getCurrentBrewingTime() >= data.getBrewingTime()) return;
        if (!data.isBrewing()) return;

        data.setCurrentBrewingTime(data.getCurrentBrewingTime() + 1);

        if (data.getCurrentBrewingTime() == data.getBrewingTime()) {
            data.setBrewing(false).setStoredOutPutItems(data.getActualOutPut());
        }

        file.setObject(stringLocation, data);
        file.save();

        Location location = getLocation(stringLocation);
        if (location == null) return;

        Map<Location, BaseGui> barrelGUIMap = BrewingBarrelListener.getBarrelGUIMap();

        if (!barrelGUIMap.containsKey(location)) return;
        StorageGui gui = (StorageGui) barrelGUIMap.get(location);

        List<HumanEntity> viewers = gui.getInventory().getViewers();

        BrewingBarrelListener.initializeSlot(location, gui);

        viewers.forEach(gui::open);
    }

    private Location getLocation(String stringLocation) {
        String jsonString = stringLocation.replaceAll(".*(\\{)(.+?)},?", "$1$2,").replaceAll("=", ":");
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        String worldName = jsonObject.get("name").getAsString();
        if (!Brewing.getWorldMap().containsKey(worldName)) return null;
        World world = Brewing.getWorldMap().get(worldName);
        float x = jsonObject.get("x").getAsFloat();
        float y = jsonObject.get("y").getAsFloat();
        float z = jsonObject.get("z").getAsFloat();
        float pitch = jsonObject.get("pitch").getAsFloat();
        float yaw = jsonObject.get("yaw").getAsFloat();
        return new Location(world , x, y, z, yaw, pitch);
    }
}
