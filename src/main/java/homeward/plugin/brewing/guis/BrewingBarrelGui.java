package homeward.plugin.brewing.guis;

import de.tr7zw.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BrewingBarrelGui extends GuiBase {
    private final ItemStack air = new ItemStack(Material.AIR);

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        ItemStack cursor = event.getCursor();
        if (cursor == null || clickedInventory == null) return;

        boolean cursorIsAir = cursor.getType() == Material.AIR;
        int eventSlot = event.getSlot();
        HumanEntity player = event.getWhoClicked();

        ItemStack rawItem = clickedInventory.getItem(event.getSlot());
        NBTItem rawNbtItem = new NBTItem(rawItem);

        if (rawNbtItem.getBoolean("default")) {
            clickedInventory.setItem(eventSlot, cursor);
            player.setItemOnCursor(air);
        } else if (cursorIsAir) {
            player.setItemOnCursor(clickedInventory.getItem(eventSlot));
            switch (eventSlot) {
                case 2 -> this.spawnSubstrateSlot();
                case 11 -> this.spawnRestrictionSlot();
                case 20 -> this.spawnYeastSlot();
            }
        } else {
            ItemStack itemInInventory = clickedInventory.getItem(eventSlot);
            ItemStack itemOnCursor = player.getItemOnCursor();
            player.setItemOnCursor(itemInInventory);
            clickedInventory.setItem(eventSlot, itemOnCursor);
        }
    }

    @Override
    public void setGuiItems() {
        spawnSubstrateSlot();
        spawnRestrictionSlot();
        spawnYeastSlot();

        ItemStack isBrewSlot = new ItemStack(Material.PAPER);
        ItemMeta isBrewItemMeta = isBrewSlot.getItemMeta();
        isBrewItemMeta.displayName(Component.text("是否开始", NamedTextColor.YELLOW));
        isBrewSlot.setItemMeta(isBrewItemMeta);

        inventory.setItem(4, isBrewSlot);
        inventory.setItem(13, isBrewSlot);
        inventory.setItem(22, isBrewSlot);

        ItemStack substrateStateSlot = new ItemStack(Material.PAPER);
        ItemMeta substrateStateSlotItemMeta = substrateStateSlot.getItemMeta();
        substrateStateSlotItemMeta.displayName(Component.text("是否添加底物", NamedTextColor.YELLOW));
        substrateStateSlot.setItemMeta(substrateStateSlotItemMeta);

        inventory.setItem(6, substrateStateSlot);

        ItemStack restrictionStateSlot = new ItemStack(Material.PAPER);
        ItemMeta restrictionStateSlotItemMeta = restrictionStateSlot.getItemMeta();
        restrictionStateSlotItemMeta.displayName(Component.text("是否添加抑制剂", NamedTextColor.YELLOW));
        restrictionStateSlot.setItemMeta(restrictionStateSlotItemMeta);

        inventory.setItem(15, restrictionStateSlot);

        ItemStack yeastStateSlot = new ItemStack(Material.PAPER);
        ItemMeta yeastStateSlotItemMeta = yeastStateSlot.getItemMeta();
        yeastStateSlotItemMeta.displayName(Component.text("是否添加酵母", NamedTextColor.YELLOW));
        yeastStateSlot.setItemMeta(yeastStateSlotItemMeta);

        inventory.setItem(24, yeastStateSlot);
    }

    private void spawnSubstrateSlot() {
        ItemStack substrateSlot = new ItemStack(Material.PAPER);
        ItemMeta substrateSlotItemMeta = substrateSlot.getItemMeta();
        substrateSlotItemMeta.displayName(Component.text("底物 Substrate", NamedTextColor.YELLOW));
        substrateSlot.setItemMeta(substrateSlotItemMeta);
        NBTItem nbtItem = new NBTItem(substrateSlot);
        nbtItem.setBoolean("default", true);
        inventory.setItem(2, nbtItem.getItem());
    }

    private void spawnRestrictionSlot() {
        ItemStack restrictionSlot = new ItemStack(Material.PAPER);
        ItemMeta restrictionSlotItemMeta = restrictionSlot.getItemMeta();
        restrictionSlotItemMeta.displayName(Component.text("抑制剂 Restriction", NamedTextColor.YELLOW));
        restrictionSlot.setItemMeta(restrictionSlotItemMeta);
        NBTItem nbtItem = new NBTItem(restrictionSlot);
        nbtItem.setBoolean("default", true);
        inventory.setItem(11, nbtItem.getItem());
    }

    private void spawnYeastSlot() {
        ItemStack yeastSlot = new ItemStack(Material.PAPER);
        ItemMeta yeastSlotItemMeta = yeastSlot.getItemMeta();
        yeastSlotItemMeta.displayName(Component.text("酵母 Yeast", NamedTextColor.YELLOW));
        yeastSlot.setItemMeta(yeastSlotItemMeta);
        NBTItem nbtItem = new NBTItem(yeastSlot);
        nbtItem.setBoolean("default", true);
        inventory.setItem(20, nbtItem.getItem());
    }
}
