package homeward.plugin.brewing.gui;

import com.google.common.collect.Maps;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import homeward.plugin.brewing.Container;
import homeward.plugin.brewing.bean.RecipeProperties;
import homeward.plugin.brewing.enumerate.Item;
import homeward.plugin.brewing.exception.BrewingInternalException;
import homeward.plugin.brewing.utilitie.GuiUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static homeward.plugin.brewing.enumerate.Title.*;

public class RecipesDetailGui extends GuiBase<PaginatedGui> {
    private RecipeProperties recipe;
    private Type currentSelected;
    private static final Map<HumanEntity, String> playerGuiMap = Maps.newHashMap();

    private RecipesDetailGui() {
        super(7, 6);
        this.currentSelected = Type.SUBSTRATE;
        this.setButtonAction();
    }

    public static void open(final HumanEntity player, @NotNull String recipeName) {
        Map<HumanEntity, PaginatedGui> guiMap = Container.RECIPE_DETAIL_GUI;
        PaginatedGui gui = guiMap.getOrDefault(player, null);
        if (gui == null || !recipeName.equals(playerGuiMap.get(player))) {
            gui = RecipesDetailGui.getGui(recipeName);
            playerGuiMap.put(player, recipeName);
            guiMap.put(player, gui);
        }
        gui.open(player);
    }

    public static PaginatedGui getGui(@NotNull String recipeKey) {
        return new RecipesDetailGui().getGeneratedGui(recipeKey);
    }

    private PaginatedGui getGeneratedGui(String recipeKey) {
        recipe = Container.RECIPE_PROPERTIES.getOrDefault(recipeKey, null);
        if (recipe == null) {
            throw new BrewingInternalException();
        }
        return super.getPaginatedGui();
    }

    private Consumer<PaginatedGui> outputConsumer() {
        return gui -> gui.setItem(4, ItemBuilder.from(recipe.output()).asGuiItem());
    }

    private Consumer<PaginatedGui> cancelConsumer() {
        return gui -> {
            GuiItem guiItem = Item.CANCEL.getGuiItem();
            guiItem.setAction(event -> {
                HumanEntity player = event.getWhoClicked();
                RecipesPreviewGui.open(player);
            });
            gui.setItem(1, guiItem);
        };
    }

    private Consumer<PaginatedGui> confirmConsumer() {
        return gui -> {
            // todo brewing gui
            gui.setItem(7, Item.CONFIRM.getGuiItem());
        };
    }

    private Consumer<PaginatedGui> substrateConsumer() {
        return gui -> gui.setItem(19, substrateButton);
    }

    private Consumer<PaginatedGui> yeastConsumer() {
        return gui -> gui.setItem(21, yeastButton);
    }

    private Consumer<PaginatedGui> containerConsumer() {
        return gui -> gui.setItem(23, containerButton);
    }

    private Consumer<PaginatedGui> extraConsumer() {
        return gui -> gui.setItem(25, extraButton);
    }

    private Consumer<PaginatedGui> updateShowcaseConsumer() {
        return this::updateShowcase;
    }

    private void updateShowcase(final PaginatedGui gui) {
        switch (currentSelected) {
            case SUBSTRATE , CONTAINER -> {
                LinkedHashSet<ItemStack> itemStacks = currentSelected == Type.SUBSTRATE ? recipe.substrates() : recipe.containers();
                gui.clearPageItems(itemStacks.size() == 0);
                if (itemStacks.size() == 0) {
                    updateTitle();
                    GuiUtils.updateButtonState(previousButtonSlot, nextButtonSlot).accept(gui);
                    return;
                }
                itemStacks.forEach(item -> {
                    GuiItem guiItem = new GuiItem(item);
                    gui.addItem(guiItem);
                });
            }
            case YEAST, EXTRA -> {
                LinkedHashSet<RecipeProperties.CustomItem> customItems = currentSelected == Type.YEAST ? recipe.yeasts() : recipe.extras();
                gui.clearPageItems(customItems == null || customItems.size() == 0);
                if (customItems == null || customItems.size() == 0) {
                    updateTitle();
                    GuiUtils.updateButtonState(previousButtonSlot, nextButtonSlot).accept(gui);
                    return;
                }
                customItems.forEach(item -> {
                    GuiItem guiItem = new GuiItem(item.item());
                    gui.addItem(guiItem);
                });
            }
            default -> throw new BrewingInternalException();
        }
        updateTitle();
        GuiUtils.updateButtonState(previousButtonSlot, nextButtonSlot).accept(gui);
    }

    private void updateTitle() {
        gui.setUpdating(true);

        final List<HumanEntity> viewers = new ArrayList<>(gui.getInventory().getViewers());

        Component title = gui.title();
        switch (currentSelected) {
            case SUBSTRATE -> title = title.append(substrateSelected);
            case YEAST -> title = title.append(yeastSelected);
            case CONTAINER -> title = title.append(containerSelected);
            case EXTRA -> title = title.append(extraSelected);
            default -> throw new BrewingInternalException();
        }

        gui.setInventory(Bukkit.createInventory(gui, gui.getInventory().getSize(), title));

        for (final HumanEntity player : viewers) {
            gui.open(player, 1);
        }

        gui.setUpdating(false);
    }

    private void setButtonAction() {
        substrateButton.setAction(event -> {
            if (currentSelected == Type.SUBSTRATE) return;
            currentSelected = Type.SUBSTRATE;
            updateShowcase(gui);
        });

        yeastButton.setAction(event -> {
            if (currentSelected == Type.YEAST) return;
            currentSelected = Type.YEAST;
            updateShowcase(gui);
        });

        containerButton.setAction(event -> {
            if (currentSelected == Type.CONTAINER) return;
            currentSelected = Type.CONTAINER;
            updateShowcase(gui);
        });

        extraButton.setAction(event -> {
            if (currentSelected == Type.EXTRA) return;
            currentSelected = Type.EXTRA;
            updateShowcase(gui);
        });
    }

    @Override
    protected Component title() {
        return getRecipeDetailGuiTitle();
    }

    @Override
    protected @Nullable Set<Integer> untouchableZone() {
        // [0,35] [36,37] [38-43] [43,44] [45,53]
        //                   ^
        Set<Integer> touchableZone = IntStream.rangeClosed(37, 43).boxed().collect(Collectors.toSet());
        IntStream zone = IntStream.rangeClosed(0, 53);
        IntStream untouchableZone = zone.filter(slot -> !touchableZone.contains(slot));
        return untouchableZone.boxed().collect(Collectors.toSet());
    }

    @Override
    protected List<Consumer<PaginatedGui>> consumer() {
        return Arrays.asList(
                GuiUtils.guiButtonConsumer(previousButtonSlot, nextButtonSlot),
                confirmConsumer(),
                cancelConsumer(),
                substrateConsumer(),
                yeastConsumer(),
                containerConsumer(),
                extraConsumer(),
                outputConsumer(),
                updateShowcaseConsumer()
        );
    }

    @Override
    protected Consumer<PaginatedGui> openGuiAction() {
        return gui -> gui.setOpenGuiAction(event -> GuiUtils.updateButtonState(previousButtonSlot, nextButtonSlot).accept(gui));
    }

    public static void clearPlayerGuiMap() {
        playerGuiMap.clear();
    }

    private final static GuiItem
            substrateButton,
            yeastButton,
            containerButton,
            extraButton;

    static {
        ItemStack opaque = Item.OPAQUE.getItemStack();

        ItemStack substrate = opaque.clone();
        substrate.editMeta(itemMeta -> itemMeta.displayName(Component.text("substrate")));
        substrateButton = ItemBuilder.from(substrate).asGuiItem();

        ItemStack yeast = opaque.clone();
        yeast.editMeta(itemMeta -> itemMeta.displayName(Component.text("yeast")));
        yeastButton = ItemBuilder.from(yeast).asGuiItem();

        ItemStack container = opaque.clone();
        container.editMeta(itemMeta -> itemMeta.displayName(Component.text("container")));
        containerButton = ItemBuilder.from(container).asGuiItem();

        ItemStack extra = opaque.clone();
        extra.editMeta(itemMeta -> itemMeta.displayName(Component.text("extra")));
        extraButton = ItemBuilder.from(extra).asGuiItem();
    }

    private final static Component substrateSelected = GuiUtils.getTitle(NEGATIVE_130, NEGATIVE_34, POSITIVE_3, DETAIL_SLOT_SELECTED);
    private final static Component yeastSelected = GuiUtils.getTitle(NEGATIVE_130, POSITIVE_7, DETAIL_SLOT_SELECTED);
    private final static Component containerSelected = GuiUtils.getTitle(NEGATIVE_66, NEGATIVE_18, NEGATIVE_6, DETAIL_SLOT_SELECTED);
    private final static Component extraSelected = GuiUtils.getTitle(NEGATIVE_66, POSITIVE_15, DETAIL_SLOT_SELECTED);

    private final static int previousButtonSlot = 46;
    private final static int nextButtonSlot = 52;

    private enum Type {
        SUBSTRATE,
        YEAST,
        CONTAINER,
        EXTRA
    }
}