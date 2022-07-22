package com.baioretto.brewing.listener;

import dev.triumphteam.gui.components.util.ItemNbt;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import lombok.SneakyThrows;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

public class MockTest implements Listener {
    // @SuppressWarnings("RedundantStringFormatCall")
    // @EventHandler
    @SneakyThrows
    public void onPlayerClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof PaginatedGui gui)) return;

        // System.out.println("event: " + event.getInventory());
        // System.out.println("gui: " + gui.getInventory());
        // System.out.println();

        try {
            GuiItem guiItem = gui.getGuiItem(event.getSlot());
            if (guiItem == null) return;
            Field uuid = guiItem.getClass().getDeclaredField("uuid");
            uuid.setAccessible(true);
            System.out.println("GuiItem: " + uuid.get(guiItem));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        System.out.println("ItemStack: " + ItemNbt.getString(item, "mf-gui"));


        // Method getPageItem = gui.getClass().getDeclaredMethod("getPageItem", int.class);
        // getPageItem.setAccessible(true);
        // GuiItem result = (GuiItem) getPageItem.invoke(gui, event.getSlot());
        // if (result == null) return;
        //
        // Field uuidField = result.getClass().getDeclaredField("uuid");
        // uuidField.setAccessible(true);
        // UUID uuid = (UUID) uuidField.get(result);
        // System.out.println(String.format("player: %s, GuiItem: %s\n", event.getWhoClicked().getName(), uuid));
        //
        // ItemStack currentItem = event.getCurrentItem();
        // if (currentItem == null) return;
        // String itemStackUUID = ItemNbt.getString(currentItem, "mf-gui");
        // System.out.println(String.format("player: %s, ItemStack: %s\n", event.getWhoClicked().getName(), itemStackUUID));
        // System.out.println();

        // not inventory
        // !pageItems
    }
}
