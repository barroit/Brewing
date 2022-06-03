package homeward.plugin.brewing.command;

import homeward.plugin.brewing.gui.LegacyRecipesPreviewGui;
import homeward.plugin.brewing.gui.RecipesPreviewGui;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("mock")
@Alias("m")
@SuppressWarnings("unused")
public class MockRecipesPreviewTest extends CommandBase {

    @Default
    public void defaultAction(CommandSender commandSender) {
        Player player = (Player) commandSender;
        RecipesPreviewGui.getGui().open(player);
        // LegacyRecipesPreviewGui instance = LegacyRecipesPreviewGui.getInstance(6, 27);
        // instance.getGui().open(player);
    }
}
