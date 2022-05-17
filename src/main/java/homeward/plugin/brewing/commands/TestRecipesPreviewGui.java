package homeward.plugin.brewing.commands;

import dev.triumphteam.gui.guis.BaseGui;
import homeward.plugin.brewing.guis.RecipesPreviewGui;
import lombok.experimental.Accessors;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

@Command("m")
@Accessors(fluent = true)
public class TestRecipesPreviewGui extends CommandBase {
    private final Map<Player, BaseGui> playerGuiInstanceMap;

    public TestRecipesPreviewGui() {
        playerGuiInstanceMap = new LinkedHashMap<>();
    }

    @Default
    public void defaultAction(CommandSender commandSender) {
        // Player player = (Player) commandSender;
        //
        // ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        //
        // PacketContainer resourcePack = new PacketContainer(PacketType.Play.Server.RESOURCE_PACK_SEND);
        // // resourcePack.
        //
        // protocolManager.sendServerPacket((Player) commandSender, );

        Player player = (Player) commandSender;

        if (playerGuiInstanceMap.containsKey(player)) {
            playerGuiInstanceMap.get(player).open(player);
        } else {
            BaseGui gui = RecipesPreviewGui.getInstance(6, 21).getGui();
            gui.open(player);
            playerGuiInstanceMap.put(player, gui);
        }
    }
}
