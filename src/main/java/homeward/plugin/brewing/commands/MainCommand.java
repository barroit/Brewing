package homeward.plugin.brewing.commands;

import de.tr7zw.nbtapi.NBTFile;
import homeward.plugin.brewing.beans.BarrelInventoryData;
import homeward.plugin.brewing.data.BrewingBarrelData;
import homeward.plugin.brewing.utils.CommonUtils;
import homeward.plugin.brewing.utils.ConfigurationUtils;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    @SubCommand("testBarrelInventoryData")
    public void testBarrelInventoryData(CommandSender sender) {
        Player player = (Player) sender;
        Block targetBlock = player.getTargetBlock(5);
        if (targetBlock == null) return;

        BarrelInventoryData inventoryData = new BarrelInventoryData()
                .setSubstrate(new ItemStack(Material.GREEN_WOOL))
                .setRestriction(new ItemStack(Material.RED_WOOL))
                .setYeast(new ItemStack(Material.PINK_WOOL))
                .setBrewingType("dark-wine")
                .setOutPutItems("old vines")
                .setExpectOutPut(4)
                .setActualOutPut(3)
                .setBrewingTime(5);
        // byte[] encodeObject = CommonUtils.encodeBukkitObject(inventoryData);

        NBTFile file;

        try {
            file = new NBTFile(new File(player.getWorld().getName(), "brew.bnt"));
            // file.setByteArray(targetBlock.getLocation() + "", encodeObject);
            file.setObject(targetBlock.getLocation() + "", inventoryData);
            file.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // byte[] dataNotDecode = file.getByteArray(targetBlock.getLocation() + "");
        // if (dataNotDecode == null) return;
        // BarrelInventoryData data = (BarrelInventoryData) CommonUtils.decodeBukkitObject(dataNotDecode);

        BarrelInventoryData object = file.getObject(targetBlock.getLocation() + "", BarrelInventoryData.class);

        System.out.println(object.getSubstrate().getType());
    }

    //不要删除 测试用的
    @SubCommand("storagedata")
    @Alias("sd")
    public void storageData(CommandSender commandSender) throws IOException {
        Player player = (Player) commandSender;
        Block targetBlock = player.getTargetBlock(5);

        if (targetBlock == null) return;

        NBTFile file = new NBTFile(new File(player.getWorld().getName(), "brew.nbt"));
        //file.hasKey(key)

        BrewingBarrelData thisBlockBrewingData = new BrewingBarrelData();
        thisBlockBrewingData.setSubstrate(new ItemStack(Material.GOLD_INGOT));

        file.setObject(targetBlock.getLocation() + "", thisBlockBrewingData);
        file.save();
        player.sendMessage(String.valueOf(file.getObject(targetBlock.getLocation() + "", BrewingBarrelData.class).getSubstrate().getType()));

    }

    /**
     * 获取当前方块的nbt字符串
     * @param commandSender
     * @throws IOException
     */
    @SubCommand("getnbt")
    public void getNBT(final CommandSender commandSender) throws Exception {
        Player player = (Player) commandSender;
        Block targetBlock = player.getTargetBlock(5);

        if (targetBlock == null) return;

        NBTFile file = new NBTFile(new File(player.getWorld().getName(), "brew.nbt"));
        if (file.hasKey(targetBlock.getLocation() + "")) {
            String object = file.getString(targetBlock.getLocation() + "");

            // BrewingBarrelData o = (BrewingBarrelData) ItemStackUtils.decodeObject(object);

            // System.out.println(o.getSubstrate());
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6!&7] 当前方块没有储存任何数据"));
        }
    }

    @SubCommand("worldfile")
    @Alias("wf")
    public void worldFileManipulate(final CommandSender commandSender) throws Exception {
        Player player = (Player) commandSender;
    }

    @Permission("homeward.admin")
    @SubCommand("reload")
    @Alias("r")
    @WrongUsage("&c/hwb <option>")
    public void reloadConfiguration(CommandSender commandSender) {
        ConfigurationUtils.reload();
        commandSender.sendMessage(ChatColor.GREEN + "homeward journey brewing plugin configurations reloaded");
    }

}