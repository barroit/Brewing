package homeward.plugin.brewing.gui;

import dev.triumphteam.gui.guis.BaseGui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import homeward.plugin.brewing.enumerate.Item;
import homeward.plugin.brewing.enumerate.Title;
import homeward.plugin.brewing.enumerate.Type;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings({"JavadocDeclaration", "SameParameterValue"})
public abstract class GuiBase<C extends BaseGui> {
    protected final Map<String, ItemStack> recipeTier;
    protected final int recipeTierSize;
    protected final int pageSize;
    private final int rows;
    private final Set<Integer> untouchableZone;
    private final Type guiType;
    private final Component title;
    private final List<Consumer<C>> consumer;

    protected GuiBase(Map<String, ItemStack> recipeTier, int pageSize, int rows, Type guiType) {
        this.recipeTier = recipeTier;
        this.recipeTierSize = recipeTier.size();

        this.pageSize = pageSize;
        this.rows = rows;
        this.untouchableZone = untouchableZone();

        this.guiType = guiType;
        this.title = getTitle();

        this.consumer = consumer();
    }

    /**
     * get gui title
     * @return title component
     */
    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private Component getTitle() {
        switch (guiType) {
            case RECIPES_PREVIEW_GUI -> {
                switch (recipeTier.size()) {
                    case 1 -> {
                        return Title.getRecipeTitle(Title.Amount.x1);
                    }
                    case 2 -> {
                        return Title.getRecipeTitle(Title.Amount.x2);
                    }
                    case 3 -> {
                        return Title.getRecipeTitle(Title.Amount.x3);
                    }
                    case 4 -> {
                        return Title.getRecipeTitle(Title.Amount.x4);
                    }
                    default -> throw new IllegalStateException();
                }
            }
            default -> throw new IllegalArgumentException();
        }
    }

    /**
     * get paginated builder
     * @return paginated builder
     */
    @SuppressWarnings("unchecked")
    protected PaginatedGui getPaginatedGui() {
        PaginatedGui gui = Gui.paginated().rows(rows).pageSize(pageSize).title(title).apply((Consumer<PaginatedGui>) actionAfterCreatingPaginationGui()).create();
        for (Consumer<C> c : consumer) {
            ((Consumer<PaginatedGui>) c).accept(gui);
        }
        return gui;
    }

    protected abstract @Nullable Set<Integer> untouchableZone();

    protected abstract List<Consumer<C>> consumer();

    /**
     * apply all action
     * @return Consumer
     */
    private Consumer<C> actionAfterCreatingPaginationGui() {
        return gui -> {
            setUntouchableZone(gui);
            setPlayerInteractGuiAction(gui);
        };
    }

    /**
     * set untouchable zone
     * @param gui
     */
    private void setUntouchableZone(final BaseGui gui) {
        if (untouchableZone != null) {
            for (int index = 0; index < 54; index++) {
                if (untouchableZone.contains(index)) continue;
                gui.setItem(index, Item.AIR.getGuiItem());
            }
        }
    }

    /**
     * prevent click/double-click/ctrl+click/drag action
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
