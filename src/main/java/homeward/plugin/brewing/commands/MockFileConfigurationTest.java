package homeward.plugin.brewing.commands;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lone.itemsadder.api.CustomStack;
import homeward.plugin.brewing.Brewing;
import homeward.plugin.brewing.beans.RecipesItem;
import homeward.plugin.brewing.enumerates.EnumBase;
import homeward.plugin.brewing.utils.ConfigurationUtils;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.base.CommandBase;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.LinkedHashSet;
import java.util.Set;

import static homeward.plugin.brewing.constants.RecipesConfiguration.*;
import static homeward.plugin.brewing.enumerates.StringEnum.*;

@Command("mock")
@Alias("m")
public class MockFileConfigurationTest extends CommandBase {
    private final MMOItems mmoItemsPlugin = MMOItems.plugin;
    private final FileConfiguration recipesFileConfiguration;

    private RecipesItem currentRecipesItem;
    private static String currentSectionName = "";

    private static final Set<RecipesItem> recipesItemSet = new LinkedHashSet<>();

    public MockFileConfigurationTest() {
        recipesFileConfiguration = ConfigurationUtils.get("recipes");

    }

    @Default
    public void defaultCommand(CommandSender commandSender) {
        Set<String> recipeKeys = recipesFileConfiguration.getKeys(false);
        recipeKeys.forEach(this::keysAction);
    }

    private void keysAction(final String key) {
        ConfigurationSection objectSection = recipesFileConfiguration.getConfigurationSection(key);
        if (objectSection == null) return;

        currentRecipesItem = new RecipesItem();
        currentSectionName = key;

        // 为substrate赋值
        String substrateStringSection = objectSection.getString("substrate"); // NPE
        if (substrateStringSection == null) return;
        JsonArray substratesArray = new Gson().fromJson(substrateStringSection.replaceAll("=", ":"), JsonArray.class);
        substratesArray.forEach(this::initSubstrateSection);

        // todo restriction
        // todo yeast



        recipesItemSet.add(currentRecipesItem);
    }

    // region 将substrate赋值给bean对象
    private void initSubstrateSection(final JsonElement jsonElement) {
        JsonObject substrateSection = (JsonObject) jsonElement;
        String provider = substrateSection.get("provider").getAsString();
        JsonObject item = substrateSection.get("item").getAsJsonObject();

        ItemStack itemStack = null;
        if ("Vanilla".equalsIgnoreCase(provider)) {
            itemStack = initVanillaItem(item);
        } else if ("ItemsAdder".equalsIgnoreCase(provider)) {
            itemStack = initItemsAdderItem(item);
        } else if ("MMOItems".equalsIgnoreCase(provider)) {
            itemStack = initMMOItemItem(item);
        }

        if (itemStack != null) {
            currentRecipesItem.substrate(itemStack);
        }
    }

    // region 初始化MMOItem物品
    private ItemStack initMMOItemItem(final JsonObject item) {
        JsonElement itemType = item.get(TYPE);
        if (itemType == null) { // type is null?
            warning(MMOITEMS, TYPE);
            return null;
        }
        Type type = mmoItemsPlugin.getTypes().get(itemType.getAsString());
        if (type == null) { // type is correct?
            warning(MMOITEMS, TYPE, itemType.getAsString());
            return null;
        }

        JsonElement itemId = item.get(ID);
        if (itemId == null){ // id is null?
            warning(MMOITEMS, ID);
            return null;
        }
        String id = itemId.getAsString();

        if (mmoItemsPlugin.getTemplates().getTemplate(type, id) == null) { // id is correct?
            warning(MMOITEMS, ID, id);
            return null;
        }

        // region 支持可选
        JsonElement itemLevel = item.get(SCALED);
        JsonElement itemTier = item.get(TIER);
        if (itemLevel != null && itemTier != null) {
            int level = itemLevel.getAsInt();
            if (level < 1) { // level is correct?
                warning(MMOITEMS, SCALED, level);
                return null;
            }

            ItemTier tier = mmoItemsPlugin.getTiers().get(itemTier.getAsString());
            if (tier == null) { // tier is correct?
                warning(MMOITEMS, TIER, itemTier.getAsString());
                return null;
            }

            MMOItem mmoItem = mmoItemsPlugin.getMMOItem(type, id, level, tier);
            return mmoItem == null ? null : mmoItem.newBuilder().build();

        } else if (itemLevel != null) { // itemTier == null
            int level = itemLevel.getAsInt();
            if (level < 1) { // level is correct?
                warning(MMOITEMS, SCALED, level);
                return null;
            }

            MMOItem mmoItem = mmoItemsPlugin.getMMOItem(type, id, level, null);
            return mmoItem == null ? null : mmoItem.newBuilder().build();

        } else if (itemTier != null) { // itemLevel == null
            ItemTier tier = mmoItemsPlugin.getTiers().get(itemTier.getAsString());
            if (tier == null) { // tier is correct?
                warning(MMOITEMS, TIER, itemTier.getAsString());
                return null;
            }

            MMOItem mmoItem = mmoItemsPlugin.getMMOItem(type, id, 0, tier);
            return mmoItem == null ? null : mmoItem.newBuilder().build();
        }
        // endregion

        MMOItem mmoItem = mmoItemsPlugin.getMMOItem(type, id, 0, null);
        return mmoItem == null ? null : mmoItem.newBuilder().build();
    }
    // endregion


    // region 初始化ItemsAdder物品
    private ItemStack initItemsAdderItem(final JsonObject item) {
        JsonElement itemNamespace = item.get(NAMESPACE);
        if (itemNamespace == null) { // namespace is null?
            warning(ITEMSADDER, NAMESPACE);
            return null;
        }
        String namespace = item.get("namespace").getAsString();
        if (namespace.isBlank()) { // namespace is blank?
            warning(ITEMSADDER, NAMESPACE, namespace);
            return null;
        }

        JsonElement itemId = item.get("id");
        if (itemId == null) { // id is null?
            warning(ITEMSADDER, ID);
            return null;
        }
        String id = item.get("id").getAsString();
        if (id.isBlank()) { // id is null?
            warning(ITEMSADDER, ID, id);
            return null;
        }

        String namespacedId = namespace + ':' + id;

        CustomStack itemInstance = CustomStack.getInstance(namespacedId); // NPE
        if (itemInstance == null) { // namespacedId is correct?
            warning(ITEMSADDER, NAMESPACED_ID, namespace + ":" + id);
            return null;
        }

        return ItemStack.deserializeBytes(itemInstance.getItemStack().serializeAsBytes()); // Prevent IA internal bug
    }
    // endregion

    // region 初始化原版物品
    private ItemStack initVanillaItem(final JsonObject item) {
        JsonElement itemMaterial = item.get(MATERIAL);
        if (itemMaterial == null) { // material is null?
            warning(VANILLA, MATERIAL);
            return null;
        }

        Material material;
        try {
            material = Material.valueOf(itemMaterial.getAsString());
        } catch (IllegalArgumentException e) { // material is correct?
            warning(VANILLA, MATERIAL, itemMaterial.getAsString());
            return null;
        }

        ItemStack itemStack = new ItemStack(material);

        // 给予potion种类
        if (Material.POTION.equals(material)) {
            JsonElement ItemPotionType = item.get(POTION_TYPE);
            if (ItemPotionType == null) { // potion type is null?
                warning(VANILLA, POTION_TYPE);
                return null;
            }

            PotionType potionType;
            try {
                potionType = PotionType.valueOf(ItemPotionType.getAsString());
            } catch (IllegalArgumentException e) { // potion type is correct?
                warning(VANILLA, POTION_TYPE, ItemPotionType.getAsString());
                return null;
            }

            itemStack.editMeta(PotionMeta.class, meta -> meta.setBasePotionData( new PotionData(potionType)));
        }

        return itemStack;
    }
    // endregion
    // endregion

    // region utils
    @Deprecated
    private void warning(String key) {
        Brewing.getInstance().getSLF4JLogger().warn("cannot read key: " + key + " from " + currentSectionName + " section in " + NAME + " configuration file, is it empty?");
    }

    private void warning(EnumBase provider, String key) {
        String message = String.format("Cannot read %s item in %s\nThe problem arises in KEY:%s from SECTION:%s, is it empty?", provider.getString(), NAME, key, currentSectionName);
        Brewing.getInstance().getSLF4JLogger().warn(message);
    }

    @Deprecated
    private void warning(String key, Object value) {
        Brewing.getInstance().getSLF4JLogger().warn("cannot read key: " + key + ", value: " + value + " from " + currentSectionName + " section in " + NAME + " configuration file");
    }

    private void warning(EnumBase provider, String key, Object value) {
        String message = String.format("Cannot read %s item in %s\nThe problem arises in KEY:%s, VALUE:%s from SECTION:%s.", provider.getString(), NAME, key, value, currentSectionName);
        Brewing.getInstance().getSLF4JLogger().warn(message);
    }
    // endregion
}