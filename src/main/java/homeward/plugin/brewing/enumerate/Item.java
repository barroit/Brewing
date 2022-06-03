package homeward.plugin.brewing.enumerate;

import com.google.common.collect.Maps;
import de.tr7zw.nbtapi.NBTItem;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public enum Item implements EnumBase {
    NEXT_BUTTON(ItemBuilder.from(Material.PAPER).model(114514).asGuiItem()),
    NEXT_BUTTON_DIM(ItemBuilder.from(Material.PAPER).model(114515).asGuiItem()),
    PREV_BUTTON(ItemBuilder.from(Material.PAPER).model(114516).asGuiItem()),
    PREV_BUTTON_DIM(ItemBuilder.from(Material.PAPER).model(114517).asGuiItem()),

    AIR(ItemBuilder.from(Material.AIR).asGuiItem());

    private final GuiItem item;

    private static final Map<String, Item> BY_NAME = Maps.newHashMap();
    static {
        for (Item item : values()) {
            BY_NAME.put(item.name(), item);
        }
    }

    Item(GuiItem item) {
        this.item = item;
    }

    public static @Nullable Item getItem(String name) {
        return BY_NAME.getOrDefault(name, null);
    }

    @Override
    public ItemStack getItemStack() {
        NBTItem nbtItem = new NBTItem(item.getItemStack());
        nbtItem.removeKey("PublicBukkitValues");
        return item.getItemStack();
    }

    @Override
    public GuiItem getGuiItem() {
        return item;
    }
}
