package homeward.plugin.brewing.listeners;

import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import homeward.plugin.brewing.enumerates.ComponentEnum;
import homeward.plugin.brewing.guis.GuiBase;
import homeward.plugin.brewing.guis.BrewingBarrelGui;
import homeward.plugin.brewing.utils.InventoryUtils;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.*;

import static homeward.plugin.brewing.constants.BaseInfo.BARREL_DESCRIPTION_MANIPULATIVE_LIST;

public class BrewingBarrelListener implements Listener {
    private static final Map<Location, GuiBase> barrelGUIMap = new HashMap<>();;

    @EventHandler
    public void onPlayerInteract(CustomBlockInteractEvent event) {
        if (!event.getNamespacedID().contains("homeward:brewing_barrel_")) return;

        Location barrelLocation = event.getBlockClicked().getLocation();
        Player player = event.getPlayer();

        if (barrelGUIMap.containsKey(barrelLocation)) {
            barrelGUIMap.get(barrelLocation).open(player);
            return;
        }

        BrewingBarrelGui brewingBarrelGui = new BrewingBarrelGui(ComponentEnum.BARREL_TITLE, 27);
        barrelGUIMap.put(barrelLocation, brewingBarrelGui);
        brewingBarrelGui.open(player);
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

        if (BARREL_DESCRIPTION_MANIPULATIVE_LIST.contains(event.getSlot())) guiBase.handleClick(event);
    }

    @EventHandler
    public void onPlayerDragEvent(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof GuiBase)) return;
        InventoryUtils.cancelDrag(event);
    }

    @EventHandler
    public void onPlayerCloseInventoryEvent(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof GuiBase)) return;


        // Arrays.stream(event.getInventory().getStorageContents()).toList().forEach(v -> {
        //     if (v == null || BARREL_DESCRIPTION_CUSTOM_MODEL_DATA_LIST.contains(new NBTItem(v).getInteger("CustomModelData"))) return;
        //     // do something here
        // });
        //
        //
        // try {
        //     NBTFile nbtFile = new NBTFile(new File(BaseInfo.PLUGIN_PATH + "nbt", barrelLocation.getWorld().getName() + "-barrel.nbt"));
        //     // nbtFile.setObject();
        //     // do something here
        //     nbtFile.save();
        // } catch (IOException ignore) {}
    }

    public static Map<Location, GuiBase> getBarrelGUIMap() {
        return barrelGUIMap;
    }
}
