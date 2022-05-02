package homeward.plugin.brewing.listeners;

import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import homeward.plugin.brewing.guis.GuiBase;
import homeward.plugin.brewing.guis.BrewingBarrelGui;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;

import java.util.*;
import java.util.stream.Stream;

public class BrewingBarrelListener implements Listener {
    private final Map<Location, GuiBase> barrelGUIMap;

    {
        barrelGUIMap = new HashMap<>();
    }

    @EventHandler
    public void onPlayerInteract(CustomBlockInteractEvent event) {
        if (!event.getNamespacedID().contains("homeward:brewing_barrel_")) return;

        Location barrelLocation = event.getBlockClicked().getLocation();
        Player player = event.getPlayer();

        if (barrelGUIMap.containsKey(barrelLocation)) {
            barrelGUIMap.get(barrelLocation).setPlayer(player).open();
            return;
        }

        BrewingBarrelGui suicideConfirmMenu = new BrewingBarrelGui();
        suicideConfirmMenu.initialize();
        barrelGUIMap.put(barrelLocation, suicideConfirmMenu);

        suicideConfirmMenu.setPlayer(player).open();
    }

    @EventHandler
    public void onPlayerClickEvent(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        // prevent shift+click
        if (event.getView().getTopInventory().getHolder() instanceof GuiBase && event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
            event.setCancelled(true);
        }

        InventoryHolder holder = event.getClickedInventory().getHolder();
        if (!(holder instanceof GuiBase guiBase)) return;
        event.setCancelled(true);

        guiBase.handleClick(event);
    }

    @EventHandler
    public void onPlayerDragEvent(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof GuiBase guiBase)) return;

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
