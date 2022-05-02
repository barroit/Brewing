package homeward.plugin.brewing.listeners;

import de.tr7zw.nbtapi.NBTFile;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.io.File;
import java.io.IOException;

/**
 * 此类用来监听方块击碎事件用来配合指令清除
 * 销毁方块时清除指定世界nbt文件的key
 */
public class BlockBreakNBTListener implements Listener {
    /**
     * 这个方法的优先级可以很低
     * @param event
     * @throws IOException
     */
    @EventHandler
    public void onNBTBlockBreak(BlockBreakEvent event) throws IOException {

        Player player = event.getPlayer();
        Block targetBlock = event.getBlock();

        int blockX = targetBlock.getLocation().getBlockX();
        int blockY = targetBlock.getLocation().getBlockY();
        int blockZ = targetBlock.getLocation().getBlockZ();

        String key = "" + blockX + blockY + blockZ;

        NBTFile file = new NBTFile(new File(player.getWorld().getName(), "brew.nbt"));
        if (file.hasKey(key)) {
            file.removeKey(key);
            file.save();
        } else {
            return;
        }

    }
}
