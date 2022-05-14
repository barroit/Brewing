package homeward.plugin.brewing.commands;

import homeward.plugin.brewing.Brewing;
import homeward.plugin.brewing.configurations.RecipesConfigurationLoader;
import homeward.plugin.brewing.configurations.RecipesConfigurationLoader.RecipesConfigurationLoaderBuilder;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;

import java.math.RoundingMode;

@Command("mock")
@Alias("m")
public class MockFileConfigurationTest extends CommandBase {

    public MockFileConfigurationTest() {
    }

    @Default
    public void defaultCommand(CommandSender commandSender) {
        commandSender.sendMessage(Brewing.getInstance().recipesMap().keySet() + "");
    }
}