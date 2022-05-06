package homeward.plugin.brewing.listeners;

import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BarrelClickListener implements Listener {

    @EventHandler
    public void onPlayerClickEvent(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        // prevent shift+click and double click
        if (event.getView().getTopInventory().getHolder() instanceof BaseGui) {
            if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                event.setCancelled(true);
            }
            if (event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
                if (event.getCursor() == null) return;
                if (event.getView().getTopInventory().contains(event.getCursor().getType())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
