package homeward.plugin.brewing.listeners;

import de.tr7zw.nbtapi.NBTFile;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.StorageGui;
import homeward.plugin.brewing.beans.BarrelInventoryData;
import homeward.plugin.brewing.utils.CommonUtils;
import homeward.plugin.brewing.guis.PlayerGui;
import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static homeward.plugin.brewing.constants.BarrelConstants.*;
import static homeward.plugin.brewing.enumerates.ComponentEnum.*;
import static homeward.plugin.brewing.utils.InventoryUtils.generateSlotItem;

public class BrewingBarrelListener implements Listener {
    @Getter private static final Map<Location, BaseGui> barrelGUIMap = new HashMap<>();
    @Getter private static final Map<HumanEntity, Location> barrelLocationMap = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(final CustomBlockInteractEvent event) {
        if (!event.getNamespacedID().contains("homeward:brewing_barrel_")) return;

        Location barrelLocation = event.getBlockClicked().getLocation();
        Player player = event.getPlayer();

        if (barrelGUIMap.containsKey(barrelLocation)) {
            barrelGUIMap.get(barrelLocation).open(player);
            barrelLocationMap.put(player, barrelLocation);
            return;
        }

        StorageGui storageGui = new PlayerGui().generateStorage();
        initializeSlot(barrelLocation, storageGui);

        barrelGUIMap.put(barrelLocation, storageGui);
        barrelLocationMap.put(player, barrelLocation);

        storageGui.open(player);
    }

    @SneakyThrows
    public static void initializeSlot(@NotNull Location location, @NotNull StorageGui gui) {
        NBTFile file = new NBTFile(new File(location.getWorld().getName(), "brew.nbt"));
        byte[] bytesData = file.getByteArray(location + "");
        BarrelInventoryData data = (BarrelInventoryData) CommonUtils.decodeBukkitObject(bytesData.length == 0 ? null : bytesData);
        if (data == null) return;

        if (data.getSubstrate() != null) {
            gui.updateItem(SUBSTRATE_SLOT, data.getSubstrate());
        }
        if (data.getRestriction() != null) {
            gui.updateItem(RESTRICTION_SLOT, data.getRestriction());
        }
        if (data.getYeast() != null) {
            gui.updateItem(YEAST_SLOT, data.getYeast());
        }

        if (data.isHasSubstrate()) {
            PlayerGui.setTitle(gui, NamedTextColor.WHITE, GAP_REGULAR, GUI_SUBSTRATE);
        }
        if (data.isHasRestriction()) {
            PlayerGui.setTitle(gui, NamedTextColor.WHITE, GAP_REGULAR, GUI_RESTRICTION);
        }
        if (data.isHasYeast()) {
            PlayerGui.setTitle(gui, NamedTextColor.WHITE, GAP_REGULAR, GUI_YEAST);
        }

        if (data.isBrewing()) {
            gui.setItem(4, new GuiItem(generateSlotItem(Material.PAPER, "wine", 4500)));
            gui.setItem(22, new GuiItem(generateSlotItem(Material.PAPER, "wine", 4500)));
            gui.setItem(13, new GuiItem(generateSlotItem(Material.PAPER, "wine", 4505)));
            gui.updateTitle(ChatColor.WHITE + ((TextComponent) gui.title()).content().replaceAll(((TextComponent) GAP_REGULAR.getComponent()).content() + ((TextComponent) GUI_BARREL.getComponent()).content(), ""));
        }
    }
}