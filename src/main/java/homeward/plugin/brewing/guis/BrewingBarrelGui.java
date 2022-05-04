package homeward.plugin.brewing.guis;

import de.tr7zw.nbtapi.NBTItem;
import homeward.plugin.brewing.constants.BaseInfo;
import homeward.plugin.brewing.enumerates.ComponentEnum;
import homeward.plugin.brewing.enumerates.EnumBase;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

import static homeward.plugin.brewing.utils.InventoryUtils.generateSlotItem;

public class BrewingBarrelGui extends GuiBase {
    private final ItemStack air = new ItemStack(Material.AIR);

    public BrewingBarrelGui(EnumBase enumBase, int slot) {
        super(enumBase.getComponent(), slot);
    }

    {
        setGuiItems();
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        ItemStack cursor = event.getCursor();
        if (cursor == null || clickedInventory == null) return;

        boolean cursorIsAir = cursor.getType() == Material.AIR;
        int eventSlot = event.getSlot();
        HumanEntity player = event.getWhoClicked();

        ItemStack rawItem = clickedInventory.getItem(event.getSlot());
        if (rawItem == null) return;

        boolean isDescription = BaseInfo.BARREL_DESCRIPTION_CUSTOM_MODEL_DATA_LIST.contains(new NBTItem(rawItem).getInteger("CustomModelData"));

        switch (eventSlot) {
            case 2 -> setTitle(ComponentEnum.BARREL_TITLE_WITH_SUBSTRATE);
            case 11 -> setTitle(ComponentEnum.BARREL_TITLE_WITH_RESTRICTION);
            case 20 -> setTitle(ComponentEnum.BARREL_TITLE_WITH_YEAST);
        }

        if (isDescription && !cursorIsAir) {
            clickedInventory.setItem(eventSlot, cursor);
            player.setItemOnCursor(air);



            return;
        }

        if (!isDescription && cursorIsAir) {

            player.setItemOnCursor(clickedInventory.getItem(eventSlot));
            switch (eventSlot) {
                case 2 -> inventory.setItem(2, utils.substrateSlot);
                case 11 -> inventory.setItem(11, utils.restrictionSlot);
                case 20 -> inventory.setItem(20, utils.yeastSlot);
            }
            return;
        }

        if (!isDescription) {
            ItemStack itemInInventory = clickedInventory.getItem(eventSlot);
            ItemStack itemOnCursor = player.getItemOnCursor();
            player.setItemOnCursor(itemInInventory);
            clickedInventory.setItem(eventSlot, itemOnCursor);
        }
    }

    private void setGuiItems() {
        Map<Integer, ItemStack> items = new LinkedHashMap<>();

        items.put(2, utils.substrateSlot);
        items.put(11, utils.restrictionSlot);
        items.put(20, utils.yeastSlot);
        items.put(4, utils.barrelSlot);
        items.put(13, utils.barrelSlot);
        items.put(22, utils.barrelSlot);
        items.put(6, utils.substrateSlotState);
        items.put(15, utils.restrictionSlotState);
        items.put(24, utils.yeastSlotState);

        setItem(items);
    }

    private static class utils {
        static ItemStack substrateSlot = generateSlotItem(Material.PAPER, ComponentEnum.SLOT_SUBSTRATE, 4501);
        static ItemStack restrictionSlot = generateSlotItem(Material.PAPER, ComponentEnum.SLOT_RESTRICTION, 4502);
        static ItemStack yeastSlot = generateSlotItem(Material.PAPER, ComponentEnum.SLOT_YEAST, 4503);

        static ItemStack barrelSlot = generateSlotItem(Material.PAPER, ComponentEnum.SLOT_BARREL, 4500);

        static ItemStack substrateSlotState = generateSlotItem(Material.PAPER, ComponentEnum.SLOT_SUBSTRATE_STATE, 4500);
        static ItemStack restrictionSlotState = generateSlotItem(Material.PAPER, ComponentEnum.SLOT_RESTRICTION_STATE, 4500);
        static ItemStack yeastSlotState = generateSlotItem(Material.PAPER, ComponentEnum.SLOT_YEAST_STATE, 4500);
    }
}
