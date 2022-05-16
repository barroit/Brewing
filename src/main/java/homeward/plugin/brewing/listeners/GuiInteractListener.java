package homeward.plugin.brewing.listeners;

import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;

import java.util.Collections;
import java.util.Set;

public class GuiInteractListener implements Listener {
    @EventHandler
    public void onPlayerDragEvent(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof BaseGui)) return;

        Set<Integer> rawSlots = event.getRawSlots();

        Integer min = Collections.min(rawSlots);
        if (min == InventoryView.OUTSIDE || min == -1) return;

        Integer max = Collections.max(rawSlots);
        int size = event.getView().getTopInventory().getSize();
        if (max < size) {
            event.setCancelled(true);
        } else if (max >= size && min < size) {
            event.setCancelled(true);
        }
    }

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
