package com.baioretto.brewing.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import com.baioretto.brewing.Container;
import com.baioretto.brewing.exception.BrewingInternalException;
import com.baioretto.brewing.util.GuiUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.baioretto.brewing.enumerate.Title.*;

public class RecipesPreviewGui extends GuiBase<PaginatedGui> {
    private int currentTier = 1;
    private final List<Map.Entry<String, ItemStack>> tierEntries;

    private RecipesPreviewGui(Location barrelLocation) {
        super(27, 6, barrelLocation);
        this.tierEntries = Container.RECIPE_TIER.entrySet().stream().toList();
    }

    public static void open(final HumanEntity player, Location barrelLocation) {
        Map<HumanEntity, RecipesPreviewGui> guiMap = Container.RECIPE_PREVIEW_GUI;

        RecipesPreviewGui instance = guiMap.getOrDefault(player, null);
        if (instance == null) {
            instance = RecipesPreviewGui.getInstance(barrelLocation);
            instance.initPaginatedGui();
            guiMap.put(player, instance);
        }

        instance.open(player, 1);
    }

    public static RecipesPreviewGui getInstance(Location barrelLocation) {
        return new RecipesPreviewGui(barrelLocation).initPaginatedGui();
    }

    private Consumer<PaginatedGui> recipeShowcaseConsumer() {
        return this::updateRecipesShowcase;
    }

    private Consumer<PaginatedGui> tierIcon() {
        return gui -> {
            ItemStack t1I = tierEntries.get(0).getValue();
            GuiItem t1G = ItemBuilder.from(t1I).asGuiItem();
            this.tierIconClickAction(t1G, 1);

            int tierSize = Container.RECIPE_TIER.size();
            if (tierSize == 1) {
                gui.setItem(4, t1G);
            } else {
                ItemStack t2I = tierEntries.get(1).getValue();
                GuiItem t2G = ItemBuilder.from(t2I).asGuiItem();
                gui.setItem(1, t1G);
                this.tierIconClickAction(t2G, 2);

                if (tierSize == 2) {
                    gui.setItem(7, t2G);
                } else {
                    ItemStack t3I = tierEntries.get(2).getValue();
                    GuiItem t3G = ItemBuilder.from(t3I).asGuiItem();
                    this.tierIconClickAction(t3G, 3);

                    if (tierSize == 3) {
                        gui.setItem(4, t2G);
                        gui.setItem(7, t3G);
                    } else {
                        ItemStack t4I = tierEntries.get(3).getValue();
                        GuiItem t4G = ItemBuilder.from(t4I).asGuiItem();
                        this.tierIconClickAction(t4G, 4);

                        gui.setItem(3, t2G);
                        gui.setItem(5, t3G);
                        gui.setItem(7, t4G);
                    }
                }
            }
        };
    }

    private void tierIconClickAction(GuiItem item, int currentTier) {
        item.setAction(event -> {
            if (this.currentTier != currentTier) {
                this.currentTier = currentTier;
                this.updateRecipesShowcase(gui);
            }
        });
    }

    private void updateRecipesShowcase(final PaginatedGui gui) {
        Map.Entry<String, ItemStack> tierEntry;
        switch (currentTier) {
            case 1 -> tierEntry = tierEntries.get(0);
            case 2 -> tierEntry = tierEntries.get(1);
            case 3 -> tierEntry = tierEntries.get(2);
            case 4 -> tierEntry = tierEntries.get(3);
            default -> throw new IllegalStateException();
        }

        Map<String, ItemStack> recipeDisplayItem = Container.RECIPE_DISPLAY_ITEMS.get(tierEntry.getKey());

        gui.clearPageItems(recipeDisplayItem == null);

        if (recipeDisplayItem == null) {
            this.updateTitle();
            GuiUtils.updateButtonState(this, 46, 52).accept(gui);
            return;
        }

        recipeDisplayItem.forEach((key, itemStack) -> {
            GuiItem item = new GuiItem(itemStack);
            item.setAction(event -> {
                HumanEntity player = event.getWhoClicked();
                this.toNext = true;
                // to next gui
                RecipesDetailGui.open(player, key, barrelLocation);
            });
            gui.addItem(item);
        });

        this.updateTitle();
        GuiUtils.updateButtonState(this, 46, 52).accept(gui);
    }

    private void updateTitle() {
        Component title = gui.title();
        int size = Container.RECIPE_TIER.size();
        switch (size) {
            case 1 -> title = title.append(selectedSlotIndex4);
            case 2, 3, 4 -> {
                if (currentTier == 1) title = title.append(selectedSlotIndex1);
                if (size == 2 && currentTier == 2) title = title.append(selectedSlotIndex7);
                if (size == 3) {
                    switch (currentTier) {
                        case 2 -> title = title.append(selectedSlotIndex4);
                        case 3 -> title = title.append(selectedSlotIndex7);
                    }
                }
                if (size == 4) {
                    switch (currentTier) {
                        case 2 -> title = title.append(selectedSlotIndex3);
                        case 3 -> title = title.append(selectedSlotIndex5);
                        case 4 -> title = title.append(selectedSlotIndex7);
                    }
                }
            }
            default -> throw new BrewingInternalException();
        }

        updateTitle(title);
    }

    private Component getRecipePreviewGuiTitle(Amount amount) {
        switch (amount) {
            case x1 -> {
                return GuiUtils.getTitle(NEGATIVE_10, RECIPES_PREVIEW_CONTAINER, GAP_REGULAR, RECIPES_PREVIEW_CONTAINER_LVL3, GAP_REGULAR, RECIPES_PREVIEW_CONTAINER_LVL1);
            }
            case x2 -> {
                return GuiUtils.getTitle(NEGATIVE_10, RECIPES_PREVIEW_CONTAINER, GAP_REGULAR, RECIPES_PREVIEW_CONTAINER_LVL3, GAP_REGULAR, RECIPES_PREVIEW_CONTAINER_LVL4);
            }
            case x3 -> {
                return GuiUtils.getTitle(NEGATIVE_10, RECIPES_PREVIEW_CONTAINER, GAP_REGULAR, RECIPES_PREVIEW_CONTAINER_LVL3);
            }
            case x4 -> {
                return GuiUtils.getTitle(NEGATIVE_10, RECIPES_PREVIEW_CONTAINER, GAP_REGULAR, RECIPES_PREVIEW_CONTAINER_LVL4);
            }
            default -> throw new IllegalArgumentException();
        }
    }

    @Override
    protected Component title() {
        switch (Container.RECIPE_TIER.size()) { // npe
            case 1 -> {
                return getRecipePreviewGuiTitle(Amount.x1);
            }
            case 2 -> {
                return getRecipePreviewGuiTitle(Amount.x2);
            }
            case 3 -> {
                return getRecipePreviewGuiTitle(Amount.x3);
            }
            case 4 -> {
                return getRecipePreviewGuiTitle(Amount.x4);
            }
            default -> throw new IllegalStateException();
        }
    }

    @Override
    protected @Nullable Set<Integer> untouchableZone() {
        // [0-17] [18] [19-25] [26] [27] [28-34] [35] [36] [37-43] [44] [45-53]
        //                ^                 ^                 ^
        Set<Integer> line1 = IntStream.rangeClosed(19, 25).boxed().collect(Collectors.toSet());
        Set<Integer> line2 = IntStream.rangeClosed(28, 34).boxed().collect(Collectors.toSet());
        Set<Integer> line3 = IntStream.rangeClosed(37, 43).boxed().collect(Collectors.toSet());

        Set<Integer> line = new LinkedHashSet<>();
        line.addAll(line1);
        line.addAll(line2);
        line.addAll(line3);

        IntStream zone = IntStream.rangeClosed(0, 53);
        return zone.filter(slot -> !line.contains(slot)).boxed().collect(Collectors.toSet());
    }

    @Override
    protected List<Consumer<PaginatedGui>> consumers() {
        return Arrays.asList(
                tierIcon(),
                GuiUtils.guiButtonConsumer(this, 46, 52),
                recipeShowcaseConsumer()
        );
    }

    @Override
    protected Consumer<PaginatedGui> openGuiAction() {
        return gui -> gui.setOpenGuiAction(event -> GuiUtils.updateButtonState(this, 46, 52).accept(gui));
    }

    @Override
    protected void open(HumanEntity player, int openPage) {
        GuiUtils.paginatedGuiOpen(gui, player, openPage);
    }

    private final Component selectedSlotIndex1 = GuiUtils.getTitle(NEGATIVE_130, NEGATIVE_18, NEGATIVE_9, NEGATIVE_5, PREVIEW_SLOT_SELECTED);
    private final Component selectedSlotIndex3 = GuiUtils.getTitle(NEGATIVE_66, NEGATIVE_34, NEGATIVE_18, NEGATIVE_8, PREVIEW_SLOT_SELECTED);
    private final Component selectedSlotIndex4 = GuiUtils.getTitle(NEGATIVE_66, NEGATIVE_34, NEGATIVE_6, PREVIEW_SLOT_SELECTED);
    private final Component selectedSlotIndex5 = GuiUtils.getTitle(NEGATIVE_66, NEGATIVE_18, NEGATIVE_4, PREVIEW_SLOT_SELECTED);
    private final Component selectedSlotIndex7 = GuiUtils.getTitle(NEGATIVE_34, NEGATIVE_10, NEGATIVE_8, PREVIEW_SLOT_SELECTED);
}