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

    public String getGuiName() {
        return null;
    }

    public abstract int getSlots();

    public abstract void handleClick(InventoryClickEvent event);

    public void handleDrag(InventoryDragEvent event) {}

    public abstract void setGuiItems();

    public void initialize() {
        inventory = Bukkit.createInventory(this, getSlots());
        this.setGuiItems();
    }

    public void open() {
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
