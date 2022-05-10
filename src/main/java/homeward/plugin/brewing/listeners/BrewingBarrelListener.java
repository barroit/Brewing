package homeward.plugin.brewing.listeners;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import de.tr7zw.nbtapi.NBTFile;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.StorageGui;
import homeward.plugin.brewing.Brewing;
import homeward.plugin.brewing.beans.BarrelInventoryData;
import homeward.plugin.brewing.utils.CommonUtils;
import homeward.plugin.brewing.guis.PlayerGui;
import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

import static homeward.plugin.brewing.constants.BarrelConstants.*;
import static homeward.plugin.brewing.enumerates.ComponentEnum.*;

public class BrewingBarrelListener implements Listener {
    @Getter
    private static final Map<Location, BaseGui> barrelGUIMap = new HashMap<>();
    @Getter
    private static final Map<HumanEntity, Location> barrelLocationMap = new HashMap<>();

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

        if (data.getSubstrateSlot() != null) {
            gui.updateItem(SUBSTRATE_SLOT, data.getSubstrateSlot());
        }
        if (data.getRestrictionSlot() != null) {
            gui.updateItem(RESTRICTION_SLOT, data.getRestrictionSlot());
        }
        if (data.getYeastSlot() != null) {
            gui.updateItem(YEAST_SLOT, data.getYeastSlot());
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
            PlayerGui.startBrewing(gui, data);
        }
    }
}