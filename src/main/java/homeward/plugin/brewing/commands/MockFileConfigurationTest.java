package homeward.plugin.brewing.commands;

import com.google.gson.*;
import dev.lone.itemsadder.api.CustomStack;
import homeward.plugin.brewing.Brewing;
import homeward.plugin.brewing.beans.CustomItemStack;
import homeward.plugin.brewing.beans.RecipesItem;
import homeward.plugin.brewing.beans.RecipesItem.RecipesItemBuilder;
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
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import static homeward.plugin.brewing.constants.RecipesConfiguration.*;

@Command("mock")
@Alias("m")
public class MockFileConfigurationTest extends CommandBase {
    private final RoundingMode roundingMode = RoundingMode.HALF_UP;
    private final String roundingPattern = "#.##";
    private final DecimalFormat decimalFormat = new DecimalFormat(roundingPattern);

    private final MMOItems mmoItemsPlugin = MMOItems.plugin;
    private final FileConfiguration recipesFileConfiguration;
    private final Set<EnumBase> materialTypeSet;

    private RecipesItemBuilder recipesBuilder;
    private String sectionName, keyName;
    private ConfigurationSection configurationSection;
    private boolean display$set, material$set, output$set, yield$set, index$set, cycle$set = false;

    private static final Map<String, RecipesItem> recipesItemSet = new LinkedHashMap<>();

    public MockFileConfigurationTest() {
        this.recipesFileConfiguration = ConfigurationUtils.get("recipes");
        this.materialTypeSet = new LinkedHashSet<>(Arrays.asList(StringEnum.SUBSTRATE, StringEnum.RESTRICTION, StringEnum.YEAST));
        decimalFormat.setRoundingMode(roundingMode);
    }

    @Default
    public void defaultCommand(CommandSender commandSender) {
        Set<String> recipeKeys = recipesFileConfiguration.getKeys(false);
        recipeKeys.forEach(this::keysAction);
    }

    private void keysAction(final String key) {
        display$set = material$set = output$set = yield$set = index$set = cycle$set = false;
        configurationSection = recipesFileConfiguration.getConfigurationSection(key);
        if (configurationSection == null) {
            warning(sectionName, key);
            return;
        }
        recipesBuilder = RecipesItem.builder();
        sectionName = key;

        setBrewingRecipeDisplayName(); // Set Display Name

        materialTypeSet.forEach(this::setBrewingMaterialItemStack); // Set (Substrate|Restriction|Yeast) Name

        setBrewingOutputItemStack(); // Set Output ItemStack

        setBrewingYield(); // Set Yield

        setBrewingIndex(); // Set Index

        setBrewingCycle(); // Set Brewing Cycle

        if (display$set && material$set && output$set && yield$set && index$set && cycle$set) {
            recipesItemSet.put(key, recipesBuilder.build());
            done(key);
        } else {
            fail(key);
        }
    }

    // region Set "cycle" into bean object
    private void setBrewingCycle() {
        keyName = BREWING_CYCLE;
        String cycle = configurationSection.getString(BREWING_CYCLE);
        if (cycle == null) {
            warning(sectionName, BREWING_CYCLE);
            return;
        }
        if (!isNumeric(cycle)) {
            warning(StringEnum.CYCLE, BREWING_CYCLE, cycle);
        }

        recipesBuilder.brewingCycle(Integer.parseInt(cycle));
        cycle$set = true;
    }
    // endregion

    // region Set "index" into bean object
    private void setBrewingIndex() {
        keyName = INDEX;
        ConfigurationSection indexSection = configurationSection.getConfigurationSection(INDEX);
        if (indexSection == null) {
            warning(sectionName, INDEX);
            return;
        }

        String restrictionIndexString = indexSection.getString(RESTRICTION_INDEX);
        if (restrictionIndexString == null) {
            warning(INDEX, RESTRICTION_INDEX);
            return;
        }
        if (!isNumeric(restrictionIndexString)) {
            warning(StringEnum.RESTRICTION, RESTRICTION_INDEX, restrictionIndexString);
            return;
        }

        String yeastIndexString = indexSection.getString(YEAST_INDEX);
        if (yeastIndexString == null) {
            warning(INDEX, YEAST_INDEX);
            return;
        }
        if (!isNumeric(yeastIndexString)) {
            warning(StringEnum.INDEX, YEAST_INDEX, yeastIndexString);
            return;
        }

        String yieldIndexString = indexSection.getString(YIELD_INDEX);
        if (yieldIndexString == null) {
            warning(INDEX, YIELD_INDEX);
            return;
        }
        if (!isNumeric(yieldIndexString)) {
            warning(StringEnum.YIELD, YIELD_INDEX, yieldIndexString);
            return;
        }

        System.out.println(restrictionIndexString);
        double restrictionIndex = Double.parseDouble(decimalFormat.format(Double.valueOf(restrictionIndexString)));
        double yeastIndex = Double.parseDouble(decimalFormat.format(Double.valueOf(yeastIndexString)));
        double yieldIndex = Double.parseDouble(decimalFormat.format(Double.valueOf(yieldIndexString)));

        recipesBuilder.globalRestrictionsIndex(restrictionIndex).globalYeastsIndex(yeastIndex).globalYieldIndex(yieldIndex);
        index$set = true;
    }
    // endregion

    // region Set "yield" into bean object
    private void setBrewingYield() {
        keyName = YIELD;
        ConfigurationSection yieldSection = configurationSection.getConfigurationSection(YIELD);
        if (yieldSection == null) {
            warning(sectionName, YIELD);
            return;
        }

        String maxString = yieldSection.getString(MAX);
        if (maxString == null) {
            warning(YIELD, MAX);
            return;
        }
        if (!isNumeric(maxString) || notInteger(maxString) || Integer.parseInt(maxString) < 1) {
            warning(StringEnum.YIELD, MAX, maxString);
            return;
        }
        int max = Integer.parseInt(maxString);

        String minString = yieldSection.getString("min");
        if (minString != null && (!isNumeric(maxString) || notInteger(minString) || Integer.parseInt(minString) < 1 || Integer.parseInt(minString) > max)) {
            warning(StringEnum.YIELD, MIN, minString);
            return;
        }
        int min = minString == null ? max : Integer.parseInt(minString);

        recipesBuilder.maxYield(max);
        recipesBuilder.minYield(min);
        yield$set = true;
    }
    // endregion

    // region Set "display-name" into bean object
    private void setBrewingRecipeDisplayName() {
        keyName = DISPLAY_NAME;
        String displayName = configurationSection.getString(DISPLAY_NAME);
        if (displayName == null || displayName.isBlank()) {
            warning(sectionName, DISPLAY_NAME);
            return;
        }

        recipesBuilder.displayName(displayName);
        display$set = true;
    }
    // endregion

    // region Set "output" section ItemStack into bean object
    private void setBrewingOutputItemStack() {
        keyName = OUTPUT;
        ConfigurationSection outputSection = configurationSection.getConfigurationSection(OUTPUT);
        if (outputSection == null) {
            warning(sectionName, OUTPUT);
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
        item.addProperty(QUANTITY, itemSection.getString(QUANTITY));

        CustomItemStack itemStack = null;
        if (RecipesConfiguration.VANILLA.equalsIgnoreCase(provider)) {
            item.addProperty(MATERIAL, itemSection.getString(MATERIAL));
            item.addProperty(POTION_TYPE, itemSection.getString(POTION_TYPE));
            itemStack = createVanillaItemStack(item);
        } else if (RecipesConfiguration.ITEMSADDER.equalsIgnoreCase(provider)) {
            item.addProperty(NAMESPACE, itemSection.getString(NAMESPACE));
            item.addProperty(ID, itemSection.getString(ID));
            itemStack = createItemsAdderItemStack(item);
        } else if (RecipesConfiguration.MMOITEMS.equalsIgnoreCase(provider)) {
            item.addProperty(TYPE, itemSection.getString(TYPE));
            item.addProperty(ID, itemSection.getString(ID));
            item.addProperty(INDEX, itemSection.getString(INDEX));
            item.addProperty(SCALED, itemSection.getString(SCALED));
            item.addProperty(TIER, itemSection.getString(TIER));
            itemStack = createMMOItemItemStack(item);
        }

        if (itemStack == null) return;

        recipesBuilder.output(itemStack);
        output$set = true;
    }
    // endregion

    // region Set "(substrate|restriction|yeast)" section ItemStack into bean object
    private void setBrewingMaterialItemStack(final EnumBase materialType) {
        keyName = materialType.getString();
        String materialStringSection = configurationSection.getString(materialType.getString()); // NPE
        if (materialStringSection == null) {
            warning(sectionName, materialType.getString());
            return;
        }

        JsonArray materialArray = new Gson().fromJson(materialStringSection.replaceAll("=", ":"), JsonArray.class);
        materialArray.forEach(this::materialArrayAction);
    }

    private void materialArrayAction(final JsonElement jsonElement) {
        JsonObject section = (JsonObject) jsonElement;
        JsonElement itemProvider = section.get(PROVIDER);
        if (itemProvider == null || itemProvider instanceof JsonNull) {
            warning(keyName, PROVIDER);
            return;
        }
        String provider = itemProvider.getAsString();
        JsonElement itemItem = section.get(ITEM);
        if (itemItem == null || itemItem instanceof JsonNull) {
            warning(keyName, ITEM);
            return;
        }
        JsonObject item = itemItem.getAsJsonObject();

        CustomItemStack itemStack = null;
        if (RecipesConfiguration.VANILLA.equalsIgnoreCase(provider)) {
            itemStack = createVanillaItemStack(item);
        } else if (RecipesConfiguration.ITEMSADDER.equalsIgnoreCase(provider)) {
            itemStack = createItemsAdderItemStack(item);
        } else if (RecipesConfiguration.MMOITEMS.equalsIgnoreCase(provider)) {
            itemStack = createMMOItemItemStack(item);
        }

        if (itemStack == null) return;

        switch (keyName) {
            case SUBSTRATE -> recipesBuilder.substrate(itemStack);
            case RESTRICTION -> recipesBuilder.restriction(itemStack);
            case YEAST -> recipesBuilder.yeast(itemStack);
        }

        material$set = true;
    }
    // endregion

    // region Create Vanilla ItemStack
    private CustomItemStack createVanillaItemStack(final JsonObject item) {
        JsonElement itemQuantity = item.get(QUANTITY);
        boolean hasQuantity = itemQuantity != null && !(itemQuantity instanceof JsonNull);
        if (hasQuantity && !isNumeric(itemQuantity.getAsString())) {
            warning(StringEnum.VANILLA, QUANTITY, itemQuantity.getAsString());
            return null;
        }

        JsonElement itemIndex = item.get(INDEX);
        boolean hasIndex = itemIndex != null && !(itemIndex instanceof JsonNull);
        if (hasIndex && !isNumeric(itemIndex.getAsString())) {
            warning(StringEnum.VANILLA, INDEX, itemIndex.getAsString());
            return null;
        }

        JsonElement itemMaterial = item.get(MATERIAL);
        if (itemMaterial == null || itemMaterial instanceof JsonNull) { // material is null?
            warning(VANILLA, MATERIAL);
            return null;
        }

        Material material;
        try {
            material = Material.valueOf(itemMaterial.getAsString());
        } catch (IllegalArgumentException e) { // material is correct?
            warning(StringEnum.VANILLA, MATERIAL, itemMaterial.getAsString());
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
                warning(StringEnum.VANILLA, POTION_TYPE, itemPotionType.getAsString());
                return null;
            }

            itemStack.editMeta(PotionMeta.class, meta -> meta.setBasePotionData( new PotionData(potionType)));
        }

        return CustomItemStack.builder().itemStack(itemStack).quantity(hasQuantity ? itemQuantity.getAsInt() : 1).index(hasIndex ? Double.parseDouble(decimalFormat.format(itemIndex.getAsDouble())) : 1D).build();
    }
    // endregion

    // region Create ItemsAdder ItemStack
    private CustomItemStack createItemsAdderItemStack(final JsonObject item) {
        JsonElement itemQuantity = item.get(QUANTITY);
        boolean hasQuantity = itemQuantity != null && !(itemQuantity instanceof JsonNull);
        if (hasQuantity && !isNumeric(itemQuantity.getAsString())) {
            warning(StringEnum.ITEMSADDER, QUANTITY, itemQuantity.getAsString());
            return null;
        }

        JsonElement itemIndex = item.get(INDEX);
        boolean hasIndex = itemIndex != null && !(itemIndex instanceof JsonNull);
        if (hasIndex && !isNumeric(itemIndex.getAsString())) {
            warning(StringEnum.ITEMSADDER, INDEX, itemIndex.getAsString());
            return null;
        }

        JsonElement itemNamespace = item.get(NAMESPACE);
        if (itemNamespace == null || itemNamespace instanceof JsonNull) { // namespace is null?
            warning(ITEMSADDER, NAMESPACE);
            return null;
        }
        String namespace = item.get("namespace").getAsString();
        if (namespace.isBlank()) { // namespace is blank?
            warning(StringEnum.ITEMSADDER, NAMESPACE, namespace);
            return null;
        }

        JsonElement itemId = item.get("id");
        if (itemId == null || itemId instanceof JsonNull) { // id is null?
            warning(ITEMSADDER, ID);
            return null;
        }
        String id = item.get("id").getAsString();
        if (id.isBlank()) { // id is null?
            warning(StringEnum.ITEMSADDER, ID, id);
            return null;
        }

        String namespacedId = namespace + ':' + id;

        CustomStack itemInstance = CustomStack.getInstance(namespacedId); // NPE
        if (itemInstance == null) { // namespacedId is correct?
            warning(StringEnum.ITEMSADDER, NAMESPACED_ID, namespace + ":" + id);
            return null;
        }

        ItemStack itemStack = ItemStack.deserializeBytes(itemInstance.getItemStack().serializeAsBytes());// Prevent IA internal bug

        return CustomItemStack.builder().itemStack(itemStack).quantity(hasQuantity ? itemQuantity.getAsInt() : 1).index(hasIndex ? Double.parseDouble(decimalFormat.format(itemIndex.getAsDouble())) : 1D).build();
    }
    // endregion

    // region Create MMOItem ItemStack
    private CustomItemStack createMMOItemItemStack(final JsonObject item) {
        JsonElement itemQuantity = item.get(QUANTITY);
        boolean hasQuantity = itemQuantity != null && !(itemQuantity instanceof JsonNull);

        if (hasQuantity && !isNumeric(itemQuantity.getAsString())) {
            warning(StringEnum.MMOITEMS, QUANTITY, itemQuantity.getAsString());
            return null;
        }

        JsonElement itemIndex = item.get(INDEX);
        boolean hasIndex = itemIndex != null && !(itemIndex instanceof JsonNull);
        if (hasIndex && !isNumeric(itemIndex.getAsString())) {
            warning(StringEnum.MMOITEMS, INDEX, itemIndex.getAsString());
            return null;
        }

        JsonElement itemType = item.get(TYPE);
        if (itemType == null || itemType instanceof JsonNull) { // type is null?
            warning(MMOITEMS, TYPE);
            return null;
        }
        Type type = mmoItemsPlugin.getTypes().get(itemType.getAsString());
        if (type == null) { // type is correct?
            warning(StringEnum.MMOITEMS, TYPE, itemType.getAsString());
            return null;
        }

        JsonElement itemId = item.get(ID);
        if (itemId == null || itemId instanceof JsonNull){ // id is null?
            warning(StringEnum.MMOITEMS, ID);
            return null;
        }
        String id = itemId.getAsString();

        if (mmoItemsPlugin.getTemplates().getTemplate(type, id) == null) { // id is correct?
            warning(StringEnum.MMOITEMS, ID, id);
            return null;
        }

        MMOItem mmoItem;

        JsonElement itemLevel = item.get(SCALED);
        JsonElement itemTier = item.get(TIER);

        if (!((itemLevel == null || itemLevel instanceof JsonNull) || (itemTier == null || itemTier instanceof JsonNull))) {
            int level = itemLevel.getAsInt();
            if (level < 1) { // level is correct?
                warning(StringEnum.MMOITEMS, SCALED, level);
                return null;
            }

            ItemTier tier = mmoItemsPlugin.getTiers().get(itemTier.getAsString());
            if (tier == null) { // tier is correct?
                warning(StringEnum.MMOITEMS, TIER, itemTier.getAsString());
                return null;
            }

            mmoItem = mmoItemsPlugin.getMMOItem(type, id, level, tier);

        } else if (!(itemLevel == null || itemLevel instanceof JsonNull)) { // itemTier == null
            int level = itemLevel.getAsInt();
            if (level < 1) { // level is correct?
                warning(StringEnum.MMOITEMS, SCALED, level);
                return null;
            }

            mmoItem = mmoItemsPlugin.getMMOItem(type, id, level, null);

        } else if (!(itemTier == null || itemTier instanceof JsonNull)) { // itemLevel == null
            ItemTier tier = mmoItemsPlugin.getTiers().get(itemTier.getAsString());
            if (tier == null) { // tier is correct?
                warning(StringEnum.MMOITEMS, TIER, itemTier.getAsString());
                return null;
            }

            mmoItem = mmoItemsPlugin.getMMOItem(type, id, 0, tier);

        } else {
            mmoItem = mmoItemsPlugin.getMMOItem(type, id, 0, null);
        }

        return mmoItem != null ? CustomItemStack.builder().itemStack(mmoItem.newBuilder().build()).quantity(hasQuantity ? itemQuantity.getAsInt() : 1).index(hasIndex ? Double.parseDouble(decimalFormat.format(itemIndex.getAsDouble())) : 1D).build() : null;
    }
    // endregion

    // region utils
    private void done(String key) {
        String message = String.format("The \"%s\" section in %s has been initialized", key, FILE_NAME);
        Brewing.getInstance().getSLF4JLogger().info(message);
    }

    private void fail(String key) {
        String message = String.format("The \"%s\" section in %s has an error and it will not take effect", key, FILE_NAME);
        Brewing.getInstance().getSLF4JLogger().error(message);
    }

    private void warning(String provider, String key) {
        String message = String.format("Cannot read property in %s. The problem arises in KEY:%s, VALUE:%s from SECTION:%s, is it empty?", FILE_NAME, provider, key, sectionName);
        Brewing.getInstance().getSLF4JLogger().warn(message);
    }

    private void warning(EnumBase provider, String key) {
        String message = String.format("Cannot read %s item in %s. The problem arises in KEY:%s from SECTION:%s, is it empty?", provider.getString(), FILE_NAME, key, sectionName);
        Brewing.getInstance().getSLF4JLogger().warn(message);
    }

    private void warning(EnumBase provider, String key, Object value) {
        String message = String.format("Cannot read %s item in %s. The problem arises in KEY:%s, VALUE:%s from SECTION:%s.", provider.getString(), FILE_NAME, key, value, sectionName);
        Brewing.getInstance().getSLF4JLogger().warn(message);
    }

    private boolean notInteger(String integer) {
        return Double.compare(2147483648.0, Double.parseDouble(integer)) <= 0 || Double.compare(-2147483649.0, Double.parseDouble(integer)) >= 0;
    }

    private boolean isNumeric(final CharSequence cs) {
        if (StringUtils.isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i)) && cs.charAt(i) != '.') {
                return false;
            }
        }
        return true;
    }
    // endregion
}