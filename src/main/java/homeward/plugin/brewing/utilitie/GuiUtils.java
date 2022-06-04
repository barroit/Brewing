package homeward.plugin.brewing.utilitie;

import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import homeward.plugin.brewing.enumerate.Item;
import homeward.plugin.brewing.enumerate.Title;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.function.Consumer;

@UtilityClass
public class GuiUtils {
    public static Component getTitle(Title...title) {
        return getTitle(NamedTextColor.WHITE, title);
    }

    public static Component getTitle(NamedTextColor color, Title...title) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(title).toList().forEach(c -> sb.append(((TextComponent) c.getComponent()).content()));
        return Component.text(sb.toString(), color);
    }

    @SuppressWarnings("ConstantConditions")
    public static Consumer<PaginatedGui> guiButtonConsumer(int previousButtonSlot, int nextButtonSlot) {
        return gui -> {
            nextButtonGuiItem.setAction(event -> {
                if (event.getAction().equals(InventoryAction.NOTHING)) {
                    return; // prevent skip
                }
                if (gui.getNextPageNum() >= gui.getPagesNum()) {
                    gui.updateItem(nextButtonSlot, nextButtonDim);
                }
                if (gui.getNextPageNum() > 1 && gui.getGuiItem(previousButtonSlot).getItemStack().equals(prevButtonDim)) {
                    gui.updateItem(previousButtonSlot, prevButton);
                }
                gui.next();
            });
            prevButtonGuiItem.setAction(event -> {
                if (event.getAction().equals(InventoryAction.NOTHING)) {
                    return;
                }
                if (gui.getPrevPageNum() - 1 == 0) {
                    gui.updateItem(previousButtonSlot, prevButtonDim);
                }
                if (gui.getNextPageNum() > 1 && gui.getGuiItem(nextButtonSlot).getItemStack().equals(nextButtonDim)) {
                    gui.updateItem(nextButtonSlot, nextButton);
                }
                gui.previous();
            });

            gui.setItem(previousButtonSlot, prevButtonGuiItem);
            gui.setItem(nextButtonSlot, nextButtonGuiItem);
        };
    }

    public static Consumer<PaginatedGui> updateButtonState(int previousButtonSlot, int nextButtonSlot) {
        return gui -> {
            gui.updateItem(previousButtonSlot, prevButtonDim);
            if (gui.getCurrentPageNum() >= gui.getPagesNum()) {
                gui.updateItem(nextButtonSlot, nextButtonDim);
            } else {
                gui.updateItem(nextButtonSlot, nextButton);
            }
        };
    }

    // todo remove test method
    public static void setRecipesShowcase(PaginatedGui paginatedGui) {
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

    private static final ItemStack
            nextButton = Item.NEXT_BUTTON.getItemStack(),
            prevButton = Item.PREV_BUTTON.getItemStack(),
            nextButtonDim = Item.NEXT_BUTTON_DIM.getItemStack(),
            prevButtonDim = Item.PREV_BUTTON_DIM.getItemStack();

    private static final GuiItem
            nextButtonGuiItem = new GuiItem(nextButton),
            prevButtonGuiItem = new GuiItem(prevButton);
}
