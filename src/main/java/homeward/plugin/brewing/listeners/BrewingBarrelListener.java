package homeward.plugin.brewing.listeners;

import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import homeward.plugin.brewing.guis.GuiBase;
import homeward.plugin.brewing.guis.BrewingBarrelGui;
import homeward.plugin.brewing.utils.AwesomeUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.*;

public class BrewingBarrelListener implements Listener {
    private final Map<Location, GuiBase> barrelGUIMap;
    private final Set<Integer> manipulativeList;

    {
        barrelGUIMap = new HashMap<>();
        manipulativeList = new HashSet<>(Arrays.asList(2, 11, 20));
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

        // prevent shift+click and double click
        if (event.getView().getTopInventory().getHolder() instanceof GuiBase) {
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

        if (!(event.getClickedInventory().getHolder() instanceof GuiBase guiBase)) return;

        event.setCancelled(true);

        if (manipulativeList.contains(event.getSlot())) guiBase.handleClick(event);
    }

    @EventHandler
    public void onPlayerDragEvent(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof GuiBase)) return;
        AwesomeUtils.cancelDrag(event);
    }
}
