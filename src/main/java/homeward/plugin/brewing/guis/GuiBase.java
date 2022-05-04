package homeward.plugin.brewing.guis;

import homeward.plugin.brewing.enumerates.EnumBase;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public abstract class GuiBase implements InventoryHolder {
    protected Inventory inventory;
    private Player player;
    private Component title;

    public GuiBase(EnumBase enumBase) {
        this.title = enumBase.getComponent();
    }

    public abstract int getSlots();

    public abstract void handleClick(InventoryClickEvent event);

    public void handleDrag(InventoryDragEvent event) {}

    public abstract void setGuiItems();

    public void initialize() {
        inventory = Bukkit.createInventory(this, getSlots(), getTitle());
        this.setGuiItems();
    }

    public void preInitialize() {
        inventory = Bukkit.createInventory(this, getSlots(), getTitle());
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

    public Component getTitle() {
        return title;
    }

    public Component setTitle(EnumBase enumBase) {
        title = enumBase.getComponent();
        return title;
    }
}
