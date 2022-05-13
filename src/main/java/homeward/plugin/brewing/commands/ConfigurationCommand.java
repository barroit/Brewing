package homeward.plugin.brewing.commands;

import homeward.plugin.brewing.Brewing;
import homeward.plugin.brewing.utils.ConfigurationUtils;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@Command("HomewardBrewing")
@Alias("hb")
public class ConfigurationCommand extends CommandBase {
    @Default
    public void reload(CommandSender commandSender) {
        ConfigurationUtils.reload();
        commandSender.sendMessage(ChatColor.GREEN + "homeward journey brewing plugin configurations reloaded");
        Brewing.getInstance().update();
    }
}
