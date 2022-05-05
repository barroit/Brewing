package homeward.plugin.brewing.listeners;

import de.tr7zw.nbtapi.NBTItem;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.StorageGui;
import homeward.plugin.brewing.beans.BarrelInventoryData;
import homeward.plugin.brewing.constants.BaseInfo;
import homeward.plugin.brewing.data.BrewingBarrelData;
import homeward.plugin.brewing.enumerates.ComponentEnum;
import homeward.plugin.brewing.enumerates.EnumBase;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import static homeward.plugin.brewing.utils.InventoryUtils.*;

public class BrewingBarrelClickListener implements Listener {
    private final ItemStack air = new ItemStack(Material.AIR);

    @EventHandler
    public void onPlayerClickEvent(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        // prevent shift+click and double click
        if (event.getView().getTopInventory().getHolder() instanceof BaseGui) {
            if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                event.setCancelled(true);
            }
            if (event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
                if (event.getCursor() == null) return;
                if (event.getView().getTopInventory().contains(event.getCursor().getType())) {
                    event.setCancelled(true);
                }
            }
        }

        if (!(event.getClickedInventory().getHolder() instanceof StorageGui gui)) return;

        event.setCancelled(true);

        handleClick(event, gui);
    }

    private void handleClick(InventoryClickEvent event, StorageGui gui) {
        ItemStack cursor = event.getCursor();
        if (cursor == null || gui == null) return;

        boolean cursorIsAir = cursor.getType() == Material.AIR;
        int eventSlot = event.getSlot();
        HumanEntity player = event.getWhoClicked();

        ItemStack itemWithoutTag = getItemInSlot(gui, eventSlot);

        boolean isDescription = BaseInfo.BARREL_DESCRIPTION_CUSTOM_MODEL_DATA_LIST.contains(new NBTItem(itemWithoutTag).getInteger("CustomModelData"));

        if (isDescription && !cursorIsAir) {
            gui.updateItem(eventSlot, cursor);
            player.setItemOnCursor(air);

        } else if (!isDescription && cursorIsAir) {
            player.setItemOnCursor(itemWithoutTag);
            switch (eventSlot) {
                case 2 -> gui.updateItem(2, substrateSlot);
                case 11 -> gui.updateItem(11, restrictionSlot);
                case 20 -> gui.updateItem(20, yeastSlot);
            }
        } else if (!isDescription) {
            ItemStack itemOnCursor = player.getItemOnCursor();
            player.setItemOnCursor(itemWithoutTag);
            gui.updateItem(eventSlot, itemOnCursor);
        }

        switch (eventSlot) {
            case 2 -> setTitle(ComponentEnum.BARREL_TITLE_WITH_SUBSTRATE, gui);
            case 11 -> setTitle(ComponentEnum.BARREL_TITLE_WITH_RESTRICTION, gui);
            case 20 -> setTitle(ComponentEnum.BARREL_TITLE_WITH_YEAST, gui);
        }


    }

    private void setTitle(EnumBase enumBase, StorageGui gui) {
        TextComponent newTitle = (TextComponent) enumBase.getComponent();
        TextComponent rawTitle = (TextComponent) gui.title();
        TextColor titleColor = rawTitle.style().color();
        String title = (titleColor == null ? "" : ChatColor.valueOf(titleColor.toString().toUpperCase()) + rawTitle.content()) + newTitle.content();
        gui.updateTitle(title);
    }

    private ItemStack getItemInSlot(StorageGui gui, int eventSlot) {
        GuiItem itemInSlot = gui.getGuiItem(eventSlot);
        if (itemInSlot == null) return new ItemStack(Material.AIR);
        NBTItem itemNBT = new NBTItem(itemInSlot.getItemStack());
        itemNBT.removeKey("PublicBukkitValues");
        return itemNBT.getItem();
    }
}
