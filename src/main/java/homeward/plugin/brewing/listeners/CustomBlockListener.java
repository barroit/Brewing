package homeward.plugin.brewing.listeners;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import homeward.plugin.brewing.utils.ConfigurationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomBlockListener implements Listener {

    @EventHandler
    public void onBlockPlaced(CustomBlockPlaceEvent event) {
        FileConfiguration configuration = ConfigurationUtil.get("block-surface");

        String currentBlock = null;

        String blockName = event.getNamespacedID().replaceAll("^.*:(.*)", "$1");

        for (String v : configuration.getKeys(false)) {
            if (v.equals(blockName)) {
                currentBlock = v;
            }
        }

        if (currentBlock == null) return;

        ConfigurationSection section = configuration.getConfigurationSection(currentBlock);

        String namespace = section.getString("namespace");

        Player player = event.getPlayer();

        Location location = event.getBlock().getLocation();
        int yaw = (int) Math.floor(player.getLocation().getYaw());

        CustomBlock customBlock;

        if (inRange(yaw, 135, 180) || inRange(yaw, -180, -135)) {
            customBlock = CustomBlock.getInstance(namespace + ":" + section.getString("south"));
        } else if (inRange(yaw, -135, -45)) {
            customBlock = CustomBlock.getInstance(namespace + ":" + section.getString("west"));
        } else if (inRange(yaw, 0, 45) || inRange(yaw, -45, 0)) {
            customBlock = CustomBlock.getInstance(namespace + ":" + section.getString("north"));
        } else {
            customBlock = CustomBlock.getInstance(namespace + ":" + section.getString("east"));
        }

        event.setCancelled(true);

        customBlock.place(location);
        customBlock.playPlaceSound();
        player.swingMainHand();
    }

    private Boolean inRange(Integer target, Integer begin, Integer end) {
        return target > begin && target < end;
    }
}
