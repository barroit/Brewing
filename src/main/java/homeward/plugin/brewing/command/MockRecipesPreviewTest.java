package homeward.plugin.brewing.command;

import dev.triumphteam.gui.guis.BaseGui;
import homeward.plugin.brewing.Main;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

@Command("mock")
@Alias("m")
public class MockRecipesPreviewTest extends CommandBase {
    private final Map<Player, BaseGui> playerGuiInstanceMap;

    public MockRecipesPreviewTest() {
        playerGuiInstanceMap = new LinkedHashMap<>();
    }

    @Default
    public void defaultAction(CommandSender commandSender) {
        Player player = (Player) commandSender;

        // if (playerGuiInstanceMap.containsKey(player)) {
        //     playerGuiInstanceMap.get(player).open(player);
        // } else {
        //     BaseGui gui = RecipesPreviewGui.getInstance(6, 21).getGui();
        //     gui.open(player);
        //     playerGuiInstanceMap.put(player, gui);
        // }
    }

    @SubCommand("nbt")
    public void testNBT(CommandSender commandSender) {
        Player player = (Player) commandSender;
        // Main.tierItemStackMap().forEach((k, v) -> {
        //     player.getInventory().addItem(v);
        // });
        // Main.outputItemStackMap().forEach((k, v) -> {
        //     player.getInventory().addItem(v);
        // });
        Main.substrateItemStackMap().forEach((k, v) -> {
            player.getInventory().addItem(v);
        });
    }
}
