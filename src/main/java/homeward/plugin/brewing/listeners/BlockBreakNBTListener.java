package homeward.plugin.brewing.listeners;

import de.tr7zw.nbtapi.NBTFile;
import homeward.plugin.brewing.commands.MainCommand;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.io.File;
import java.io.IOException;

public class BlockBreakNBTListener implements Listener {

    @EventHandler
    public void onNBTBlockBreak(BlockBreakEvent event) throws IOException {

        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();

        NBTFile file = new NBTFile(new File(player.getWorld().getName(), "brew.nbt"));

        if (!file.hasKey(location + "")) return;

        file.removeKey(location + "");
        file.save();
    }
}
