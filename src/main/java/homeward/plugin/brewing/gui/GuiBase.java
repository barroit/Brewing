package homeward.plugin.brewing.gui;

import dev.triumphteam.gui.guis.BaseGui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import homeward.plugin.brewing.enumerate.Item;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings({"JavadocDeclaration", "SameParameterValue"})
public abstract class GuiBase<C extends BaseGui> {
    protected final int pageSize;
    private final int rows;
    private final Set<Integer> untouchableZone;
    private final Component title;
    private final List<Consumer<C>> consumer;
    protected C gui;

    protected GuiBase(int pageSize, int rows) {
        this.pageSize = pageSize;
        this.rows = rows;
        this.untouchableZone = untouchableZone();

        this.title = title();

        this.consumer = consumer();
    }

    /**
     * get gui title
     *
     * @return title component
     */
    protected abstract Component title();

    /**
     * get paginated builder
     *
     * @return paginated builder
     */
    @SuppressWarnings("unchecked")
    protected PaginatedGui getPaginatedGui() {
        PaginatedGui gui = Gui.paginated().rows(rows).pageSize(pageSize).title(title).apply((Consumer<PaginatedGui>) actionAfterCreatingPaginationGui()).create();
        this.gui = (C) gui;
        if (consumer != null) {
            for (Consumer<C> c : consumer) {
                ((Consumer<PaginatedGui>) c).accept(gui);
            }
        }
        return gui;
    }

    protected abstract @Nullable Set<Integer> untouchableZone();

    protected abstract List<Consumer<C>> consumer();

    protected abstract Consumer<C> openGuiAction();

    /**
     * apply all action
     *
     * @return Consumer
     */
    private Consumer<C> actionAfterCreatingPaginationGui() {
        return gui -> {
            setUntouchableZone(gui);
            setPlayerInteractGuiAction(gui);
            openGuiAction().accept(gui);
        };
    }

    /**
     * set untouchable zone
     *
     * @param gui
     */
    private void setUntouchableZone(final BaseGui gui) {
        if (untouchableZone == null) return;
        GuiItem air = Item.AIR.getGuiItem();
        for (int index = 0; index < 54; index++) {
            if (!untouchableZone.contains(index)) continue;
            gui.setItem(index, air);
        }
    }

    /**
     * prevent click/double-click/ctrl+click/drag action
     *
     * @param gui
     */
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
}
