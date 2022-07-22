package com.baioretto.brewing.command;

import com.baioretto.brewing.Container;
import com.baioretto.brewing.enumerate.Tag;
import com.baioretto.brewing.gui.RecipesPreviewGui;
import de.tr7zw.nbtapi.NBTItem;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Command("mock")
@SuppressWarnings("unused")
public class MockTest extends CommandBase {
    @SubCommand("getRecipesPreviewGui")
    public void getRecipesPreviewGui(CommandSender commandSender) {
        if (!(commandSender instanceof Player player)) return;
        RecipesPreviewGui.open(player, new Location(player.getWorld(), -49D, 67D, -87D));
    }

    @SubCommand("clearGuiContainer")
    public void clearGuiContainer(CommandSender commandSender) {
        Container.RECIPE_PREVIEW_GUI.clear();
        Container.RECIPE_DETAIL_GUI.clear();
    }

    @SubCommand("getOpenedBarrel")
    public void getOpenedBarrel(CommandSender commandSender) {
        System.out.println(Container.OPENED_BARREL);
    }

    @SubCommand("getGuiContainer")
    public void getGuiContainer(CommandSender commandSender) {
        System.out.println(Container.RECIPE_PREVIEW_GUI);
        System.out.println(Container.RECIPE_DETAIL_GUI);
    }

    @SubCommand("getNbtInHand")
    public void getNbtInHand(CommandSender commandSender) {
        if (!(commandSender instanceof Player player)) return;
        System.out.println(new NBTItem(player.getInventory().getItemInMainHand()));
    }

    @SubCommand("getBarrel")
    public void getBarrel(CommandSender commandSender) {
        if (!(commandSender instanceof Player player)) return;

        ItemStack barrel = new ItemStack(Material.BARREL);
        NBTItem nbtItem = new NBTItem(barrel);
        nbtItem.setObject(Tag.BARREL.key(), Tag.BARREL.value());
        player.getInventory().addItem(nbtItem.getItem());
    }
}
