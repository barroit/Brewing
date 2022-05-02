package homeward.plugin.brewing.commands;

import de.tr7zw.nbtapi.NBTFile;
import homeward.plugin.brewing.data.BrewingData;
import homeward.plugin.brewing.utils.ConfigurationUtil;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

@Command("homewardbrewing")
@Alias("hwb")
public class MainCommand extends CommandBase {

    @Default
    public void defaultCommand(final CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Homeward brewing (协调酿酒) version &61.0.2"));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7基于GUI界面的多材料酿酒系统"));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7输入&6/hwb help &7来获取所有指令帮助"));
    }

    /**
     * 设置你指向的方块指定字符串
     * @param commandSender
     * @param args
     * @throws IOException
     */
    @SubCommand("addnbt")
    public void addNBT(final CommandSender commandSender, final String[] args) throws IOException {

        Player player = (Player) commandSender;

        if (args.length == 1) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&c!&7] 你至少要提供一个字符串来储存 /hwb addnbt <>"));
            return;
        }


        Block targetBlock = player.getTargetBlock(5);

        NBTFile file = new NBTFile(new File(player.getWorld().getName(), "brew.nbt"));
        int blockX = targetBlock.getLocation().getBlockX();
        int blockY = targetBlock.getLocation().getBlockY();
        int blockZ = targetBlock.getLocation().getBlockZ();

        String key = "" + blockX + blockY + blockZ;
        BrewingData bd = new BrewingData(args[1]);
        if (file.hasKey(key)) {
            file.setObject(key, bd);
        } else {
            file.setObject(key, bd);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&2+&7] 储存成功"));
        }
        file.save();


    }

    /**
     * 获取当前方块的nbt字符串
     * @param commandSender
     * @throws IOException
     */
    @SubCommand("getnbt")
    public void getNBT(final CommandSender commandSender) throws IOException {
        Player player = (Player) commandSender;
        Block targetBlock = player.getTargetBlock(5);

        int blockX = targetBlock.getLocation().getBlockX();
        int blockY = targetBlock.getLocation().getBlockY();
        int blockZ = targetBlock.getLocation().getBlockZ();

        String key = "" + blockX + blockY + blockZ;

        NBTFile file = new NBTFile(new File(player.getWorld().getName(), "brew.nbt"));
        if (file.hasKey(key)) {
            BrewingData object = file.getObject(key, BrewingData.class);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&2i&7] 当前方块数据为" + object.info));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6!&7] 当前方块没有储存任何数据"));
        }
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