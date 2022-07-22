package com.baioretto.brewing.util;

import com.baioretto.brewing.exception.BrewingInternalException;
import com.baioretto.brewing.gui.GuiBase;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import com.baioretto.brewing.enumerate.Title;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@UtilityClass
public class GuiUtils {
    public Component getTitle(Title... title) {
        return getTitle(NamedTextColor.WHITE, title);
    }

    public Component getTitle(NamedTextColor color, Title... title) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(title).toList().forEach(c -> sb.append(((TextComponent) c.getComponent()).content()));
        return Component.text(sb.toString(), color);
    }

    public Consumer<PaginatedGui> guiButtonConsumer(GuiBase<?> guiContainer, int previousButtonSlot, int nextButtonSlot) {
        return gui -> {
            guiContainer.nextButtonGuiItem().setAction(event -> {
                if (event.getAction().equals(InventoryAction.NOTHING)) {
                    return; // prevent skip
                }
                if (gui.getNextPageNum() >= gui.getPagesNum()) {
                    gui.updateItem(nextButtonSlot, guiContainer.nextButtonDim());
                }
                GuiItem guiItem = gui.getGuiItem(previousButtonSlot);
                if (gui.getNextPageNum() > 1 && guiItem != null && guiItem.getItemStack().equals(guiContainer.prevButtonDim())) {
                    gui.updateItem(previousButtonSlot, guiContainer.prevButton());
                }
                gui.next();
            });
            guiContainer.prevButtonGuiItem().setAction(event -> {
                if (event.getAction().equals(InventoryAction.NOTHING)) {
                    return;
                }
                if (gui.getPrevPageNum() - 1 == 0) {
                    gui.updateItem(previousButtonSlot, guiContainer.prevButtonDim());
                }
                GuiItem guiItem = gui.getGuiItem(nextButtonSlot);
                if (gui.getNextPageNum() > 1 && guiItem != null && guiItem.getItemStack().equals(guiContainer.nextButtonDim())) {
                    gui.updateItem(nextButtonSlot, guiContainer.nextButton());
                }
                gui.previous();
            });

            gui.setItem(previousButtonSlot, guiContainer.prevButtonGuiItem());
            gui.setItem(nextButtonSlot, guiContainer.nextButtonGuiItem());
        };
    }

    public Consumer<PaginatedGui> updateButtonState(GuiBase<?> guiContainer, int previousButtonSlot, int nextButtonSlot) {
        return gui -> {
            gui.updateItem(previousButtonSlot, guiContainer.prevButtonDim());
            if (gui.getCurrentPageNum() >= gui.getPagesNum()) {
                gui.updateItem(nextButtonSlot, guiContainer.nextButtonDim());
            } else {
                gui.updateItem(nextButtonSlot, guiContainer.nextButton());
            }
        };
    }

    @SuppressWarnings("unchecked")
    public void paginatedGuiOpen(PaginatedGui gui, HumanEntity player, int openPage) {
        if (player.isSleeping()) return;
        if (openPage <= gui.getPagesNum() || openPage > 0) {
            try {
                pageNumField.set(gui, openPage);
            } catch (IllegalAccessException e) {
                throw new BrewingInternalException(e);
            }
        }

        gui.getInventory().clear();
        try {
            Map<Integer, GuiItem> currentPage = (Map<Integer, GuiItem>) currentPageField.get(gui);
            currentPage.clear();
        } catch (IllegalAccessException e) {
            throw new BrewingInternalException(e);
        }

        populateGui(gui);

        try {
            int pageSize = (int) pageSizeField.get(gui);
            if (pageSize == 0) pageSizeField.set(gui, calculatePageSize(gui));
        } catch (IllegalAccessException e) {
            throw new BrewingInternalException(e);
        }

        populatePage(gui);

        player.openInventory(gui.getInventory());
    }

    @SuppressWarnings("unchecked")
    private void populatePage(PaginatedGui gui) {
        try {
            for (final GuiItem guiItem : (List<GuiItem>) getPageNumMethod.invoke(gui, gui.getCurrentPageNum())) {
                for (int slot = 0; slot < gui.getRows() * 9; slot++) {
                    if (gui.getGuiItem(slot) != null || gui.getInventory().getItem(slot) != null) continue;
                    ((Map<Integer, GuiItem>) currentPageField.get(gui)).put(slot, guiItem);
                    gui.getInventory().setItem(slot, getFixedItemStack(guiItem));
                    break;
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BrewingInternalException(e);
        }
    }

    public void populateGui(PaginatedGui gui) {
        Inventory inventory = gui.getInventory();
        for (final Map.Entry<Integer, GuiItem> entry : gui.getGuiItems().entrySet()) {
            // fixed uuid not match
            inventory.setItem(entry.getKey(), getFixedItemStack(entry.getValue()));
        }
    }

    public int calculatePageSize(PaginatedGui gui) {
        int counter = 0;

        for (int slot = 0; slot < gui.getRows() * 9; slot++) {
            if (gui.getInventory().getItem(slot) == null) counter++;
        }

        return counter;
    }

    public ItemStack getFixedItemStack(GuiItem guiItem) {
        guiItem.setItemStack(guiItem.getItemStack());
        return guiItem.getItemStack();
    }

    private static final Class<PaginatedGui> guiClass = PaginatedGui.class;
    private static final Field
            pageNumField,
            currentPageField,
            pageSizeField;
    private static final Method getPageNumMethod;

    static {
        try {
            pageNumField = guiClass.getDeclaredField("pageNum");
            pageNumField.setAccessible(true);

            currentPageField = guiClass.getDeclaredField("currentPage");
            currentPageField.setAccessible(true);

            pageSizeField = guiClass.getDeclaredField("pageSize");
            pageSizeField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new BrewingInternalException(e);
        }

        try {
            getPageNumMethod = guiClass.getDeclaredMethod("getPageNum", int.class);
            getPageNumMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new BrewingInternalException(e);
        }
    }

    // todo remove test method
    public void setRecipesShowcase(PaginatedGui paginatedGui) {
        for (var i = 0; i < 10; i++) {
            paginatedGui.addItem(new GuiItem(Material.RED_WOOL));
        }
        for (var i = 0; i < 10; i++) {
            paginatedGui.addItem(new GuiItem(Material.GREEN_WOOL));
        }
        for (var i = 0; i < 10; i++) {
            paginatedGui.addItem(new GuiItem(Material.PINK_WOOL));
        }
        for (var i = 0; i < 10; i++) {
            paginatedGui.addItem(new GuiItem(Material.BLACK_WOOL));
        }
        for (var i = 0; i < 10; i++) {
            paginatedGui.addItem(new GuiItem(Material.PURPLE_WOOL));
        }
        for (var i = 0; i < 10; i++) {
            paginatedGui.addItem(new GuiItem(Material.CYAN_WOOL));
        }
        for (var i = 0; i < 10; i++) {
            paginatedGui.addItem(new GuiItem(Material.BLUE_WOOL));
        }
        for (var i = 0; i < 10; i++) {
            paginatedGui.addItem(new GuiItem(Material.BROWN_WOOL));
        }
        for (var i = 0; i < 10; i++) {
            paginatedGui.addItem(new GuiItem(Material.WHITE_WOOL));
        }
        for (var i = 0; i < 10; i++) {
            paginatedGui.addItem(new GuiItem(Material.GRAY_WOOL));
        }
        for (var i = 0; i < 10; i++) {
            paginatedGui.addItem(new GuiItem(Material.YELLOW_WOOL));
        }
        for (var i = 0; i < 10; i++) {
            paginatedGui.addItem(new GuiItem(Material.LIME_WOOL));
        }
        for (var i = 0; i < 10; i++) {
            paginatedGui.addItem(new GuiItem(Material.ORANGE_WOOL));
        }
        for (var i = 0; i < 10; i++) {
            paginatedGui.addItem(new GuiItem(Material.MAGENTA_WOOL));
        }
    }
}
