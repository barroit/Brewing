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
        if (event.getCurrentItem() == null) return;

    }

    @Override
    public void handleDrag(InventoryDragEvent event) {

    }

    @Override
    public void setGuiItems() {
        ItemStack substrateSlot = new ItemStack(Material.PAPER);
        ItemMeta substrateSlotItemMeta = substrateSlot.getItemMeta();
        substrateSlotItemMeta.displayName(Component.text("底物 Substrate", NamedTextColor.YELLOW));
        substrateSlot.setItemMeta(substrateSlotItemMeta);

        inventory.setItem(2, substrateSlot);

        ItemStack restrictionSlot = new ItemStack(Material.PAPER);
        ItemMeta restrictionSlotItemMeta = substrateSlot.getItemMeta();
        restrictionSlotItemMeta.displayName(Component.text("抑制剂 Restriction", NamedTextColor.YELLOW));
        restrictionSlot.setItemMeta(restrictionSlotItemMeta);

        inventory.setItem(11, restrictionSlot);

        ItemStack yestSlot = new ItemStack(Material.PAPER);
        ItemMeta yestSlotItemMeta = yestSlot.getItemMeta();
        yestSlotItemMeta.displayName(Component.text("酵母 Yeast", NamedTextColor.YELLOW));
        yestSlot.setItemMeta(yestSlotItemMeta);

        inventory.setItem(20, yestSlot);

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
        ItemMeta restrictionStateSlotItemMeta = substrateStateSlot.getItemMeta();
        restrictionStateSlotItemMeta.displayName(Component.text("是否添加抑制剂", NamedTextColor.YELLOW));
        restrictionStateSlot.setItemMeta(restrictionStateSlotItemMeta);

        inventory.setItem(15, restrictionStateSlot);

        ItemStack yestStateSlot = new ItemStack(Material.PAPER);
        ItemMeta yestStateSlotItemMeta = yestSlot.getItemMeta();
        yestStateSlotItemMeta.displayName(Component.text("是否添加酵母", NamedTextColor.YELLOW));
        yestStateSlot.setItemMeta(yestStateSlotItemMeta);

        inventory.setItem(24, yestStateSlot);
    }
}
