package homeward.plugin.brewing.guis;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static homeward.plugin.brewing.constants.RecipesPreviewGui.*;

public class RecipesPreviewGui extends GuiBase {
    private static volatile RecipesPreviewGui instance;
    private final PaginatedGui paginatedGui;
    private final ItemStack ITEM_STACK_AIR = new ItemStack(Material.AIR);
    private final GuiItem GUI_ITEM_AIR = new GuiItem(Material.AIR);

    @SneakyThrows
    public BaseGui getGui() {
        paginatedGui.setDefaultTopClickAction(event -> event.setCancelled(true));

        // todo level showcase

        // timing!

        setInoperableArea(); // Set Inoperable Area

        setRecipes(); // Set Recipes

        setPaginateButton(); // Set Paginate Button

        setGuiOpenAction(); // Set Gui Open Action

        return paginatedGui;
    }

    // region Set Gui Open Action
    private void setGuiOpenAction() {
        paginatedGui.setOpenGuiAction(event -> {
            paginatedGui.updateItem(SLOT_PREV, ITEM_STACK_AIR);
            if (paginatedGui.getInventory().getItem(SLOT_NEXT) == null) paginatedGui.updateItem(SLOT_NEXT, getButtonNext());
        });
    }
    // endregion

    // region Set Paginate Button
    private void setPaginateButton() {
        GuiItem next = getButtonNext();
        GuiItem previous = getButtonPrevious();

        paginatedGui.setItem(SLOT_NEXT, next);
        paginatedGui.setItem(SLOT_PREV, previous);
    }
    // endregion

    // region Get The Next Button
    private GuiItem getButtonNext() {
        GuiItem guiItem = new GuiItem(Material.OAK_BUTTON);
        guiItem.setAction(event -> {
            if (paginatedGui.getNextPageNum() == paginatedGui.getPagesNum()) {
                paginatedGui.updateItem(SLOT_NEXT, ITEM_STACK_AIR);
            }
            if (paginatedGui.getInventory().getItem(SLOT_PREV) == null) {
                paginatedGui.updateItem(SLOT_PREV, getButtonPrevious());
            }
            paginatedGui.next();
        });
        return guiItem;
    }
    // endregion

    // region Get The Previous Button
    private GuiItem getButtonPrevious() {
        GuiItem guiItem = new GuiItem(Material.OAK_BUTTON);
        guiItem.setAction(event -> {
            if (paginatedGui.getPrevPageNum() == 1) {
                paginatedGui.updateItem(SLOT_PREV, ITEM_STACK_AIR);
            }
            if (paginatedGui.getInventory().getItem(SLOT_NEXT) == null) {
                paginatedGui.updateItem(SLOT_NEXT, getButtonNext());
            }
            paginatedGui.previous();
        });
        return guiItem;
    }
    // endregion

    // region Set Recipes
    private void setRecipes() {
        for (var i = 0; i < 10; i ++) {
            paginatedGui.addItem(new GuiItem(Material.RED_WOOL));
        }
        for (var i = 0; i < 10; i ++) {
            paginatedGui.addItem(new GuiItem(Material.GREEN_WOOL));
        }
        for (var i = 0; i < 10; i ++) {
            paginatedGui.addItem(new GuiItem(Material.PINK_WOOL));
        }
        for (var i = 0; i < 10; i ++) {
            paginatedGui.addItem(new GuiItem(Material.RED_WOOL));
        }
        for (var i = 0; i < 10; i ++) {
            paginatedGui.addItem(new GuiItem(Material.GREEN_WOOL));
        }
        for (var i = 0; i < 10; i ++) {
            paginatedGui.addItem(new GuiItem(Material.PINK_WOOL));
        }
    }
    // endregion

    // region Set Inoperable Area
    public void setInoperableArea() {
        List<Integer> paginationArea = getPaginationArea();
        for (var var1 = 0; var1 <= 53; var1 ++) {
            if (paginationArea.contains(var1)) continue;
            paginatedGui.setItem(var1, GUI_ITEM_AIR);
        }
    }
    // endregion

    // region Get Pagination Area
    private List<Integer> getPaginationArea() {
        int start = 18;
        List<Integer> var0 = new ArrayList<>();

        boolean var3 = false, var4 = false;

        for (int var1 = start, var2 = 2; var1 <= start + 3 * 9 - 1; var1 ++) {
            var var5 = 9 * var2;
            var var6 = 9 * (var2 + 1) - 1;

            if (var5 == var1) {
                var3 = true;
                if (var4) {
                    var2 ++;
                    var3 = var4 = false;
                }
                continue;
            }
            if (var6 == var1) {
                var4 = true;
                if (var3) {
                    var2 ++;
                    var3 = var4 = false;
                }
                continue;
            }

            var0.add(var1);
        }

        return var0;
    }
    // endregion

    // region Get Class Instance
    private RecipesPreviewGui(Component title, int rows, int pageSize) {
        paginatedGui = Gui.paginated().title(title).rows(rows).pageSize(pageSize).create();
    }

    public static RecipesPreviewGui getInstance(Component title, int rows, int pageSize) {
        if (null == instance) {
            synchronized (RecipesPreviewGui.class) {
                if (null == instance) {
                    instance = new RecipesPreviewGui(title, rows, pageSize);
                }
            }
        }
        return instance;
    }
    // endregion
}
