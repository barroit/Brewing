package homeward.plugin.brewing.listeners;

import de.tr7zw.nbtapi.NBTFile;
import de.tr7zw.nbtapi.NBTItem;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import homeward.plugin.brewing.Brewing;
import homeward.plugin.brewing.constants.BaseInfo;
import homeward.plugin.brewing.guis.GuiBase;
import homeward.plugin.brewing.guis.BrewingBarrelGui;
import homeward.plugin.brewing.utils.AwesomeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BrewingBarrelListener implements Listener {
    private final Map<Location, GuiBase> barrelGUIMap;
    private final Map<HumanEntity, Location> barrelLocationMap;
    private final Set<Integer> manipulativeList;

    {
        barrelGUIMap = new HashMap<>();
        barrelLocationMap = new HashMap<>();
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

        BrewingBarrelGui brewingBarrelGui = new BrewingBarrelGui(BaseInfo.BARREL_TITLE);
        brewingBarrelGui.initialize();
        barrelGUIMap.put(barrelLocation, brewingBarrelGui);
        barrelLocationMap.put(player, barrelLocation);

        brewingBarrelGui.setPlayer(player).open();
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

    @EventHandler
    public void onPlayerCloseInventoryEvent(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof GuiBase)) return;

        Arrays.stream(event.getInventory().getStorageContents()).toList().forEach(v -> {
            if (v == null || new NBTItem(v).getBoolean("barrel_function_description")) return;
            // System.out.println(v.get);
        });

        HumanEntity player = event.getPlayer();

        if (!barrelLocationMap.containsKey(player)) return;
        Location barrelLocation = barrelLocationMap.get(player);

        try {
            NBTFile nbtFile = new NBTFile(new File(BaseInfo.PLUGIN_PATH + "nbt", barrelLocation.getWorld().getName() + "-barrel.nbt"));
            // nbtFile.setObject();
            nbtFile.save();
        } catch (IOException ignore) {}

    }
}
