package homeward.plugin.brewing.utils;

import dev.triumphteam.gui.components.ScrollType;
import dev.triumphteam.gui.guis.*;
import homeward.plugin.brewing.enumerates.ComponentEnum;
import homeward.plugin.brewing.enumerates.EnumBase;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.Set;

/**
 * A super awesome utilities
 *
 * @author Baioretto
 * @version 1.0.0
 */
public class InventoryUtils {

    public static ItemStack generateSlotItem(Material material, EnumBase title, Integer customModelData) {
        ItemStack slot = new ItemStack(material);
        ItemMeta slotMeta = slot.getItemMeta();
        slotMeta.displayName(title.getComponent());
        slotMeta.setCustomModelData(customModelData);
        slot.setItemMeta(slotMeta);
        return slot;
    }

    public static Gui generateGui(int rows, EnumBase title) {
        return Gui.gui().title(title.getComponent()).rows(rows).create();
    }

    public static StorageGui generateStorageGui(int rows, EnumBase title) {
        return Gui.storage().title(title.getComponent()).rows(rows).create();
    }

    public static ScrollingGui generateScrollingGui(int rows, EnumBase title, int pageSize, ScrollType scrollType) {
        return Gui.scrolling().title(title.getComponent()).rows(rows).pageSize(pageSize).scrollType(scrollType).create();
    }

    public static PaginatedGui generatePaginatedGui(int rows, EnumBase title, int pageSize, ScrollType scrollType) {
        return Gui.paginated().title(title.getComponent()).rows(rows).pageSize(pageSize).create();
    }

    public static ItemStack substrateSlot = generateSlotItem(Material.PAPER, ComponentEnum.SLOT_SUBSTRATE, 4501);
    public static ItemStack restrictionSlot = generateSlotItem(Material.PAPER, ComponentEnum.SLOT_RESTRICTION, 4502);
    public static ItemStack yeastSlot = generateSlotItem(Material.PAPER, ComponentEnum.SLOT_YEAST, 4503);
    public static ItemStack barrelSlot = generateSlotItem(Material.PAPER, ComponentEnum.SLOT_BARREL, 4500);
    public static ItemStack substrateSlotState = generateSlotItem(Material.PAPER, ComponentEnum.SLOT_SUBSTRATE_STATE, 4500);
    public static ItemStack restrictionSlotState = generateSlotItem(Material.PAPER, ComponentEnum.SLOT_RESTRICTION_STATE, 4500);
    public static ItemStack yeastSlotState = generateSlotItem(Material.PAPER, ComponentEnum.SLOT_YEAST_STATE, 4500);
}