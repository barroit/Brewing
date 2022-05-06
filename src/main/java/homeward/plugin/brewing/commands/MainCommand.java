package homeward.plugin.brewing.commands;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import de.tr7zw.nbtapi.NBTFile;
import homeward.plugin.brewing.Brewing;
import homeward.plugin.brewing.beans.BarrelInventoryData;
import homeward.plugin.brewing.data.BrewingBarrelData;
import homeward.plugin.brewing.events.BrewDataProcessEvent;
import homeward.plugin.brewing.utils.ConfigurationUtils;
import homeward.plugin.brewing.utils.ItemStackUtils;
import lombok.SneakyThrows;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

@Command("homewardbrewing")
@Alias("hwb")
public class MainCommand extends CommandBase {

    @Default
    public void defaultCommand(final CommandSender commandSender) {

        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Homeward brewing (协调酿酒) version &61.0.2"));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7基于GUI界面的多材料酿酒系统"));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7输入&6/hwb help &7来获取所有指令帮助"));

    }

    @SubCommand("testLocationCast")
    public void testLocationCast(CommandSender sender) throws Exception {
        NBTFile file = new NBTFile(new File(((Player) sender).getWorld().getName(), "brew.nbt"));
        Set<String> barrelLocations = file.getKeys();
        barrelLocations.forEach(v -> {
            String jsonString = v.replaceAll(".*(\\{)(.+?)},?", "$1$2,").replaceAll("=", ":");
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            String worldName = jsonObject.get("name").getAsString();
            float x = jsonObject.get("x").getAsFloat();
            float y = jsonObject.get("y").getAsFloat();
            float z = jsonObject.get("z").getAsFloat();
            float pitch = jsonObject.get("pitch").getAsFloat();
            float yaw = jsonObject.get("yaw").getAsFloat();
            Location location = new Location(((Player) sender).getWorld(), x, y, z, yaw, pitch);
        });
    }

    @SubCommand("testBarrelInventoryData")
    public void testBarrelInventoryData(CommandSender sender) throws IOException {

        Player player = (Player) sender;
        Block targetBlock = player.getTargetBlock(5);
        if (targetBlock == null) return;

        BarrelInventoryData inventoryData = new BarrelInventoryData()
                .setSubstrate(new ItemStack(Material.GREEN_WOOL))
                .setRestriction(new ItemStack(Material.RED_WOOL))
                .setYeast(new ItemStack(Material.PINK_WOOL))
                .setBrewingType("dark vine")
                .setOutPutItems("old_vines")
                .setExpectOutPut(4)
                .setActualOutPut(3)
                .setBrewingTime(5);

        String dataS = ItemStackUtils.encodeObject(inventoryData);


        NBTFile file = new NBTFile(new File(player.getWorld().getName(), "brew.nbt"));
        file.setObject(targetBlock.getLocation() + "", inventoryData);

        file.save();
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

    //不要删除 测试用的
    @SubCommand("addstage")
    @Alias("as")
    public void addStage(CommandSender commandSender) throws IOException {
        Player player = (Player) commandSender;
        Bukkit.getServer().getPluginManager().callEvent(new BrewDataProcessEvent(player.getWorld(), player.getWorld().getWorldFolder()));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7增加了 &61 &7酿造周期"));
    }


    // /**
    //  * 设置你指向的方块指定字符串
    //  * @param commandSender
    //  * @param args
    //  * @throws IOException
    //  */
    // @SubCommand("addnbt")
    // public void addNBT(final CommandSender commandSender, final String[] args) throws IOException {
    //
    //     Player player = (Player) commandSender;
    //
    //     if (args.length == 1) {
    //         player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&c!&7] 你至少要提供一个字符串来储存 /hwb addnbt <>"));
    //         return;
    //     }
    //
    //
    //     Block targetBlock = player.getTargetBlock(5);
    //
    //     NBTFile file = new NBTFile(new File(player.getWorld().getName(), "brew.nbt"));
    //     int blockX = targetBlock.getLocation().getBlockX();
    //     int blockY = targetBlock.getLocation().getBlockY();
    //     int blockZ = targetBlock.getLocation().getBlockZ();
    //
    //     String key = "" + blockX + blockY + blockZ;
    //     BrewingData bd = new BrewingData(args[1]);
    //     if (file.hasKey(key)) {
    //         file.setObject(key, bd);
    //     } else {
    //         file.setObject(key, bd);
    //         player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&2+&7] 储存成功"));
    //     }
    //     file.save();
    //
    //
    // }

    /**
     * 获取当前方块的nbt字符串
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

    @Permission("homeward.admin")
    @SubCommand("reload")
    @Alias("r")
    @WrongUsage("&c/hwb <option>")
    public void reloadConfiguration(CommandSender commandSender) {
        ConfigurationUtils.reload();
        commandSender.sendMessage(ChatColor.GREEN + "homeward journey brewing plugin configurations reloaded");
        Brewing.getInstance().update();
    }

}