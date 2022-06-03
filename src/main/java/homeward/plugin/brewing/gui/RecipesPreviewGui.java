package homeward.plugin.brewing.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import homeward.plugin.brewing.Container;
import homeward.plugin.brewing.enumerate.Item;
import homeward.plugin.brewing.enumerate.Type;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class RecipesPreviewGui extends GuiBase<PaginatedGui> {
    private int currentTier = 1;
    private final List<Map.Entry<String, ItemStack>> recipeEntryList;

    private RecipesPreviewGui() {
        super(Container.RECIPE_TIER, 27, 6, Type.RECIPES_PREVIEW_GUI);
        recipeEntryList = super.recipeTier.entrySet().stream().toList();
    }

    public static PaginatedGui getGui() {
        return new RecipesPreviewGui().getPaginatedGui();
    }

    @Override
    protected @Nullable Set<Integer> untouchableZone() {
        int start = 18;
        Set<Integer> zone = new LinkedHashSet<>();
        boolean var3 = false, var4 = false;
        for (int index = start, line = 2; index < start + super.pageSize; index++) {
            var left = 9 * line;
            var right = 9 * (line + 1) - 1;

            if (left == index) {
                var3 = true;
                if (var4) {
                    line++;
                    var3 = var4 = false;
                }
                continue;
            }
            if (right == index) {
                var4 = true;
                if (var3) {
                    line++;
                    var3 = var4 = false;
                }
                continue;
            }
            zone.add(index);
        }
        return zone;
    }

    @Override
    protected List<Consumer<PaginatedGui>> consumer() {
        return Arrays.asList(recipeTierDisplayConsumer(), guiButtonConsumer(), guiOpenActionConsumer(), recipeShowcaseConsumer());
    }

    private Consumer<PaginatedGui> recipeShowcaseConsumer() {
        return this::updateRecipeShowcase;
    }

    private void updateRecipeShowcase(PaginatedGui gui) {
        Map.Entry<String, ItemStack> entry;
        switch (currentTier) {
            case 1 -> entry = recipeEntryList.get(0);
            case 2 -> entry = recipeEntryList.get(1);
            case 3 -> entry = recipeEntryList.get(2);
            case 4 -> entry = recipeEntryList.get(3);
            default -> throw new IllegalStateException();
        }
        Map<String, ItemStack> map = Container.RECIPE_DISPLAY_ITEMS.get(entry.getKey());
        gui.clearPageItems(map == null);
        if (map == null) return;
        map.forEach((key, itemStack) -> {
            GuiItem item = new GuiItem(itemStack);
            gui.addItem(item);
        });
        gui.update();
        this.updateButtonState(gui);
    }

    private Consumer<PaginatedGui> recipeTierDisplayConsumer() {
        return gui -> {
            ItemStack t1I = recipeEntryList.get(0).getValue();
            GuiItem t1G = ItemBuilder.from(t1I).asGuiItem();

            t1G.setAction(event -> {
                if (currentTier != 1) {
                    currentTier = 1;
                    this.updateRecipeShowcase(gui);
                }
            });

            int tierSize = super.recipeTierSize;
            if (tierSize == 1) {
                gui.setItem(4, t1G);
            } else {
                ItemStack t2I = recipeEntryList.get(1).getValue();
                GuiItem t2G = ItemBuilder.from(t2I).asGuiItem();
                gui.setItem(1, t1G);

                t2G.setAction(event -> {
                    if (currentTier != 2) {
                        currentTier = 2;
                        this.updateRecipeShowcase(gui);
                    }
                });

                if (tierSize == 2) {
                    gui.setItem(7, t2G);
                } else {
                    ItemStack t3I = recipeEntryList.get(2).getValue();
                    GuiItem t3G = ItemBuilder.from(t3I).asGuiItem();

                    t3G.setAction(event -> {
                        if (currentTier != 3) {
                            currentTier = 3;
                            this.updateRecipeShowcase(gui);
                        }
                    });

                    if (tierSize == 3) {
                        gui.setItem(4, t2G);
                        gui.setItem(7, t3G);
                    } else {
                        ItemStack t4I = recipeEntryList.get(3).getValue();
                        GuiItem t4G = ItemBuilder.from(t4I).asGuiItem();

                        t4G.setAction(event -> {
                            if (currentTier != 4) {
                                currentTier = 4;
                                this.updateRecipeShowcase(gui);
                            }
                        });

                        gui.setItem(3, t2G);
                        gui.setItem(5, t3G);
                        gui.setItem(7, t4G);
                    }
                }
            }
        };
    }

    @SuppressWarnings("ConstantConditions")
    private Consumer<PaginatedGui> guiButtonConsumer() {
        return gui -> {
            nextButtonGuiItem.setAction(event -> {
                if (event.getAction().equals(InventoryAction.NOTHING)) {
                    return; // prevent skip
                }
                if (gui.getNextPageNum() >= gui.getPagesNum()) {
                    gui.updateItem(52, nextButtonDim);
                }
                if (gui.getNextPageNum() > 1 && gui.getGuiItem(46).getItemStack().equals(prevButtonDim)) {
                    gui.updateItem(46, prevButton);
                }
                gui.next();
            });
            prevButtonGuiItem.setAction(event -> {
                if (event.getAction().equals(InventoryAction.NOTHING)) {
                    return;
                }
                if (gui.getPrevPageNum() - 1 == 0) {
                    gui.updateItem(46, prevButtonDim);
                }
                if (gui.getNextPageNum() > 1 && gui.getGuiItem(52).getItemStack().equals(nextButtonDim)) {
                    gui.updateItem(52, nextButton);
                }
                gui.previous();
            });

            gui.setItem(52, nextButtonGuiItem);
            gui.setItem(46, prevButtonGuiItem);
        };
    }

    private Consumer<PaginatedGui> guiOpenActionConsumer() {
        return gui -> gui.setOpenGuiAction(event -> this.updateButtonState(gui));
    }

    private void updateButtonState(final PaginatedGui gui) {
        gui.updateItem(46, prevButtonDim);
        if (gui.getCurrentPageNum() >= gui.getPagesNum()) {
            gui.updateItem(52, nextButtonDim);
        } else {
            gui.updateItem(52, nextButton);
        }
    }

    private final ItemStack
            nextButton = Item.NEXT_BUTTON.getItemStack(),
            prevButton = Item.PREV_BUTTON.getItemStack(),
            nextButtonDim = Item.NEXT_BUTTON_DIM.getItemStack(),
            prevButtonDim = Item.PREV_BUTTON_DIM.getItemStack();

    private final GuiItem
            nextButtonGuiItem = new GuiItem(nextButton),
            prevButtonGuiItem = new GuiItem(prevButton);
}