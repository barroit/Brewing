package homeward.plugin.brewing.listeners;

import de.tr7zw.nbtapi.NBTFile;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.StorageGui;
import homeward.plugin.brewing.beans.BarrelInventoryData;
import homeward.plugin.brewing.enumerates.ComponentEnum;
import homeward.plugin.brewing.utils.CommonUtils;
import homeward.plugin.brewing.utils.GuiUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static homeward.plugin.brewing.constants.BarrelConstants.*;

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

        StorageGui storageGui = new GuiUtils().generateStorage();
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
            GuiUtils.setTitle(ComponentEnum.BARREL_TITLE_WITH_SUBSTRATE, gui);
        }
        if (data.isHasRestriction()) {
            GuiUtils.setTitle(ComponentEnum.BARREL_TITLE_WITH_RESTRICTION, gui);
        }
        if (data.isHasYeast()) {
            GuiUtils.setTitle(ComponentEnum.BARREL_TITLE_WITH_YEAST, gui);
        }
        System.out.println(data);
        // if (data.isBrewing()) {
        //
        // }
    }
}