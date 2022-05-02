package homeward.plugin.brewing.utils;

import homeward.plugin.brewing.guis.GuiBase;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.InventoryView;

import java.util.Collections;
import java.util.Set;

/**
 * A super awesome utilities
 *
 * @author Baioretto
 * @version 1.0.0
 */
public class AwesomeUtils {

    /**
     * cancel drag event for your custom GUI.
     * this can prevent drag from top inventory
     * and also prevent drag between the top and bottom inventory
     *
     * @param event drag event
     */
    public static void cancelDrag(final InventoryDragEvent event) {
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
}
