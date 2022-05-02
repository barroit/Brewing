package homeward.plugin.brewing.guis;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public abstract class GuiBase implements InventoryHolder {
    protected Inventory inventory;
    protected Player player;

    public abstract String getGuiName();

    public abstract int getSlots();

    public abstract void handleClick(InventoryClickEvent event);

    public abstract void handleDrag(InventoryDragEvent event);

    public abstract void setGuiItems();

    public void initialize() {
        inventory = Bukkit.createInventory(this, getSlots());
    }

    public void open() {
        this.setGuiItems();
        player.openInventory(inventory);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public GuiBase setPlayer(Player player) {
        this.player = player;
        return this;
    }
}
