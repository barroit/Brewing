package homeward.plugin.brewing.commands;

import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;

@Command("mock")
@Alias("m")
public class MockFileConfigurationTest extends CommandBase {

    public MockFileConfigurationTest() {
    }

    @Default
    public void defaultCommand(CommandSender commandSender) {
    }
}