package com.baioretto.brewing.listener;

import com.baioretto.brewing.Container;
import com.baioretto.brewing.bean.OpenedBarrel;
import com.baioretto.brewing.enumerate.Tag;
import com.baioretto.brewing.gui.RecipesPreviewGui;
import com.baioretto.brewing.util.BrewingUtils;
import de.tr7zw.nbtapi.*;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {
    @EventHandler
    public void onBarrelPlaced(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getItemInHand();
        NBTItem nbtItem = new NBTItem(itemInHand);
        if (!(nbtItem.hasKey(Tag.BARREL.key()) && nbtItem.getObject(Tag.BARREL.key(), Object.class).equals(Tag.BARREL.value()))) return;

        // brewing barrel
        Block block = event.getBlock();

        NBTBlock nbtBlock = new NBTBlock(block);
        nbtBlock.getData().setObject(Tag.BARREL.key(), Tag.BARREL.value());
    }

    @EventHandler
    public void onBarrelInteracted(PlayerInteractEvent event) {
        if (!Action.RIGHT_CLICK_BLOCK.equals(event.getAction())) return;

        Block clickedBlock = event.getClickedBlock();

        if (BrewingUtils.notBrewingBarrel(clickedBlock)) return;

        event.setCancelled(true);
        //noinspection ConstantConditions
        RecipesPreviewGui.open(event.getPlayer(), clickedBlock.getLocation());

        OpenedBarrel openedBarrel = Container.OPENED_BARREL.get(clickedBlock.getLocation());
        Player player = event.getPlayer();
        if (openedBarrel != null) {
            Container.OPENED_BARREL.replace(clickedBlock.getLocation(), openedBarrel.viewers(player));
            return;
        }

        Barrel barrel = (Barrel) clickedBlock.getState();
        barrel.open();
        openedBarrel = new OpenedBarrel(barrel).viewers(player);
        Container.OPENED_BARREL.put(clickedBlock.getLocation(), openedBarrel);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBarrelBreaked(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (BrewingUtils.notBrewingBarrel(block)) return;

        OpenedBarrel openedBarrel = Container.OPENED_BARREL.remove(block.getLocation());
        if (openedBarrel == null) return;

        openedBarrel.viewers().forEach(HumanEntity::closeInventory);
    }
}