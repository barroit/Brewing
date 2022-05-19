package homeward.plugin.brewing.commands;

import dev.triumphteam.gui.guis.BaseGui;
import homeward.plugin.brewing.guis.RecipesPreviewGui;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

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

        if (playerGuiInstanceMap.containsKey(player)) {
            playerGuiInstanceMap.get(player).open(player);
        } else {
            BaseGui gui = RecipesPreviewGui.getInstance(6, 21).getGui();
            gui.open(player);
            playerGuiInstanceMap.put(player, gui);
        }
    }
}
