package homeward.plugin.brewing.listeners;

import de.tr7zw.nbtapi.NBTItem;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.StorageGui;
import homeward.plugin.brewing.constants.BaseInfo;
import homeward.plugin.brewing.enumerates.ComponentEnum;
import homeward.plugin.brewing.enumerates.EnumBase;
import homeward.plugin.brewing.guis.GuiBase;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static homeward.plugin.brewing.utils.InventoryUtils.*;

public class BrewingBarrelListener implements Listener {
    private static final Map<Location, BaseGui> barrelGUIMap = new HashMap<>();
    private final ItemStack air = new ItemStack(Material.AIR);

    @EventHandler
    public void onPlayerInteract(CustomBlockInteractEvent event) {
        if (!event.getNamespacedID().contains("homeward:brewing_barrel_")) return;

        Location barrelLocation = event.getBlockClicked().getLocation();
        Player player = event.getPlayer();

        if (barrelGUIMap.containsKey(barrelLocation)) {
            barrelGUIMap.get(barrelLocation).open(player);
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

        if (!(event.getClickedInventory().getHolder() instanceof StorageGui gui)) return;

        event.setCancelled(true);

        handleClick(event, gui);
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
        if (!(event.getInventory().getHolder() instanceof GuiBase)) return;
        // do something here...
    }

    private void handleClick(InventoryClickEvent event, StorageGui gui) {
        ItemStack cursor = event.getCursor();
        if (cursor == null || gui == null) return;

        boolean cursorIsAir = cursor.getType() == Material.AIR;
        int eventSlot = event.getSlot();
        HumanEntity player = event.getWhoClicked();

        GuiItem guiItem = gui.getGuiItem(event.getSlot());
        if (guiItem == null) return;
        ItemStack rawItem = guiItem.getItemStack();

        boolean isDescription = BaseInfo.BARREL_DESCRIPTION_CUSTOM_MODEL_DATA_LIST.contains(new NBTItem(rawItem).getInteger("CustomModelData"));

        // switch (eventSlot) {
        //     case 2 -> setTitle(ComponentEnum.BARREL_TITLE_WITH_SUBSTRATE, gui);
        //     case 11 -> setTitle(ComponentEnum.BARREL_TITLE_WITH_RESTRICTION, gui);
        //     case 20 -> setTitle(ComponentEnum.BARREL_TITLE_WITH_YEAST, gui);
        // }

        if (isDescription && !cursorIsAir) {
            gui.updateItem(eventSlot, cursor);
            player.setItemOnCursor(air);
            return;
        }

        GuiItem itemInSlot = gui.getGuiItem(eventSlot);
        if (itemInSlot == null) return;

        NBTItem itemNBT = new NBTItem(itemInSlot.getItemStack());
        itemNBT.removeKey("PublicBukkitValues");
        itemNBT.getKeys().forEach(System.out::println);
        ItemStack itemWithoutTag = itemNBT.getItem();


        if (!isDescription && cursorIsAir) {
            player.setItemOnCursor(itemWithoutTag);
            switch (eventSlot) {
                case 2 -> gui.updateItem(2, substrateSlot);
                case 11 -> gui.updateItem(11, restrictionSlot);
                case 20 -> gui.updateItem(20, yeastSlot);
            }
            return;
        }

        if (!isDescription) {
            ItemStack itemOnCursor = player.getItemOnCursor();
            player.setItemOnCursor(itemWithoutTag);
            gui.updateItem(eventSlot, itemOnCursor);
        }
    }

    private void setTitle(EnumBase enumBase, StorageGui gui) {
        TextComponent component = (TextComponent) enumBase.getComponent();
        TextColor color = component.style().color();
        String rawTitle = (color == null ? "" : ChatColor.valueOf(color.toString().toUpperCase()) + "") + component.content();
        gui.updateTitle(rawTitle);
    }
}
