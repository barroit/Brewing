package homeward.plugin.brewing.guis;

import de.tr7zw.nbtapi.NBTItem;
import dev.triumphteam.gui.builder.gui.PaginatedBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import homeward.plugin.brewing.Brewing;
import homeward.plugin.brewing.utils.GuiUtils;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static homeward.plugin.brewing.enumerates.ComponentEnum.*;
import static homeward.plugin.brewing.guis.RecipesPreviewGui.constants.*;

public class RecipesPreviewGui extends GuiBase {
    private static volatile RecipesPreviewGui instance;
    private final PaginatedGui paginatedGui;
    private final Map<String, String> recipesLevelMap;

    private final ItemStack ITEM_STACK_AIR = new ItemStack(Material.AIR);
    private final GuiItem GUI_ITEM_AIR = new GuiItem(Material.AIR);

    @SneakyThrows
    public BaseGui getGui() {
        paginatedGui.setDefaultTopClickAction(event -> event.setCancelled(true));

        setInoperableArea(); // Set Inoperable Area

        setLevelShowcase(); // Set Level Showcase

        setRecipesShowcase(); // Set Recipes Showcase

        setPaginateButton(); // Set Paginate Button

        setGuiOpenAction(); // Set Gui Open Action

        return paginatedGui;
    }

    // region Set Level Showcase
    private void setLevelShowcase() {
        switch (recipesLevelMap.size()) {
            //  |0 1 2 3 4 5 6 7 8|
            //  |  ^   ^   ^   ^  |
            case SIZE_FOUR -> {
                paginatedGui.setItem(SIZE_FOUR$1, new GuiItem(Material.PINK_STAINED_GLASS_PANE));
                paginatedGui.setItem(SIZE_FOUR$2, new GuiItem(Material.MAGENTA_STAINED_GLASS_PANE));
                paginatedGui.setItem(SIZE_FOUR$3, new GuiItem(Material.YELLOW_STAINED_GLASS_PANE));
                paginatedGui.setItem(SIZE_FOUR$4, new GuiItem(Material.LIME_STAINED_GLASS_PANE));
            }
            //  |0 1 2 3 4 5 6 7 8|
            //  |  ^     ^     ^  |
            case SIZE_THREE -> {
                paginatedGui.setItem(SIZE_THREE$1, new GuiItem(Material.PAPER));
                paginatedGui.setItem(SIZE_THREE$2, new GuiItem(Material.PAPER));
                paginatedGui.setItem(SIZE_THREE$3, new GuiItem(Material.PAPER));
            }
            //  |0 1 2 3 4 5 6 7 8|
            //  |  ^           ^  |
            case SIZE_TWO -> {
                paginatedGui.setItem(SIZE_TWO$1, new GuiItem(Material.PAPER));
                paginatedGui.setItem(SIZE_TWO$2, new GuiItem(Material.PAPER));
            }
            //  |0 1 2 3 4 5 6 7 8|
            //  |        ^        |
            case SIZE_ONE -> paginatedGui.setItem(SIZE_ONE$1, new GuiItem(Material.PAPER));
            default -> throw new RuntimeException("The Recipes Level Map Has An Error. This is a brewing plugin internal bug!");
        }
    }
    // endregion

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
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(Component.text("Next", NamedTextColor.RED));
        itemMeta.setCustomModelData(114);
        itemStack.setItemMeta(itemMeta);

        NBTItem nbtItem = new NBTItem(itemStack);
        System.out.println(nbtItem.getInteger("CustomModelData"));

        GuiItem guiItem = new GuiItem(itemStack);
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
    private void setRecipesShowcase() {
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
    private RecipesPreviewGui(int rows, int pageSize) {
        recipesLevelMap = Brewing.getInstance().recipesLevelMap();
        PaginatedBuilder guiBuilder = Gui.paginated();

        switch (recipesLevelMap.size()) {
            case SIZE_FOUR -> guiBuilder.title(GuiUtils.getTitle(NamedTextColor.WHITE, NEGATIVE_10, GUI_RECIPES_PREVIEW_CONTAINER, GAP_REGULAR, GUI_RECIPES_PREVIEW_CONTAINER_LVL4));
            case SIZE_THREE -> guiBuilder.title(GuiUtils.getTitle(NamedTextColor.WHITE, NEGATIVE_10, GUI_RECIPES_PREVIEW_CONTAINER, GAP_REGULAR, GUI_RECIPES_PREVIEW_CONTAINER_LVL3));
            case SIZE_TWO -> guiBuilder.title(GuiUtils.getTitle(NamedTextColor.WHITE, NEGATIVE_10, GUI_RECIPES_PREVIEW_CONTAINER, GAP_REGULAR, GUI_RECIPES_PREVIEW_CONTAINER_LVL3, GAP_REGULAR, GUI_RECIPES_PREVIEW_CONTAINER_LVL4));
            case SIZE_ONE -> guiBuilder.title(GuiUtils.getTitle(NamedTextColor.WHITE, NEGATIVE_10, GUI_RECIPES_PREVIEW_CONTAINER, GAP_REGULAR, GUI_RECIPES_PREVIEW_CONTAINER_LVL3, GAP_REGULAR, GUI_RECIPES_PREVIEW_CONTAINER_LVL1));
            default -> throw new RuntimeException("The Recipes Level Map Has An Error. This is a brewing plugin internal bug!");
        }
        paginatedGui = guiBuilder.rows(rows).pageSize(pageSize).create();
    }

    public static RecipesPreviewGui getInstance(int rows, int pageSize) {
        if (null == instance) {
            synchronized (RecipesPreviewGui.class) {
                if (null == instance) {
                    instance = new RecipesPreviewGui(rows, pageSize);
                }
            }
        }
        return instance;
    }
    // endregion

    // region Constants
    static class constants {
        static final int SLOT_NEXT = 52;
        static final int SLOT_PREV = 46;

        static final int SIZE_FOUR = 4;
        static final int SIZE_THREE = 3;
        static final int SIZE_TWO = 2;
        static final int SIZE_ONE = 1;

        static final int SIZE_FOUR$1 = 1;
        static final int SIZE_FOUR$2 = 3;
        static final int SIZE_FOUR$3 = 5;
        static final int SIZE_FOUR$4 = 7;

        static final int SIZE_THREE$1 = 1;
        static final int SIZE_THREE$2 = 4;
        static final int SIZE_THREE$3 = 7;

        static final int SIZE_TWO$1 = 1;
        static final int SIZE_TWO$2 = 7;

        static final int SIZE_ONE$1 = 4;
    }
    // endregion
}
