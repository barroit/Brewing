package homeward.plugin.brewing.command;

import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@Command("brewing")
@SuppressWarnings("unused")
public class BrewingCommand extends CommandBase {
    @Default
    public void defaultCommand(final CommandSender commandSender) {
        List<String> usageList = new ArrayList<>();
        usageList.add(translateAlternateColorCode("&d/brewing reload"));
        usageList.add(translateAlternateColorCode("&d/brewing get <item-type> <id> <amount>"));
        commandSender.sendMessage(usageList.toArray(new String[]{}));
    }

    private String translateAlternateColorCode(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
