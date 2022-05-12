package homeward.plugin.brewing.commands;

import com.google.gson.*;
import de.tr7zw.nbtapi.NBTFile;
import dev.lone.itemsadder.api.CustomStack;
import homeward.plugin.brewing.Brewing;
import homeward.plugin.brewing.beans.RecipesItem;
import homeward.plugin.brewing.data.BrewingBarrelData;
import homeward.plugin.brewing.events.BrewDataProcessEvent;
import homeward.plugin.brewing.utils.ConfigurationUtils;
import lombok.SneakyThrows;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static homeward.plugin.brewing.utils.HomewardUtils.*;

@Command("homewardbrewing")
@Alias("hwb")
public class MainCommand extends CommandBase {
    private final Map<String, JsonElement> configurationMap = new LinkedHashMap<>();

    @SubCommand("testSerialize")
    public void testSerialize(CommandSender commandSender) {
        ItemStack grape = CustomStack.getInstance("homeward:grape").getItemStack();
        
        // Serialize and deserialize using ItemStack static method before serialize and deserialize the bean class
        ItemStack itemStack = ItemStack.deserializeBytes(grape.serializeAsBytes());

        RecipesItem recipesItem = new RecipesItem();

        byte[] recipesItemByteArray = serializeAsBytes(recipesItem);
        RecipesItem recipes = (RecipesItem) deserializeBytes(recipesItemByteArray); // now working fine

        System.out.println(recipes.substrate());
    }

    public boolean isSimilar(ItemStack stack1, ItemStack stack2) {
        if (stack2 == null) {
            return false;
        }
        if (stack2 == stack1) {
            return true;
        }
        Material comparisonType =
                (stack1.getType().isLegacy()) ? Bukkit.getUnsafe().fromLegacy(stack1.getData(), true) : stack1.getType();

        return comparisonType == stack2.getType() &&
                stack1.getDurability() == stack2.getDurability() &&
                stack1.hasItemMeta() == stack2.hasItemMeta() &&
                (stack1.hasItemMeta() ? Bukkit.getItemFactory().equals(stack1.getItemMeta(), stack2.getItemMeta()) : true);
    }
    

    @SubCommand("testMMOItems")
    public void testMMOItems(CommandSender commandSender) {
        Player player = (Player) commandSender;

        FileConfiguration recipes = ConfigurationUtils.get("recipes");
        // [{from=grapeInHand, item={material=POTION, potion-type=SPEED, amount=1}},
        // {from=ItemsAdder, item={namespacedId=homeward:grape, amount=1}},
        // {from=MMOItems, item={type=BREWING, id=PEAR, scaled=1, tier=UNCOMMON, amount=1}}]

        JsonArray jsonElements = new Gson().fromJson(recipes.getConfigurationSection("wine").getString("substrate").replaceAll("=", ":"), JsonArray.class);
        jsonElements.forEach(v -> {
            JsonObject jsonObject = v.getAsJsonObject();
            String from = jsonObject.get("from").getAsString();
            JsonObject item = jsonObject.get("item").getAsJsonObject();
            PlayerInventory inventory = player.getInventory();

            if ("grapeInHand".equalsIgnoreCase(from)) {
                String material = item.get("material").getAsString();
                ItemStack itemStack = new ItemStack(Material.valueOf(material));
                if ("POTION".equalsIgnoreCase(material)) {
                    itemStack.editMeta(PotionMeta.class, meta -> meta.setBasePotionData(new PotionData(PotionType.valueOf(item.get("potion-type").getAsString()))));
                }
                inventory.addItem(itemStack);
            } else if ("ItemsAdder".equalsIgnoreCase(from)) {
                CustomStack instance = CustomStack.getInstance(item.get("namespace").getAsString() + ":" + item.get("id").getAsString());
                inventory.addItem(instance.getItemStack());
            } else if ("MMOItems".equalsIgnoreCase(from)) {
                MMOItem mmoItem = MMOItems.plugin.getMMOItem(MMOItems.plugin.getTypes().get(item.get("type").getAsString()), item.get("id").getAsString(), item.get("scaled").getAsInt(), MMOItems.plugin.getTiers().get(item.get("tier").getAsString()));
                ItemStack build = mmoItem.newBuilder().build();
                inventory.addItem(build);
            }
        });
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

    @SubCommand("removeNBTFile")
    @SneakyThrows
    public void removeFile(CommandSender sender) {
        File file = new File(((Player) sender).getWorld().getName(), "brew.nbt");
        file.delete();
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
        new Thread() {
            Runnable runnable = () -> {
                Bukkit.getServer().getPluginManager().callEvent(new BrewDataProcessEvent(player.getWorld(), player.getWorld().getWorldFolder()));
            };
        }.start();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7增加了 &61 &7酿造周期"));
    }

    @SubCommand("testConfiguration")
    @SneakyThrows
    public void testConfiguration(CommandSender commandSender) {
        Set<String> shallowKeys = ConfigurationUtils.getKeys("recipes");
        FileConfiguration fileConfiguration = ConfigurationUtils.get("recipes");
        if (shallowKeys == null) return;

        Gson gson = new Gson();

        shallowKeys.forEach(k -> {
            ConfigurationSection section = fileConfiguration.getConfigurationSection(k);
            if (section == null) return;
            ConfigurationSection yieldSection = section.getConfigurationSection("yield");
            if (yieldSection == null) return;

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("substrate", section.getString("substrate"));
            jsonObject.addProperty("restriction", gson.toJson(section.getStringList("restriction"), List.class));
            jsonObject.addProperty("yeast", gson.toJson(section.getStringList("yeast"), List.class));
            jsonObject.addProperty("maxYield", yieldSection.getInt("max-yield"));
            jsonObject.addProperty("minYield", yieldSection.getInt("min-yield"));
            jsonObject.addProperty("restrictionIndex", yieldSection.getDouble("restriction-index"));
            jsonObject.addProperty("yeastIndex", yieldSection.getDouble("yeast-index"));
            jsonObject.addProperty("brewingCycle", section.getInt("brewing-cycle"));
            jsonObject.addProperty("output", section.getString("output"));

            configurationMap.put(k, jsonObject);
        });

        shallowKeys.forEach(v -> {
            JsonObject object = (JsonObject) configurationMap.get(v);
            System.out.println(object.getAsJsonObject());
        });
    }

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