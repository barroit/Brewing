package homeward.plugin.brewing.guis;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BrewingBarrelGui extends GuiBase {

    @Override
    public String getGuiName() {
        return "Confirm: Kill yourself?";
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) return;

        int eventSlot = event.getSlot();
        event.getClickedInventory().setItem(eventSlot, event.getCursor());

    }

    @Override
    public void handleDrag(InventoryDragEvent event) {

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
        inventory.setItem(2, substrateSlot);
    }

    private void spawnRestrictionSlot() {
        ItemStack restrictionSlot = new ItemStack(Material.PAPER);
        ItemMeta restrictionSlotItemMeta = restrictionSlot.getItemMeta();
        restrictionSlotItemMeta.displayName(Component.text("抑制剂 Restriction", NamedTextColor.YELLOW));
        restrictionSlot.setItemMeta(restrictionSlotItemMeta);
        inventory.setItem(11, restrictionSlot);
    }

    private void spawnYeastSlot() {
        ItemStack yeastSlot = new ItemStack(Material.PAPER);
        ItemMeta yeastSlotItemMeta = yeastSlot.getItemMeta();
        yeastSlotItemMeta.displayName(Component.text("酵母 Yeast", NamedTextColor.YELLOW));
        yeastSlot.setItemMeta(yeastSlotItemMeta);
        inventory.setItem(20, yeastSlot);
    }
}
