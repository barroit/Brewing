package com.baioretto.brewing.gui;

import com.baioretto.baiolib.api.extension.bukkit.BukkitImpl;
import com.baioretto.brewing.Container;
import com.baioretto.brewing.bean.OpenedBarrel;
import dev.triumphteam.gui.guis.BaseGui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import com.baioretto.brewing.enumerate.Item;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public abstract class GuiBase<C extends BaseGui> {
    protected final int pageSize;
    private final int rows;
    private final Set<Integer> untouchableZone;
    private final Component title;
    private final List<Consumer<C>> consumer;
    public C gui;
    protected final Location barrelLocation;
    protected boolean toNext = false;

    protected GuiBase(int pageSize, int rows, Location barrelLocation) {
        this.pageSize = pageSize;
        this.rows = rows;
        this.untouchableZone = untouchableZone();

        this.title = title();

        this.consumer = consumers();

        this.barrelLocation = barrelLocation;
    }

    @SuppressWarnings("unchecked")
    protected <E extends GuiBase<?>> E initPaginatedGui() {
        PaginatedGui gui = Gui.paginated().rows(rows).pageSize(pageSize).title(title).apply((Consumer<PaginatedGui>) actionAfterCreatingPaginationGui()).create();

        this.gui = (C) gui;

        if (consumer != null) {
            for (Consumer<C> c : consumer) {
                ((Consumer<PaginatedGui>) c).accept(gui);
            }
        }

        return (E) this;
    }

    protected abstract Component title();

    protected abstract @Nullable Set<Integer> untouchableZone();

    protected abstract List<Consumer<C>> consumers();

    protected abstract Consumer<C> openGuiAction();

    protected abstract void open(HumanEntity player, int openPage);

    private Consumer<C> defaultCloseGuiAction() {
        return gui -> gui.setCloseGuiAction(event -> {
            if (event.getViewers().size() != 1) return;

            if (this.toNext) {
                toNext = false;
                return;
            }

            OpenedBarrel openedBarrel = Container.OPENED_BARREL.get(barrelLocation);
            if (openedBarrel == null) return;

            if (openedBarrel.viewers().size() != 1) {
                openedBarrel.viewers().remove(event.getPlayer());
                return;
            }

            Container.OPENED_BARREL.remove(barrelLocation);

            openedBarrel.barrel().close();
            toNext = false;
        });
    }

    private Consumer<C> actionAfterCreatingPaginationGui() {
        return gui -> {
            setUntouchableZone(gui);
            setPlayerInteractGuiAction(gui);
            openGuiAction().accept(gui);
            defaultCloseGuiAction().accept(gui);
        };
    }

    private void setUntouchableZone(final BaseGui gui) {
        if (untouchableZone == null) return;
        GuiItem air = Item.AIR.getGuiItem();
        for (int index = 0; index < 54; index++) {
            if (!untouchableZone.contains(index)) continue;
            gui.setItem(index, air);
        }
    }

    private void setPlayerInteractGuiAction(final BaseGui gui) {
        gui.setDragAction(this::dragAction);
        gui.setDefaultTopClickAction(this::defaultTopClickAction);
        gui.setDefaultClickAction(this::defaultClickAction);
    }

    private void dragAction(final InventoryDragEvent event) {
        Set<Integer> rawSlots = event.getRawSlots();
        if (rawSlots.size() == 0) return;
        int min = rawSlots.stream().mapToInt(Integer::intValue).min().getAsInt();
        if (min < 54) event.setCancelled(true);
    }

    private void defaultClickAction(final InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
            event.setCancelled(true);
            return;
        }
        if (event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
            if (event.getCursor() == null) return;
            if (event.getView().getTopInventory().contains(event.getCursor().getType())) {
                event.setCancelled(true);
            }
        }
    }

    private void defaultTopClickAction(final InventoryClickEvent event) {
        event.setCancelled(true);
    }

    protected void updateTitle(Component title) {
        updateTitle(title, 1);
    }

    @SuppressWarnings("SameParameterValue")
    protected void updateTitle(Component title, int pageNum) {
        gui.setUpdating(true);

        final List<HumanEntity> viewers = new ArrayList<>(gui.getInventory().getViewers());

        gui.setInventory(BukkitImpl.createInventory(gui, gui.getInventory().getSize(), title));

        for (final HumanEntity player : viewers) {
            open(player, pageNum);
        }

        gui.setUpdating(false);
    }

    @Getter
    private final ItemStack
            nextButton = Item.NEXT_BUTTON.getItemStack(),
            prevButton = Item.PREV_BUTTON.getItemStack(),
            nextButtonDim = Item.NEXT_BUTTON_DIM.getItemStack(),
            prevButtonDim = Item.PREV_BUTTON_DIM.getItemStack();

    @Getter
    private final GuiItem
            nextButtonGuiItem = new GuiItem(nextButton),
            prevButtonGuiItem = new GuiItem(prevButton);
}