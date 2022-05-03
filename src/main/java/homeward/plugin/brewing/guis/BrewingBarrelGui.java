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

    public BrewingBarrelGui(Component title) {
        super(title);
    }

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
        if (rawItem == null) return;
        NBTItem rawNbtItem = new NBTItem(rawItem);

        Boolean isDescription = rawNbtItem.getBoolean("barrel_function_description");

        if (isDescription && !cursorIsAir) {
            clickedInventory.setItem(eventSlot, cursor);
            player.setItemOnCursor(air);
            return;
        }

        if (!isDescription && cursorIsAir) {
            player.setItemOnCursor(clickedInventory.getItem(eventSlot));
            switch (eventSlot) {
                case 2 -> this.spawnSubstrateSlot();
                case 11 -> this.spawnRestrictionSlot();
                case 20 -> this.spawnYeastSlot();
            }
            return;
        }

        if (!isDescription) {
            ItemStack itemInInventory = clickedInventory.getItem(eventSlot);
            ItemStack itemOnCursor = player.getItemOnCursor();
            player.setItemOnCursor(itemInInventory);
            clickedInventory.setItem(eventSlot, itemOnCursor);
        }
    }

    @Override
    public void setGuiItems() {
        this.spawnSubstrateSlot();
        this.spawnRestrictionSlot();
        this.spawnYeastSlot();

        this.spawnIsBrewSlot();

        this.spawnSubstrateStateSlot();
        this.spawnRestrictionStateSlot();
        this.spawnYeastStateSlot();
    }

    // region Define Description Spawner
    private void spawnSubstrateSlot() {
        ItemStack substrateSlot = new ItemStack(Material.PAPER);
        ItemMeta substrateSlotItemMeta = substrateSlot.getItemMeta();
        substrateSlotItemMeta.displayName(Component.text("底物 Substrate", NamedTextColor.YELLOW));
        substrateSlot.setItemMeta(substrateSlotItemMeta);
        NBTItem nbtItem = new NBTItem(substrateSlot);
        nbtItem.setBoolean("barrel_function_description", true);
        inventory.setItem(2, nbtItem.getItem());
    }

    private void spawnRestrictionSlot() {
        ItemStack restrictionSlot = new ItemStack(Material.PAPER);
        ItemMeta restrictionSlotItemMeta = restrictionSlot.getItemMeta();
        restrictionSlotItemMeta.displayName(Component.text("抑制剂 Restriction", NamedTextColor.YELLOW));
        restrictionSlot.setItemMeta(restrictionSlotItemMeta);
        NBTItem nbtItem = new NBTItem(restrictionSlot);
        nbtItem.setBoolean("barrel_function_description", true);
        inventory.setItem(11, nbtItem.getItem());
    }

    private void spawnYeastSlot() {
        ItemStack yeastSlot = new ItemStack(Material.PAPER);
        ItemMeta yeastSlotItemMeta = yeastSlot.getItemMeta();
        yeastSlotItemMeta.displayName(Component.text("酵母 Yeast", NamedTextColor.YELLOW));
        yeastSlot.setItemMeta(yeastSlotItemMeta);
        NBTItem nbtItem = new NBTItem(yeastSlot);
        nbtItem.setBoolean("barrel_function_description", true);
        inventory.setItem(20, nbtItem.getItem());
    }

    private void spawnIsBrewSlot() {
        ItemStack isBrewSlot = new ItemStack(Material.PAPER);
        ItemMeta isBrewItemMeta = isBrewSlot.getItemMeta();
        isBrewItemMeta.displayName(Component.text("是否开始", NamedTextColor.YELLOW));
        isBrewSlot.setItemMeta(isBrewItemMeta);
        NBTItem nbtItem = new NBTItem(isBrewSlot);
        nbtItem.setBoolean("barrel_function_description", true);
        inventory.setItem(4, nbtItem.getItem());
        inventory.setItem(13, nbtItem.getItem());
        inventory.setItem(22, nbtItem.getItem());
    }

    private void spawnSubstrateStateSlot() {
        ItemStack substrateStateSlot = new ItemStack(Material.PAPER);
        ItemMeta substrateStateSlotItemMeta = substrateStateSlot.getItemMeta();
        substrateStateSlotItemMeta.displayName(Component.text("是否添加底物", NamedTextColor.YELLOW));
        substrateStateSlot.setItemMeta(substrateStateSlotItemMeta);
        NBTItem nbtItem = new NBTItem(substrateStateSlot);
        nbtItem.setBoolean("barrel_function_description", true);
        inventory.setItem(6, nbtItem.getItem());
    }

    private void spawnRestrictionStateSlot() {
        ItemStack restrictionStateSlot = new ItemStack(Material.PAPER);
        ItemMeta restrictionStateSlotItemMeta = restrictionStateSlot.getItemMeta();
        restrictionStateSlotItemMeta.displayName(Component.text("是否添加抑制剂", NamedTextColor.YELLOW));
        restrictionStateSlot.setItemMeta(restrictionStateSlotItemMeta);
        NBTItem nbtItem = new NBTItem(restrictionStateSlot);
        nbtItem.setBoolean("barrel_function_description", true);
        inventory.setItem(15, nbtItem.getItem());
    }

    private void spawnYeastStateSlot() {
        ItemStack yeastStateSlot = new ItemStack(Material.PAPER);
        ItemMeta yeastStateSlotItemMeta = yeastStateSlot.getItemMeta();
        yeastStateSlotItemMeta.displayName(Component.text("是否添加酵母", NamedTextColor.YELLOW));
        yeastStateSlot.setItemMeta(yeastStateSlotItemMeta);
        NBTItem nbtItem = new NBTItem(yeastStateSlot);
        nbtItem.setBoolean("barrel_function_description", true);
        inventory.setItem(24, nbtItem.getItem());
    }
    // endregion
}
