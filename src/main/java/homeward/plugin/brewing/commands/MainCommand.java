package homeward.plugin.brewing.commands;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtinjector.NBTInjector;
import homeward.plugin.brewing.utils.ConfigurationUtil;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

@Command("homewardbrewing")
@Alias("hwb")
public class MainCommand extends CommandBase {

    @Default
    public void defaultCommand(final CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7Homeward brewing (协调酿酒) version &61.0.2"));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7基于GUI界面的多材料酿酒系统"));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7输入&6/hwb help &7来获取所有指令帮助"));
    }

    @SubCommand("addnbt")
    public void addNBT(final CommandSender commandSender) {
        Player player = (Player) commandSender;
        Block targetBlock = player.getTargetBlock(5);
        NBTCompound comp = NBTInjector.getNbtData(targetBlock.getState());
        comp.setString("Foo", "Bar");
    }

    @SubCommand("getnbt")
    public void getNBT(final CommandSender commandSender) {
        Player player = (Player) commandSender;
        Block targetBlock = player.getTargetBlock(5);
        NBTCompound comp = NBTInjector.getNbtData(targetBlock.getState());
        player.sendMessage((BaseComponent) comp.getKeys());
    }


    @Permission("homeward.admin")
    @SubCommand("reload")
    @Alias("r")
    @WrongUsage("&c/hwb <option>")
    public void reloadConfiguration(CommandSender commandSender) {
        ConfigurationUtil.reload();
        commandSender.sendMessage(ChatColor.GREEN + "homeward journey brewing plugin configurations reloaded");
    }

}