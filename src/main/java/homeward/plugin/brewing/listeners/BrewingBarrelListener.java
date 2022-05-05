package homeward.plugin.brewing.listeners;

import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.StorageGui;
import homeward.plugin.brewing.enumerates.ComponentEnum;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static homeward.plugin.brewing.utils.InventoryUtils.*;

public class BrewingBarrelListener implements Listener {
    private final Map<Location, BaseGui> barrelGUIMap = new HashMap<>();
    private static final Map<HumanEntity, Location> barrelLocationMap = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(CustomBlockInteractEvent event) {
        if (!event.getNamespacedID().contains("homeward:brewing_barrel_")) return;

        Location barrelLocation = event.getBlockClicked().getLocation();
        Player player = event.getPlayer();

        if (barrelGUIMap.containsKey(barrelLocation)) {
            barrelGUIMap.get(barrelLocation).open(player);
            barrelLocationMap.put(player, barrelLocation);
            return;
        }

        StorageGui storageGui = generateStorageGui(3, ComponentEnum.BARREL_TITLE);

        storageGui.setItem(2, new GuiItem(substrateSlot));
        storageGui.setItem(11, new GuiItem(restrictionSlot));
        storageGui.setItem(20, new GuiItem(yeastSlot));
        storageGui.setItem(6, new GuiItem(substrateSlotState));
        storageGui.setItem(15, new GuiItem(restrictionSlotState));
        storageGui.setItem(24, new GuiItem(yeastSlotState));

        barrelGUIMap.put(barrelLocation, storageGui);

        storageGui.open(player);
    }

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
    public void onPlayerCloseInventoryEvent(InventoryCloseEvent event) {
        HumanEntity player = event.getPlayer();
        barrelLocationMap.remove(player);
    }

    public static Map<HumanEntity, Location> getBarrelLocationMap() {
        return barrelLocationMap;
    }
}
