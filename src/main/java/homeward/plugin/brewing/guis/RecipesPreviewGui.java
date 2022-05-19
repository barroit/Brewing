package homeward.plugin.brewing.guis;

import dev.triumphteam.gui.builder.gui.PaginatedBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import homeward.plugin.brewing.Main;
import lombok.SneakyThrows;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static homeward.plugin.brewing.enumerates.ComponentEnum.*;
import static homeward.plugin.brewing.guis.RecipesPreviewGui.constants.*;
import static homeward.plugin.brewing.utilities.GuiUtils.*;

public class RecipesPreviewGui {
    private static volatile RecipesPreviewGui instance;
    private final PaginatedGui paginatedGui;
    private final Map<String, ItemStack> recipesLevelMap;

    private final ItemStack NEXT_BUTTON = new ItemStack(Material.PAPER);
    private final ItemStack PREV_BUTTON = new ItemStack(Material.PAPER);
    private final ItemStack NEXT_BUTTON_DIM = new ItemStack(Material.PAPER);
    private final ItemStack PREV_BUTTON_DIM = new ItemStack(Material.PAPER);

    private GuiItem next_button_gui_item;
    private GuiItem prev_button_gui_item;

    @SneakyThrows
    public BaseGui getGui() {
        paginatedGui.setDefaultTopClickAction(event -> event.setCancelled(true));

        setInoperableArea(); // Set Inoperable Area

        setLevelShowcase(); // Set Level Showcase

        setRecipesShowcase(); // Set Recipes Showcase

        paginatedGui.setItem(SLOT_NEXT, next_button_gui_item); // Set The Next Button
        paginatedGui.setItem(SLOT_PREV, prev_button_gui_item); // Set The Previous Button

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
                paginatedGui.setItem(SIZE_THREE$1, new GuiItem(Material.MAGENTA_STAINED_GLASS_PANE));
                paginatedGui.setItem(SIZE_THREE$2, new GuiItem(Material.PINK_STAINED_GLASS_PANE));
                paginatedGui.setItem(SIZE_THREE$3, new GuiItem(Material.LIME_STAINED_GLASS_PANE));
            }
            //  |0 1 2 3 4 5 6 7 8|
            //  |  ^           ^  |
            case SIZE_TWO -> {
                paginatedGui.setItem(SIZE_TWO$1, new GuiItem(Material.YELLOW_STAINED_GLASS_PANE));
                paginatedGui.setItem(SIZE_TWO$2, new GuiItem(Material.PINK_STAINED_GLASS_PANE));
            }
            //  |0 1 2 3 4 5 6 7 8|
            //  |        ^        |
            case SIZE_ONE -> paginatedGui.setItem(SIZE_ONE$1, new GuiItem(Material.PINK_STAINED_GLASS_PANE));
            default -> throw new RuntimeException("The Recipes Level Map Has An Error. This is a brewing plugin internal bug!");
        }
    }
    // endregion

    // region Set Gui Open Action
    private void setGuiOpenAction() {
        paginatedGui.setOpenGuiAction(event -> {
            paginatedGui.updateItem(SLOT_PREV, PREV_BUTTON_DIM);
            if (paginatedGui.getCurrentPageNum() == paginatedGui.getPagesNum()) {
                paginatedGui.updateItem(SLOT_NEXT, NEXT_BUTTON_DIM);
            } else {
                paginatedGui.updateItem(SLOT_NEXT, NEXT_BUTTON);
            }
        });
    }
    // endregion

    // region Initialize Next Button Action
    private void initializeNextButtonAction() {
        next_button_gui_item.setAction(event -> {
            if (paginatedGui.getNextPageNum() == paginatedGui.getPagesNum()) {
                paginatedGui.updateItem(SLOT_NEXT, NEXT_BUTTON_DIM);
            }
            ItemStack previousButton = paginatedGui.getInventory().getItem(SLOT_PREV);
            if (paginatedGui.getNextPageNum() != 1 && previousButton != null && previousButton.getItemMeta().getCustomModelData() == PREV_BUTTON_DIM_CUSTOM_MODEL_DATA) {
                paginatedGui.updateItem(SLOT_PREV, PREV_BUTTON);
            }
            paginatedGui.next();
        });
    }
    // endregion

    // region Initialize Previous Button Action
    private void initializePreviousButtonAction() {
        prev_button_gui_item.setAction(event -> {
            if (paginatedGui.getPrevPageNum() == 1) {
                paginatedGui.updateItem(SLOT_PREV, PREV_BUTTON_DIM);
            }
            ItemStack nextButton = paginatedGui.getInventory().getItem(SLOT_NEXT);
            if (paginatedGui.getPagesNum() > paginatedGui.getPrevPageNum() && nextButton != null && nextButton.getItemMeta().getCustomModelData() == NEXT_BUTTON_DIM_CUSTOM_MODEL_DATA) {
                paginatedGui.updateItem(SLOT_NEXT, NEXT_BUTTON);
            }
            paginatedGui.previous();
        });
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
            paginatedGui.setItem(var1, new GuiItem(Material.AIR));
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

    // region Initialize Button When Gui Class Be instanced
    private void initializeButton() {
        ItemMeta nextButtonMeta = NEXT_BUTTON.getItemMeta();
        ItemMeta nextButtonDimMeta = NEXT_BUTTON_DIM.getItemMeta();
        ItemMeta prevButtonMeta = PREV_BUTTON.getItemMeta();
        ItemMeta prevButtonDimMeta = PREV_BUTTON_DIM.getItemMeta();

        nextButtonMeta.setCustomModelData(NEXT_BUTTON_CUSTOM_MODEL_DATA);
        nextButtonDimMeta.setCustomModelData(NEXT_BUTTON_DIM_CUSTOM_MODEL_DATA);
        prevButtonMeta.setCustomModelData(PREV_BUTTON_CUSTOM_MODEL_DATA);
        prevButtonDimMeta.setCustomModelData(PREV_BUTTON_DIM_CUSTOM_MODEL_DATA);

        NEXT_BUTTON.setItemMeta(nextButtonMeta);
        NEXT_BUTTON_DIM.setItemMeta(nextButtonDimMeta);
        PREV_BUTTON.setItemMeta(prevButtonMeta);
        PREV_BUTTON_DIM.setItemMeta(prevButtonDimMeta);

        next_button_gui_item = new GuiItem(NEXT_BUTTON);
        prev_button_gui_item = new GuiItem(PREV_BUTTON);
    }
    // endregion

    // region Initialize Title When Gui Class Be instanced
    private PaginatedBuilder initializeGuiTitle(PaginatedBuilder guiBuilder) {
        switch (recipesLevelMap.size()) {
            case SIZE_FOUR -> guiBuilder.title(getTitle(NamedTextColor.WHITE, NEGATIVE_10, GUI_RECIPES_PREVIEW_CONTAINER, GAP_REGULAR, GUI_RECIPES_PREVIEW_CONTAINER_LVL4));
            case SIZE_THREE -> guiBuilder.title(getTitle(NamedTextColor.WHITE, NEGATIVE_10, GUI_RECIPES_PREVIEW_CONTAINER, GAP_REGULAR, GUI_RECIPES_PREVIEW_CONTAINER_LVL3));
            case SIZE_TWO -> guiBuilder.title(getTitle(NamedTextColor.WHITE, NEGATIVE_10, GUI_RECIPES_PREVIEW_CONTAINER, GAP_REGULAR, GUI_RECIPES_PREVIEW_CONTAINER_LVL3, GAP_REGULAR, GUI_RECIPES_PREVIEW_CONTAINER_LVL4));
            case SIZE_ONE -> guiBuilder.title(getTitle(NamedTextColor.WHITE, NEGATIVE_10, GUI_RECIPES_PREVIEW_CONTAINER, GAP_REGULAR, GUI_RECIPES_PREVIEW_CONTAINER_LVL3, GAP_REGULAR, GUI_RECIPES_PREVIEW_CONTAINER_LVL1));
            default -> throw new RuntimeException("The Recipes Level Map Has An Error. This is a brewing plugin internal bug!");
        }
        return guiBuilder;
    }
    // endregion

    // region Instance Class
    private RecipesPreviewGui(int rows, int pageSize) {
        this.recipesLevelMap = Main.getInstance().recipesLevelMap();

        this.initializeButton();

        PaginatedBuilder guiBuilder = this.initializeGuiTitle(Gui.paginated());

        this.paginatedGui = guiBuilder.rows(rows).pageSize(pageSize).create();

        initializeNextButtonAction();
        initializePreviousButtonAction();
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

        static final int NEXT_BUTTON_CUSTOM_MODEL_DATA = 114514;
        static final int NEXT_BUTTON_DIM_CUSTOM_MODEL_DATA = 114515;
        static final int PREV_BUTTON_CUSTOM_MODEL_DATA = 114516;
        static final int PREV_BUTTON_DIM_CUSTOM_MODEL_DATA = 114517;
    }
    // endregion
}
