package homeward.plugin.brewing.listeners;

import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import homeward.plugin.brewing.guis.GuiBase;
import homeward.plugin.brewing.guis.BrewingBarrelGui;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;

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
        InventoryHolder holder = event.getClickedInventory().getHolder();
        if (!(holder instanceof GuiBase guiBase)) return;
        event.setCancelled(true);

        guiBase.handleClick(event);
    }

    @EventHandler
    public void onPlayerDragEvent(InventoryDragEvent event) {

    }
}
