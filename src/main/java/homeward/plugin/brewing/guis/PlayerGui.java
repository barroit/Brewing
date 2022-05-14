package homeward.plugin.brewing.guis;

import com.google.gson.Gson;
import de.tr7zw.nbtapi.NBTFile;
import de.tr7zw.nbtapi.NBTItem;
import dev.lone.itemsadder.api.CustomStack;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.StorageGui;
import homeward.plugin.brewing.Brewing;
import homeward.plugin.brewing.beans.BarrelInventoryData;
import homeward.plugin.brewing.constants.PluginInformation;
import homeward.plugin.brewing.enumerates.ComponentEnum;
import homeward.plugin.brewing.enumerates.EnumBase;
import homeward.plugin.brewing.listeners.BrewingBarrelListener;
import homeward.plugin.brewing.utils.HomewardUtils;
import lombok.SneakyThrows;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.common.value.qual.IntRange;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import static homeward.plugin.brewing.enumerates.ComponentEnum.*;
import static homeward.plugin.brewing.utils.InventoryUtils.*;
import static homeward.plugin.brewing.constants.BarrelConstants.*;

public class PlayerGui {
    private NBTFile file;
    private Location barrelLocation;

    @SneakyThrows
    public StorageGui generateStorage() {
        ComponentEnum[] title = {NEGATIVE_10, GUI_CONTAINER, GAP_REGULAR, GUI_BARREL};
        StorageGui storageGui = generateStorageGui(3, NamedTextColor.WHITE, title);

        storageGui.setItem(SUBSTRATE_SLOT, new GuiItem(substrateSlot));
        storageGui.setItem(RESTRICTION_SLOT, new GuiItem(restrictionSlot));
        storageGui.setItem(YEAST_SLOT, new GuiItem(yeastSlot));
        storageGui.setItem(6, new GuiItem(substrateSlotState));
        storageGui.setItem(15, new GuiItem(restrictionSlotState));
        storageGui.setItem(24, new GuiItem(yeastSlotState));

        storageGui.setDefaultTopClickAction(event -> event.setCancelled(true));
        storageGui.addSlotAction(SUBSTRATE_SLOT, this::handleSlotClick);
        storageGui.addSlotAction(RESTRICTION_SLOT, this::handleSlotClick);
        storageGui.addSlotAction(YEAST_SLOT, this::handleSlotClick);
        storageGui.setCloseGuiAction(event -> {
            Map<HumanEntity, Location> barrelLocationMap = BrewingBarrelListener.getBarrelLocationMap();
            barrelLocationMap.remove(event.getPlayer());
        });

        return storageGui;
    }

    private void handleSlotClick(InventoryClickEvent event) {
        if (!(event.getClickedInventory().getHolder() instanceof StorageGui gui)) return;

        ItemStack cursorItem = event.getCursor();
        if (cursorItem == null) return;
        boolean cursorItemIsAir = cursorItem.getType() == Material.AIR;

        int eventSlot = event.getSlot();
        HumanEntity player = event.getWhoClicked();

        if (getItemInSlot(gui, eventSlot) == null) return;
        boolean slotNotDefined = PluginInformation.BARREL_DESCRIPTION_CUSTOM_MODEL_DATA_LIST.contains(new NBTItem(getItemInSlot(gui, eventSlot)).getInteger("CustomModelData"));

        if (slotNotDefined && !cursorItemIsAir) {
            setInventoryCursorItem(gui, eventSlot, cursorItem, player);
        } else if (!slotNotDefined) {
            if (cursorItemIsAir) {
                switch (eventSlot) {
                    case 2 -> setInventoryCursorItem(player, gui, eventSlot, substrateSlot);
                    case 11 -> setInventoryCursorItem(player, gui, eventSlot, restrictionSlot);
                    case 20 -> setInventoryCursorItem(player, gui, eventSlot, yeastSlot);
                }
            } else if (gui.getGuiItem(eventSlot) != null && gui.getGuiItem(eventSlot).getItemStack().getItemMeta().getCustomModelData() == player.getItemOnCursor().getItemMeta().getCustomModelData()) {
                ItemStack itemInInventory = gui.getGuiItem(eventSlot).getItemStack();
                if (itemInInventory.getAmount() == 64) {
                    exchangeInventoryCursorItem(player, gui, eventSlot);
                } else if (itemInInventory.getAmount() + cursorItem.getAmount() <= 64) {
                    refillItems(itemInInventory, gui, eventSlot, player, itemInInventory.getAmount(), cursorItem.getAmount());
                } else {
                    refillItems(itemInInventory, gui, eventSlot, player, cursorItem, itemInInventory.getAmount(), cursorItem.getAmount());
                }
            } else {
                exchangeInventoryCursorItem(player, gui, eventSlot);
            }
        } else return;


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
        BarrelInventoryData data = (BarrelInventoryData) HomewardUtils.deserializeBytes(bytesData.length == 0 ? null : bytesData);
        if (data == null) {
            data = new BarrelInventoryData();
        }

        if (gui.getGuiItem(eventSlot) == null) return;
        ItemStack rawItem = gui.getInventory().getItem(eventSlot);
        if (rawItem == null) return;
        NBTItem nbtTags = new NBTItem(rawItem);

        switch (eventSlot) {
            case 2 -> setSubstrateData(data, nbtTags, rawItem, gui, eventSlot);
            case 11 -> setRestrictionData(data, nbtTags, rawItem, gui, eventSlot);
            case 20 -> setYeastData(data, nbtTags, rawItem, gui, eventSlot);
        }


        if (gui.getInventory().getItem(eventSlot) == null) {
            switch (eventSlot) {
                case 2 -> gui.updateItem(SUBSTRATE_SLOT, substrateSlot);
                case 11 -> gui.updateItem(RESTRICTION_SLOT, restrictionSlot);
                case 20 -> gui.updateItem(YEAST_SLOT, yeastSlot);
            }
        }

        if (data.isHasSubstrate() && data.isHasRestriction() && data.isHasYeast() && !data.isBrewing()) {
            startBrewing(gui, data);
        }

        saveData(data);
    }

    public static void startBrewing(StorageGui gui, BarrelInventoryData data) {
        gui.setItem(4, new GuiItem(generateSlotItem(Material.PAPER, "wine", 4500)));
        gui.setItem(22, new GuiItem(generateSlotItem(Material.PAPER, "wine", 4500)));
        gui.setItem(13, new GuiItem(generateSlotItem(Material.PAPER, "wine", 4505)));
        gui.updateTitle(ChatColor.WHITE + ((TextComponent) gui.title()).content().replaceAll(((TextComponent) GAP_REGULAR.getComponent()).content() + ((TextComponent) GUI_BARREL.getComponent()).content(), ""));

        System.out.println(data);

        boolean[] hasSet = {false};
        if (data.isInitialize()) return;

        // Brewing.getConfigurationMap().forEach((k, v) -> {
        //     NBTItem substrate = new NBTItem(data.getSubstrate());
        //     NBTItem restriction = new NBTItem(data.getRestriction());
        //     NBTItem yeast = new NBTItem(data.getYeast());
        //     substrate.removeKey("PublicBukkitValues");
        //     restriction.removeKey("PublicBukkitValues");
        //     yeast.removeKey("PublicBukkitValues");
        //
        //     if (CustomStack.byItemStack(substrate.getItem()).getNamespacedID().equals(v.get("substrate").getAsString())) {
        //         String[] restrictionList = new Gson().fromJson(v.get("restriction").getAsString().replaceAll("^\"\"$", ""), String[].class);
        //         String[] yeastList = new Gson().fromJson(v.get("yeast").getAsString().replaceAll("^\"\"$", ""), String[].class);
        //
        //         boolean hasRestriction = Arrays.stream(restrictionList).toList().contains(CustomStack.byItemStack(restriction.getItem()).getNamespacedID());
        //         boolean hasYeast = Arrays.stream(yeastList).toList().contains(CustomStack.byItemStack(yeast.getItem()).getNamespacedID());
        //
        //         if (!(hasRestriction && hasYeast)) return;
        //
        //         data.setBrewingType(k).setOutPutItems(v.get("output").getAsString())
        //                 .setExpectOutPut(v.get("maxYield").getAsInt())
        //                 .setActualOutPut(HomewardUtils.getIntervalRandom(v.get("minYield").getAsInt(), v.get("maxYield").getAsInt()))
        //                 .setStoredOutPutItems(0).setBrewingTime(v.get("brewingCycle").getAsInt())
        //                 .setCurrentBrewingTime(0).setBrewing(true).setInitialize(true);
        //         hasSet[0] = true;
        //     }
        // });
    }

    private void setSubstrateData(BarrelInventoryData data, NBTItem nbtTags, ItemStack rawItem, StorageGui gui, int eventSlot) {
        if (data.getSubstrate() == null && nbtTags.getString("BrewingBarrel").equalsIgnoreCase("Substrate") && !data.isHasSubstrate()) {
            ItemStack itemInSlot = updateItem(rawItem, nbtTags);
            if (itemInSlot == null) return;
            data.setSubstrateSlot(rawItem).setSubstrate(itemInSlot).setHasSubstrate(true);
            updateItem(gui, itemInSlot, eventSlot, data);
            setTitle(gui, NamedTextColor.WHITE, GAP_REGULAR, GUI_SUBSTRATE);
        } else {
            data.setSubstrateSlot(rawItem);
        }
    }

    private void setRestrictionData(BarrelInventoryData data, NBTItem nbtTags, ItemStack rawItem, StorageGui gui, int eventSlot) {
        if (data.getRestriction() == null && nbtTags.getString("BrewingBarrel").equalsIgnoreCase("Restriction") && !data.isHasRestriction()) {
            ItemStack itemInSlot = updateItem(rawItem, nbtTags);
            if (itemInSlot == null) return;
            data.setRestrictionSlot(rawItem).setRestriction(itemInSlot).setHasRestriction(true);
            updateItem(gui, itemInSlot, eventSlot, data);
            setTitle(gui, NamedTextColor.WHITE, GAP_REGULAR, GUI_RESTRICTION);
        } else {
            data.setRestrictionSlot(rawItem);
        }
    }

    private void setYeastData(BarrelInventoryData data, NBTItem nbtTags, ItemStack rawItem, StorageGui gui, int eventSlot) {
        if (data.getYeast() == null && nbtTags.getString("BrewingBarrel").equalsIgnoreCase("Yeast") && !data.isHasYeast()) {
            ItemStack itemInSlot = updateItem(rawItem, nbtTags);
            if (itemInSlot == null) return;
            data.setYeastSlot(rawItem).setYeast(itemInSlot).setHasYeast(true);
            updateItem(gui, itemInSlot, eventSlot, data);
            setTitle(gui, NamedTextColor.WHITE, GAP_REGULAR, GUI_YEAST);
        } else {
            data.setYeastSlot(rawItem);
        }
    }

    private ItemStack updateItem(ItemStack rawItem, NBTItem nbtTags) {
        if (!nbtTags.hasKey("BrewingBarrel")) return null;
        if (rawItem == null) return null;
        rawItem.setAmount(rawItem.getAmount() - 1);
        return rawItem;
    }

    private void updateItem(StorageGui gui, ItemStack itemInSlot, @IntRange(from = 0, to = 26) int eventSlot, BarrelInventoryData data) {
        gui.updateItem(eventSlot, itemInSlot);
    }

    @SneakyThrows
    private void saveData(BarrelInventoryData data) {
        file.setByteArray(barrelLocation + "", HomewardUtils.serializeAsBytes(data));
        file.save();
    }

    public static void setTitle(StorageGui gui, NamedTextColor namedTextColor, EnumBase ...title) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(title).toList().forEach(enumBase -> sb.append(((TextComponent) enumBase.getComponent()).content()));

        String finalTitle = ChatColor.valueOf(namedTextColor.toString().toUpperCase()) + ((TextComponent) gui.title()).content() + sb;
        gui.updateTitle(finalTitle);
    }

    public static void removeTitle(EnumBase remove, EnumBase replace, StorageGui gui) {
        String removeContent = ((TextComponent) remove.getComponent()).content();
        String replaceContent = ((TextComponent) replace.getComponent()).content();
        TextComponent rawTitle = (TextComponent) gui.title();
        TextColor titleColor = rawTitle.style().color();
        String title = (titleColor == null ? "" : ChatColor.valueOf(titleColor.toString().toUpperCase()) + rawTitle.content().replaceAll(removeContent, "")) + replaceContent;
        gui.updateTitle(title);
    }

    private ItemStack getItemInSlot(StorageGui gui, @IntRange(from = 0, to = 26) int eventSlot) {
        GuiItem itemInSlot = gui.getGuiItem(eventSlot);
        if (itemInSlot.getItemStack().getType() == Material.AIR) return null;
        NBTItem itemNBT = new NBTItem(itemInSlot.getItemStack());
        itemNBT.removeKey("PublicBukkitValues");
        return itemNBT.getItem();
    }

    // the player not put an item in this slot && their cursor nonnull
    private void setInventoryCursorItem(StorageGui gui, int eventSlot, ItemStack cursorItem, HumanEntity player) {
        gui.updateItem(eventSlot, cursorItem);
        player.setItemOnCursor(new ItemStack(Material.AIR));
    }

    // the player has put an item in this slot && their cursor doesn't have any items
    private void setInventoryCursorItem(HumanEntity player, StorageGui gui, int eventSlot, ItemStack descriptionItem) {
        player.setItemOnCursor(getItemInSlot(gui, eventSlot));
        gui.updateItem(eventSlot, descriptionItem);
    }

    private void exchangeInventoryCursorItem(HumanEntity player, StorageGui gui, int eventSlot) {
        ItemStack itemOnCursor = player.getItemOnCursor();
        player.setItemOnCursor(getItemInSlot(gui, eventSlot));
        gui.updateItem(eventSlot, itemOnCursor);
    }

    // the player cursor item amount + event slot item amount < 64
    private void refillItems(ItemStack itemInInventory, StorageGui gui, int eventSlot, HumanEntity player, int itemInInventoryAmount, int itemInCursorAmount) {
        itemInInventory.setAmount(itemInCursorAmount + itemInInventoryAmount);
        gui.updateItem(eventSlot, itemInInventory);
        player.setItemOnCursor(new ItemStack(Material.AIR));
    }

    // the player cursor item amount + event slot item amount > 64
    private void refillItems(ItemStack itemInInventory, StorageGui gui, int eventSlot, HumanEntity player, ItemStack cursorItem, int itemInInventoryAmount, int itemInCursorAmount) {
        int cursorItemRemain = itemInCursorAmount + itemInInventoryAmount - 64;
        cursorItem.setAmount(cursorItemRemain);
        player.setItemOnCursor(cursorItem);
        itemInInventory.setAmount(64);
        gui.updateItem(eventSlot, itemInInventory);
    }
}
