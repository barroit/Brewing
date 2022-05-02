package homeward.plugin.brewing.guis;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.StorageGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Array;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BarrelGUI {

    public static StorageGui renderBarrelInterface() {
        StorageGui gui;

        gui = Gui.storage()
                .title(Component.text("Brewing barrel"))
                .rows(3)
                .create();


        ItemStack substrateSlot = new ItemStack(Material.PAPER);
        ItemMeta substrateSlotItemMeta = substrateSlot.getItemMeta();
        substrateSlotItemMeta.displayName(Component.text("底物 Substrate", NamedTextColor.YELLOW));
        substrateSlot.setItemMeta(substrateSlotItemMeta);

        GuiItem substrateSlotGuiItem = new GuiItem(substrateSlot);

        gui.setItem(1, 3, substrateSlotGuiItem);

        ItemStack restrictionSlot = new ItemStack(Material.PAPER);
        ItemMeta restrictionSlotItemMeta = substrateSlot.getItemMeta();
        restrictionSlotItemMeta.displayName(Component.text("抑制剂 Restriction", NamedTextColor.YELLOW));
        restrictionSlot.setItemMeta(restrictionSlotItemMeta);

        GuiItem restrictionSlotGuiItem = new GuiItem(restrictionSlot);

        gui.setItem(2, 3, restrictionSlotGuiItem);

        ItemStack yestSlot = new ItemStack(Material.PAPER);
        ItemMeta yestSlotItemMeta = yestSlot.getItemMeta();
        yestSlotItemMeta.displayName(Component.text("酵母 Yeast", NamedTextColor.YELLOW));
        yestSlot.setItemMeta(yestSlotItemMeta);

        GuiItem yestSlotGuiItem = new GuiItem(yestSlot);

        gui.setItem(3, 3, yestSlotGuiItem);

        ItemStack isBrewSlot = new ItemStack(Material.PAPER);
        ItemMeta isBrewItemMeta = isBrewSlot.getItemMeta();
        isBrewItemMeta.displayName(Component.text("是否开始", NamedTextColor.YELLOW));
        isBrewSlot.setItemMeta(isBrewItemMeta);

        GuiItem isBrewSlotGuiItem = new GuiItem(isBrewSlot);

        gui.setItem(1, 5, isBrewSlotGuiItem);
        gui.setItem(2, 5, isBrewSlotGuiItem);
        gui.setItem(3, 5, isBrewSlotGuiItem);

        ItemStack substrateStateSlot = new ItemStack(Material.PAPER);
        ItemMeta substrateStateSlotItemMeta = substrateStateSlot.getItemMeta();
        substrateStateSlotItemMeta.displayName(Component.text("是否添加底物", NamedTextColor.YELLOW));
        substrateStateSlot.setItemMeta(substrateStateSlotItemMeta);

        GuiItem substrateStateSlotGuiItem = new GuiItem(substrateStateSlot);

        gui.setItem(1, 7, substrateStateSlotGuiItem);

        ItemStack restrictionStateSlot = new ItemStack(Material.PAPER);
        ItemMeta restrictionStateSlotItemMeta = substrateStateSlot.getItemMeta();
        restrictionStateSlotItemMeta.displayName(Component.text("是否添加抑制剂", NamedTextColor.YELLOW));
        restrictionStateSlot.setItemMeta(restrictionStateSlotItemMeta);

        GuiItem restrictionStateSlotGuiItem = new GuiItem(restrictionStateSlot);

        gui.setItem(2, 7, restrictionStateSlotGuiItem);

        ItemStack yestStateSlot = new ItemStack(Material.PAPER);
        ItemMeta yestStateSlotItemMeta = yestSlot.getItemMeta();
        yestStateSlotItemMeta.displayName(Component.text("是否添加酵母", NamedTextColor.YELLOW));
        yestStateSlot.setItemMeta(yestStateSlotItemMeta);

        GuiItem yestStateSlotGuiItem = new GuiItem(yestStateSlot);

        gui.setItem(3, 7, yestStateSlotGuiItem);

        //Event
        gui.addSlotAction(1, 3, event -> {
            // Handle your open action
            if (event.getCursor().getType().equals(Material.AIR)) {
                event.setCancelled(true);
            }
        });

        gui.addSlotAction(2, 3, event -> {
            // Handle your open action
            if (event.getCursor().getType().equals(Material.AIR)) {
                event.setCancelled(true);
            }
        });

        gui.addSlotAction(3, 3, event -> {
            // Handle your open action
            if (event.getCursor().getType().equals(Material.AIR)) {
                event.setCancelled(true);
            }
        });

        gui.addSlotAction(1, 5, BarrelGUI::restrictSlotAction);

        gui.addSlotAction(2, 5, BarrelGUI::restrictSlotAction);

        gui.addSlotAction(3, 5, BarrelGUI::restrictSlotAction);

        gui.addSlotAction(1, 7, BarrelGUI::restrictSlotAction);

        gui.addSlotAction(2, 7, BarrelGUI::restrictSlotAction);

        gui.addSlotAction(3, 7, BarrelGUI::restrictSlotAction);

        return gui;

    }

    private static void restrictSlotAction(InventoryClickEvent event) {
        event.setCancelled(true);

    }

}
