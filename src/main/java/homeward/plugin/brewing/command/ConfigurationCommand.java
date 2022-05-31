package homeward.plugin.brewing.command;

import homeward.plugin.brewing.loader.ConfigurationLoader;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;


@Command("brewing")
@SuppressWarnings("unused")
public class ConfigurationCommand extends CommandBase {
    @SubCommand("reload")
    public void reload(final CommandSender commandSender) {
        ConfigurationLoader.getInstance().reload();
    }
}
