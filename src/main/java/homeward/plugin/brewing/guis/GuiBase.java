package homeward.plugin.brewing.guis;

import homeward.plugin.brewing.enumerates.EnumBase;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class GuiBase implements InventoryHolder {
    protected Inventory inventory;
    private Component title;
    private final Map<Integer, ItemStack> guiItems;
    private final int slots;

    public GuiBase(Component title, int slots) {
        this.slots = slots;
        this.title = title;
        guiItems = new LinkedHashMap<>();
        inventory = Bukkit.createInventory(this, this.slots, title);
    }

    public void setItem(Map<Integer, ItemStack> items) {
        guiItems.putAll(items);
    }

    public void setItem(Integer slot, ItemStack item) {
        inventory.setItem(slot, item);
        guiItems.put(slot, item);
    }

    public void removeItem(Integer slot) {
        inventory.clear(slot);
        guiItems.remove(slot);
    }

    private void generateItemSlot() {
        guiItems.entrySet().forEach((Map.Entry<Integer, ItemStack> entry) -> {
            inventory.setItem(entry.getKey(), entry.getValue());
        });
    }

    public void open(HumanEntity player) {
        if (player.isSleeping()) return;

        inventory.clear();
        generateItemSlot();
        player.openInventory(inventory);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public GuiBase setTitle(EnumBase enumBase) {
        title = enumBase.getComponent();
        return this;
    }


    public abstract void handleClick(InventoryClickEvent event);
}
