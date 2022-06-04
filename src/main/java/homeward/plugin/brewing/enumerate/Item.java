package homeward.plugin.brewing.enumerate;

import com.google.common.collect.Maps;
import de.tr7zw.nbtapi.NBTItem;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@SuppressWarnings("unused")
public enum Item implements EnumBase {
    NEXT_BUTTON(ItemBuilder.from(Material.PAPER).model(15200).asGuiItem()),
    NEXT_BUTTON_DIM(ItemBuilder.from(Material.PAPER).model(15201).asGuiItem()),
    PREV_BUTTON(ItemBuilder.from(Material.PAPER).model(15202).asGuiItem()),
    PREV_BUTTON_DIM(ItemBuilder.from(Material.PAPER).model(15203).asGuiItem()),

    CONFIRM(
            ItemBuilder.from(Material.PAPER)
                    .model(15204)
                    .name(Component.text("Confirm", NamedTextColor.GREEN))
                    .asGuiItem()
    ),
    CANCEL(ItemBuilder.from(Material.PAPER)
            .model(15205)
            .name(Component.text("Cancel", NamedTextColor.RED))
            .asGuiItem()
    ),

    OPAQUE(ItemBuilder.from(Material.PAPER).model(15299).asGuiItem()),

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
