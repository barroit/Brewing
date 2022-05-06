package homeward.plugin.brewing.utils;

import de.tr7zw.nbtapi.NBTFile;
import de.tr7zw.nbtapi.NBTItem;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.StorageGui;
import homeward.plugin.brewing.beans.BarrelInventoryData;
import homeward.plugin.brewing.constants.BaseInfo;
import homeward.plugin.brewing.enumerates.ComponentEnum;
import homeward.plugin.brewing.enumerates.EnumBase;
import homeward.plugin.brewing.listeners.BrewingBarrelListener;
import lombok.SneakyThrows;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;

import static homeward.plugin.brewing.utils.InventoryUtils.*;
import static homeward.plugin.brewing.constants.BarrelConstants.*;

public class GuiUtils {
    private NBTFile file;
    private Location barrelLocation;

    @SneakyThrows
    public StorageGui generateStorage() {
        StorageGui storageGui = generateStorageGui(3, ComponentEnum.BARREL_TITLE);

        storageGui.setItem(SUBSTRATE_SLOT, new GuiItem(substrateSlot));
        storageGui.setItem(RESTRICTION_SLOT, new GuiItem(restrictionSlot));
        storageGui.setItem(YEAST_SLOT, new GuiItem(yeastSlot));
        storageGui.setItem(6, new GuiItem(substrateSlotState));
        storageGui.setItem(15, new GuiItem(restrictionSlotState));
        storageGui.setItem(24, new GuiItem(yeastSlotState));

        storageGui.setDefaultTopClickAction(event -> event.setCancelled(true));
        storageGui.addSlotAction(SUBSTRATE_SLOT, this::handleClick);
        storageGui.addSlotAction(RESTRICTION_SLOT, this::handleClick);
        storageGui.addSlotAction(YEAST_SLOT, this::handleClick);
        storageGui.setCloseGuiAction(event -> {
            Map<HumanEntity, Location> barrelLocationMap = BrewingBarrelListener.getBarrelLocationMap();
            barrelLocationMap.remove(event.getPlayer());
        });

        return storageGui;
    }

    private void handleClick(InventoryClickEvent event) {
        if (!(event.getClickedInventory().getHolder() instanceof StorageGui gui)) return;

        ItemStack cursor = event.getCursor();
        if (cursor == null) return;
        int eventSlot = event.getSlot();
        if (!BaseInfo.BARREL_DESCRIPTION_MANIPULATIVE_LIST.contains(eventSlot)) return;

        HumanEntity player = event.getWhoClicked();
        ItemStack itemWithoutTag = getItemInSlot(gui, eventSlot);

        boolean isDescription = BaseInfo.BARREL_DESCRIPTION_CUSTOM_MODEL_DATA_LIST.contains(new NBTItem(itemWithoutTag).getInteger("CustomModelData"));
        boolean cursorIsAir = cursor.getType() == Material.AIR;

        if (isDescription && !cursorIsAir) { // the player not put an item in this slot and their cursor nonnull
            gui.updateItem(eventSlot, cursor);
            player.setItemOnCursor(new ItemStack(Material.AIR));

        } else if (!isDescription && cursorIsAir) { // the player has put an item in this slot and their cursor doesn't have an item
            player.setItemOnCursor(itemWithoutTag);
            switch (eventSlot) {
                case 2 -> gui.updateItem(SUBSTRATE_SLOT, substrateSlot);
                case 11 -> gui.updateItem(RESTRICTION_SLOT, restrictionSlot);
                case 20 -> gui.updateItem(YEAST_SLOT, yeastSlot);
            }

        } else if (!isDescription) { // the player has put an item in this slot and their cursor nonnull
            ItemStack itemOnCursor = player.getItemOnCursor();
            player.setItemOnCursor(itemWithoutTag);
            gui.updateItem(eventSlot, itemOnCursor);
        }

        persistentFileData(player, gui, eventSlot);
    }

    @SneakyThrows
    private void persistentFileData(HumanEntity player, StorageGui gui, int eventSlot) {
        if (barrelLocation == null) {
            Map<HumanEntity, Location> barrelLocationMap = BrewingBarrelListener.getBarrelLocationMap();
            if (!barrelLocationMap.containsKey(player)) return;
            barrelLocation = barrelLocationMap.get(player);
        }

        if (file == null) {
            file = new NBTFile(new File(player.getWorld().getName(), "brew.nbt"));
        }

        byte[] bytesData = file.getByteArray(barrelLocation + "");
        BarrelInventoryData data = (BarrelInventoryData) CommonUtils.decodeBukkitObject(bytesData.length == 0 ? null : bytesData);
        if (data == null) {
            data = new BarrelInventoryData();
        }

        if (gui.getGuiItem(eventSlot) == null) return;

        if (!(data.getSubstrate() == null || data.getRestriction() == null || data.getYeast() == null)) return; // barrel is brewing
        ItemStack itemInSlot = updateItem(gui, eventSlot);

        if (data.getSubstrate() == null && eventSlot == SUBSTRATE_SLOT) {
            data.setSubstrate(itemInSlot).setHasSubstrate(true);
            updateItem(gui, itemInSlot, eventSlot, data, ComponentEnum.BARREL_TITLE_WITH_SUBSTRATE);

        } else if (data.getRestriction() == null && eventSlot == RESTRICTION_SLOT) {
            data.setRestriction(itemInSlot).setHasRestriction(true);
            updateItem(gui, itemInSlot, eventSlot, data, ComponentEnum.BARREL_TITLE_WITH_RESTRICTION);

        } else if (data.getYeast() == null && eventSlot == YEAST_SLOT) {
            data.setYeast(itemInSlot).setHasYeast(true);
            updateItem(gui, itemInSlot, eventSlot, data, ComponentEnum.BARREL_TITLE_WITH_YEAST);
        }
    }


    private ItemStack updateItem(StorageGui gui, int eventSlot) {
        ItemStack itemInSlot = gui.getInventory().getItem(eventSlot);
        if (itemInSlot == null) return null;

        itemInSlot.setAmount(itemInSlot.getAmount() - 1);

        return itemInSlot;
    }

    private void updateItem(StorageGui gui, ItemStack itemInSlot, @IntRange(from = 0, to = 26) int eventSlot, BarrelInventoryData data, EnumBase title) {
        gui.updateItem(eventSlot, itemInSlot);


        setTitle(title, gui);

        if (data.getSubstrate() != null && data.getRestriction() != null && data.getYeast() != null && !data.isBrewing()) {
            data.setBrewing(true);
        }

        saveData(data);
    }

    @SneakyThrows
    private void saveData(BarrelInventoryData data) {
        file.setByteArray(barrelLocation + "", CommonUtils.encodeBukkitObject(data));
        file.save();
    }


    public static void setTitle(EnumBase enumBase, StorageGui gui) {
        TextComponent newTitle = (TextComponent) enumBase.getComponent();
        TextComponent rawTitle = (TextComponent) gui.title();
        TextColor titleColor = rawTitle.style().color();
        String title = (titleColor == null ? "" : ChatColor.valueOf(titleColor.toString().toUpperCase()) + rawTitle.content()) + newTitle.content();
        gui.updateTitle(title);
    }

    private ItemStack getItemInSlot(@NotNull StorageGui gui, @IntRange(from = 0, to = 26) int eventSlot) {
        GuiItem itemInSlot = gui.getGuiItem(eventSlot);
        if (itemInSlot == null) return new ItemStack(Material.AIR);
        NBTItem itemNBT = new NBTItem(itemInSlot.getItemStack());
        itemNBT.removeKey("PublicBukkitValues");
        return itemNBT.getItem();
    }
}
