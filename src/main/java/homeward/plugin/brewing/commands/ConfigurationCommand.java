package homeward.plugin.brewing.commands;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import homeward.plugin.brewing.Brewing;
import homeward.plugin.brewing.utils.ConfigurationUtils;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("HomewardBrewing")
@Alias("brewing")
public class ConfigurationCommand extends CommandBase {
    @SubCommand("reload")
    public void reloadConfiguration(CommandSender commandSender) {
        ConfigurationUtils.reload();
        commandSender.getServer().getPluginManager().callEvent(new ItemsAdderLoadDataEvent(true));
        commandSender.sendMessage(ChatColor.GREEN + "homeward journey brewing plugin configurations reloaded");
        Brewing.getInstance().update();
    }

    public void reloadTexture(CommandSender commandSender) {
        Player player = (Player) commandSender;
        // player.res
    }
}
