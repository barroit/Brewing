package homeward.plugin.brewing.listener;

import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.InventoryView;

import java.util.Collections;
import java.util.Set;

public class PlayerConsumeListener implements Listener {
    @EventHandler
    public void onPlayerEatingEvent(PlayerItemConsumeEvent event) {

    }
}
