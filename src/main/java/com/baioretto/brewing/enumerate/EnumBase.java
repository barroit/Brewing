package com.baioretto.brewing.enumerate;

import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

@SuppressWarnings({"unused", "SameReturnValue"})
public interface EnumBase {
    default Component getComponent() {
        return null;
    }

    default Collection<?> getCollection() {
        return null;
    }

    default String getString() {
        return null;
    }

    default ItemStack getItemStack() {
        return null;
    }

    default GuiItem getGuiItem() {
        return null;
    }
}
