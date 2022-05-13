package homeward.plugin.brewing.commands;

import com.google.gson.*;
import dev.lone.itemsadder.api.CustomStack;
import homeward.plugin.brewing.Brewing;
import homeward.plugin.brewing.beans.CustomItemStack;
import homeward.plugin.brewing.beans.RecipesItem;
import homeward.plugin.brewing.constants.RecipesConfiguration;
import homeward.plugin.brewing.enumerates.EnumBase;
import homeward.plugin.brewing.enumerates.StringEnum;
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

import java.util.*;

import static homeward.plugin.brewing.constants.RecipesConfiguration.*;
import static homeward.plugin.brewing.enumerates.StringEnum.VANILLA;
import static homeward.plugin.brewing.enumerates.StringEnum.ITEMSADDER;
import static homeward.plugin.brewing.enumerates.StringEnum.MMOITEMS;

@Command("mock")
@Alias("m")
public class MockFileConfigurationTest extends CommandBase {
    private final MMOItems mmoItemsPlugin = MMOItems.plugin;
    private final FileConfiguration recipesFileConfiguration;
    private final Set<EnumBase> materialTypeSet;

    private RecipesItem currentRecipesItem;
    private String currentSectionName;
    private String currentItemSectionName;
    private String currentMaterialArrayIndex;
    private ConfigurationSection currentConfigurationSection;
    private boolean material$set, output$set = false;

    private static final Set<RecipesItem> recipesItemSet = new LinkedHashSet<>();

    public MockFileConfigurationTest() {
        this.recipesFileConfiguration = ConfigurationUtils.get("recipes");
        this.materialTypeSet = new LinkedHashSet<>(Arrays.asList(StringEnum.SUBSTRATE, StringEnum.RESTRICTION, StringEnum.YEAST));
    }

    @Default
    public void defaultCommand(CommandSender commandSender) {
        Set<String> recipeKeys = recipesFileConfiguration.getKeys(false);
        recipeKeys.forEach(this::keysAction);
    }

    private void keysAction(final String key) {
        material$set = output$set = false;
        currentConfigurationSection = recipesFileConfiguration.getConfigurationSection(key);
        if (currentConfigurationSection == null) {
            warning(currentSectionName, key);
            return;
        }

        currentRecipesItem = new RecipesItem();
        currentSectionName = key;

        materialTypeSet.forEach(this::initializeBrewingMaterial); // 初始化酿酒的material

        // todo output
        initializeBrewingOutput();

        // todo yield
        // todo index
        // todo brewing-cycle
        if (!material$set) recipesItemSet.add(currentRecipesItem);
    }

    private void initializeBrewingOutput() {
        currentItemSectionName = OUTPUT;
        ConfigurationSection outputSection = currentConfigurationSection.getConfigurationSection(OUTPUT);
        if (outputSection == null) {
            warning(currentSectionName, OUTPUT);
            return;
        }

        String provider = outputSection.getString(PROVIDER);
        if (provider == null) {
            warning(StringEnum.OUTPUT, PROVIDER);
            return;
        }

        ConfigurationSection itemSection = outputSection.getConfigurationSection(ITEM);
        if (itemSection == null) {
            warning(StringEnum.OUTPUT, ITEM);
            return;
        }

        JsonObject item = new JsonObject();
        item.addProperty(QUANTITY, itemSection.getString("quantity"));

        CustomItemStack itemStack = null;
        if (RecipesConfiguration.VANILLA.equalsIgnoreCase(provider)) {
            item.addProperty(MATERIAL, itemSection.getString("material"));
            item.addProperty(POTION_TYPE, itemSection.getString("potion-type"));
            itemStack = initVanillaItem(item);
        } else if (RecipesConfiguration.ITEMSADDER.equalsIgnoreCase(provider)) {
            item.addProperty(NAMESPACE, itemSection.getString("namespace"));
            item.addProperty(ID, itemSection.getString("id"));
            itemStack = initItemsAdderItem(item);
        } else if (RecipesConfiguration.MMOITEMS.equalsIgnoreCase(provider)) {
            item.addProperty(TYPE, itemSection.getString("type"));
            item.addProperty(ID, itemSection.getString("id"));
            item.addProperty(INDEX, itemSection.getString("index"));
            item.addProperty(SCALED, itemSection.getString("scaled"));
            item.addProperty(TIER, itemSection.getString("tier"));
            itemStack = initMMOItemItem(item);
        }

        if (itemStack == null) return;
        currentRecipesItem.output(itemStack);
        System.out.println(itemStack.itemStack() + ";" + itemStack.index() + ";" + itemStack.quantity());
        output$set = true;
    }

    // region 初始化(substrate|restriction|yeast)section
    private void initializeBrewingMaterial(final EnumBase materialType) {
        currentItemSectionName = materialType.getString();
        String materialStringSection = currentConfigurationSection.getString(materialType.getString()); // NPE
        if (materialStringSection == null) {
            warning(currentSectionName, materialType.getString());
            return;
        }

        currentMaterialArrayIndex = materialType.getString();

        JsonArray materialArray = new Gson().fromJson(materialStringSection.replaceAll("=", ":"), JsonArray.class);
        materialArray.forEach(this::materialArrayAction);
    }
    // endregion

    // region 将(substrate|restriction|yeast)赋值给bean对象
    private void materialArrayAction(final JsonElement jsonElement) {
        JsonObject section = (JsonObject) jsonElement;
        JsonElement itemProvider = section.get(PROVIDER);
        if (itemProvider == null || itemProvider instanceof JsonNull) {
            warning(currentMaterialArrayIndex, PROVIDER);
            return;
        }
        String provider = itemProvider.getAsString();
        JsonElement itemItem = section.get(ITEM);
        if (itemItem == null || itemItem instanceof JsonNull) {
            warning(currentMaterialArrayIndex, ITEM);
            return;
        }
        JsonObject item = itemItem.getAsJsonObject();

        CustomItemStack itemStack = null;
        if (RecipesConfiguration.VANILLA.equalsIgnoreCase(provider)) {
            itemStack = initVanillaItem(item);
        } else if (RecipesConfiguration.ITEMSADDER.equalsIgnoreCase(provider)) {
            itemStack = initItemsAdderItem(item);
        } else if (RecipesConfiguration.MMOITEMS.equalsIgnoreCase(provider)) {
            itemStack = initMMOItemItem(item);
        }

        if (itemStack == null) return;

        switch (currentItemSectionName) {
            case SUBSTRATE -> currentRecipesItem.substrate(itemStack);
            case RESTRICTION -> currentRecipesItem.restriction(itemStack);
            case YEAST -> currentRecipesItem.yeast(itemStack);
        }

        material$set = true;
    }

    // region 初始化原版物品
    private CustomItemStack initVanillaItem(final JsonObject item) {
        JsonElement itemMaterial = item.get(MATERIAL);
        if (itemMaterial == null || itemMaterial instanceof JsonNull) { // material is null?
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
            JsonElement itemPotionType = item.get(POTION_TYPE);
            if (itemPotionType == null || itemPotionType instanceof JsonNull) { // potion type is null?
                warning(VANILLA, POTION_TYPE);
                return null;
            }

            PotionType potionType;
            try {
                potionType = PotionType.valueOf(itemPotionType.getAsString());
            } catch (IllegalArgumentException e) { // potion type is correct?
                warning(VANILLA, POTION_TYPE, itemPotionType.getAsString());
                return null;
            }

            itemStack.editMeta(PotionMeta.class, meta -> meta.setBasePotionData( new PotionData(potionType)));
        }

        JsonElement quantity = item.get("quantity");
        JsonElement index = item.get("index");

        return CustomItemStack.builder().itemStack(itemStack).quantity(quantity == null || quantity instanceof JsonNull ? 1 : quantity.getAsInt()).index(index == null || index instanceof JsonNull ? 100 : index.getAsInt()).build();
    }
    // endregion

    // region 初始化ItemsAdder物品
    private CustomItemStack initItemsAdderItem(final JsonObject item) {
        JsonElement itemNamespace = item.get(NAMESPACE);
        if (itemNamespace == null || itemNamespace instanceof JsonNull) { // namespace is null?
            warning(ITEMSADDER, NAMESPACE);
            return null;
        }
        String namespace = item.get("namespace").getAsString();
        if (namespace.isBlank()) { // namespace is blank?
            warning(ITEMSADDER, NAMESPACE, namespace);
            return null;
        }

        JsonElement itemId = item.get("id");
        if (itemId == null || itemId instanceof JsonNull) { // id is null?
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

        ItemStack itemStack = ItemStack.deserializeBytes(itemInstance.getItemStack().serializeAsBytes());// Prevent IA internal bug
        JsonElement quantity = item.get("quantity");
        JsonElement index = item.get("index");
        return CustomItemStack.builder().itemStack(itemStack).quantity(quantity == null || quantity instanceof JsonNull ? 1 : quantity.getAsInt()).index(index == null || index instanceof JsonNull ? 100 : index.getAsInt()).build();
    }
    // endregion

    // region 初始化MMOItem物品
    private CustomItemStack initMMOItemItem(final JsonObject item) {
        JsonElement itemType = item.get(TYPE);
        if (itemType == null || itemType instanceof JsonNull) { // type is null?
            warning(MMOITEMS, TYPE);
            return null;
        }
        Type type = mmoItemsPlugin.getTypes().get(itemType.getAsString());
        if (type == null) { // type is correct?
            warning(MMOITEMS, TYPE, itemType.getAsString());
            return null;
        }

        JsonElement itemId = item.get(ID);
        if (itemId == null || itemId instanceof JsonNull){ // id is null?
            warning(MMOITEMS, ID);
            return null;
        }
        String id = itemId.getAsString();

        if (mmoItemsPlugin.getTemplates().getTemplate(type, id) == null) { // id is correct?
            warning(MMOITEMS, ID, id);
            return null;
        }

        MMOItem mmoItem;

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

            mmoItem = mmoItemsPlugin.getMMOItem(type, id, level, tier);

        } else if (itemLevel != null) { // itemTier == null
            int level = itemLevel.getAsInt();
            if (level < 1) { // level is correct?
                warning(MMOITEMS, SCALED, level);
                return null;
            }

            mmoItem = mmoItemsPlugin.getMMOItem(type, id, level, null);

        } else if (itemTier != null) { // itemLevel == null
            ItemTier tier = mmoItemsPlugin.getTiers().get(itemTier.getAsString());
            if (tier == null) { // tier is correct?
                warning(MMOITEMS, TIER, itemTier.getAsString());
                return null;
            }

            mmoItem = mmoItemsPlugin.getMMOItem(type, id, 0, tier);

        } else {
            mmoItem = mmoItemsPlugin.getMMOItem(type, id, 0, null);
        }


        JsonElement quantity = item.get("quantity");
        JsonElement index = item.get("index");

        return mmoItem != null ? CustomItemStack.builder().itemStack(mmoItem.newBuilder().build()).quantity((quantity == null || quantity instanceof JsonNull) ? 1 : quantity.getAsInt()).index((index == null || index instanceof JsonNull) ? 100 : index.getAsInt()).build() : null;
    }
    // endregion
    // endregion

    // region utils
    private void warning(String provider, String key) {
        String message = String.format("Cannot read property in %s. The problem arises in KEY:%s, VALUE:%s from SECTION:%s, is it empty?", NAME, provider, key, currentSectionName);
        Brewing.getInstance().getSLF4JLogger().warn(message);
    }

    private void warning(EnumBase provider, String key) {
        String message = String.format("Cannot read %s item in %s. The problem arises in KEY:%s from SECTION:%s, is it empty?", provider.getString(), NAME, key, currentSectionName);
        Brewing.getInstance().getSLF4JLogger().warn(message);
    }

    private void warning(EnumBase provider, String key, Object value) {
        String message = String.format("Cannot read %s item in %s. The problem arises in KEY:%s, VALUE:%s from SECTION:%s.", provider.getString(), NAME, key, value, currentSectionName);
        Brewing.getInstance().getSLF4JLogger().warn(message);
    }
    // endregion
}