package homeward.plugin.brewing.commands;

import com.google.gson.*;
import dev.lone.itemsadder.api.CustomStack;
import homeward.plugin.brewing.Brewing;
import homeward.plugin.brewing.beans.CustomItemStack;
import homeward.plugin.brewing.beans.RecipesItem;
import homeward.plugin.brewing.beans.RecipesItem.RecipesItemBuilder;
import homeward.plugin.brewing.constants.RecipesConfiguration;
import homeward.plugin.brewing.enumerates.EnumBase;
import homeward.plugin.brewing.enumerates.ErrorEnum;
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
    private String sectionName, keyName, currentPath;
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
            noKeyFoundWarning(sectionName);
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
        String brewingCycle = configurationSection.getString(BREWING_CYCLE);
        String path = getPath(configurationSection, BREWING_CYCLE);
        if (brewingCycle == null) {
            noKeyFoundWarning(path);
            return;
        }
        if (notNumeric(brewingCycle)) {
            valueIncorrectWarning(path, brewingCycle, ErrorEnum.NOT_ACTUALLY_NUMERIC);
            return;
        }

        int cycle = Integer.parseInt(brewingCycle);
        if (cycle < 0) {
            valueIncorrectWarning(path, brewingCycle, ErrorEnum.NUMBER_TOO_SMALL);
            return;
        }

        recipesBuilder.brewingCycle(cycle);
        cycle$set = true;
    }
    // endregion

    // region Set "index" into bean object
    private void setBrewingIndex() {
        keyName = INDEX;
        ConfigurationSection indexSection = configurationSection.getConfigurationSection(INDEX);
        String sectionPath = getPath(configurationSection, INDEX);
        if (indexSection == null) {
            noKeyFoundWarning(sectionPath);
            return;
        }

        String restrictionIndexString = indexSection.getString(RESTRICTION_INDEX);
        String restrictionIndexPath = getPath(sectionPath, RESTRICTION_INDEX);
        if (restrictionIndexString == null) {
            noKeyFoundWarning(restrictionIndexPath);
            return;
        }
        if (notNumeric(restrictionIndexString)) {
            valueIncorrectWarning(restrictionIndexPath, restrictionIndexString, ErrorEnum.NOT_ACTUALLY_NUMERIC);
            return;
        }

        String yeastIndexString = indexSection.getString(YEAST_INDEX);
        String yeastIndexPath = getPath(sectionPath, YEAST_INDEX);
        if (yeastIndexString == null) {
            noKeyFoundWarning(yeastIndexPath);
            return;
        }
        if (notNumeric(yeastIndexString)) {
            valueIncorrectWarning(yeastIndexPath, yeastIndexString, ErrorEnum.NOT_ACTUALLY_NUMERIC);
            return;
        }

        String yieldIndexString = indexSection.getString(YIELD_INDEX);
        String yieldIndexPath = getPath(sectionPath, YIELD_INDEX);
        if (yieldIndexString == null) {
            noKeyFoundWarning(yieldIndexPath);
            return;
        }
        if (notNumeric(yieldIndexString)) {
            valueIncorrectWarning(yieldIndexPath, yieldIndexString, ErrorEnum.NOT_ACTUALLY_NUMERIC);
            return;
        }

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
        String sectionPath = getPath(configurationSection, YIELD);
        if (yieldSection == null) {
            noKeyFoundWarning(sectionPath);
            return;
        }

        String maxString = yieldSection.getString(MAX);
        String maxPath = getPath(sectionPath, MAX);
        if (maxString == null) {
            noKeyFoundWarning(maxPath);
            return;
        }
        if (notNumeric(maxString)) {
            valueIncorrectWarning(maxPath, maxString, ErrorEnum.NOT_ACTUALLY_NUMERIC);
            return;
        }
        if (notInteger(maxString)) {
            valueIncorrectWarning(maxPath, maxString, ErrorEnum.NUMBER_NOT_INTEGER);
            return;
        }
        if (Integer.parseInt(maxString) < 1) {
            valueIncorrectWarning(maxPath, maxString, ErrorEnum.NUMBER_TOO_SMALL);
            return;
        }
        int max = Integer.parseInt(maxString);

        String minString = yieldSection.getString(MIN);
        String minPath = getPath(sectionPath, MIN);
        if (minString != null) {
            if (notNumeric(maxString)) {
                valueIncorrectWarning(minPath, minString, ErrorEnum.NOT_ACTUALLY_NUMERIC);
                return;
            }
            if (notInteger(minString)) {
                valueIncorrectWarning(minPath, minString, ErrorEnum.NUMBER_NOT_INTEGER);
                return;
            }
            if (Integer.parseInt(minString) < 1) {
                valueIncorrectWarning(minPath, minString, ErrorEnum.NUMBER_TOO_SMALL);
                return;
            }
            if (Integer.parseInt(minString) > max) {
                valueIncorrectWarning(minPath, minString, ErrorEnum.NUMBER_TOO_LARGE);
                return;
            }
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
        String sectionPath = getPath(configurationSection, DISPLAY_NAME);
        if (displayName == null) {
            noKeyFoundWarning(sectionPath);
            return;
        }
        if (displayName.isBlank()) {
            valueIncorrectWarning(sectionPath, displayName, ErrorEnum.STRING_IS_BLANK);
            return;
        }

        recipesBuilder.displayName(displayName);
        display$set = true;
    }
    // endregion

    // region Set "output" into bean object
    private void setBrewingOutputItemStack() {
        keyName = OUTPUT;
        ConfigurationSection outputSection = configurationSection.getConfigurationSection(OUTPUT);
        String sectionPath = getPath(configurationSection, OUTPUT);
        if (outputSection == null) {
            noKeyFoundWarning(sectionPath);
            return;
        }

        String provider = outputSection.getString(PROVIDER);
        String providerPath = getPath(sectionPath, PROVIDER);
        if (provider == null) {
            noKeyFoundWarning(providerPath);
            return;
        }

        ConfigurationSection itemSection = outputSection.getConfigurationSection(ITEM);
        String itemPath = getPath(sectionPath, ITEM);
        if (itemSection == null) {
            noKeyFoundWarning(itemPath);
            return;
        }

        JsonObject item = new JsonObject();
        item.addProperty(QUANTITY, itemSection.getString(QUANTITY));

        CustomItemStack itemStack = null;
        currentPath = itemPath;
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

    // region Set "(substrate|restriction|yeast)" into bean object
    private void setBrewingMaterialItemStack(final EnumBase materialType) {
        keyName = materialType.getString();
        String materialStringSection = configurationSection.getString(materialType.getString()); // NPE
        String sectionPath = getPath(configurationSection, materialType.getString());
        if (materialStringSection == null) {
            noKeyFoundWarning(sectionPath);
            return;
        }

        JsonArray materialArray = new Gson().fromJson(materialStringSection.replaceAll("=", ":"), JsonArray.class);
        materialArray.forEach(jsonElement -> {
            JsonObject section = (JsonObject) jsonElement;

            JsonElement providerElement = section.get(PROVIDER);
            String providerPath = getPath(sectionPath, PROVIDER);
            if (providerElement == null || providerElement instanceof JsonNull) {
                noKeyFoundWarning(providerPath);
                return;
            }
            String provider = providerElement.getAsString();

            JsonElement itemElement = section.get(ITEM);
            String itemPath = getPath(sectionPath, ITEM);
            if (itemElement == null || itemElement instanceof JsonNull) {
                noKeyFoundWarning(itemPath);
                return;
            }
            JsonObject item = itemElement.getAsJsonObject();

            CustomItemStack itemStack = null;
            currentPath = itemPath;
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
        });
    }
    // endregion

    // region Create Vanilla ItemStack
    private CustomItemStack createVanillaItemStack(final JsonObject item) {
        JsonElement quantityElement = item.get(QUANTITY);
        boolean hasQuantity = quantityElement != null && !(quantityElement instanceof JsonNull);
        String quantityPath = getPath(currentPath, QUANTITY);
        if (hasQuantity && notNumeric(quantityElement.getAsString())) {
            valueIncorrectWarning(quantityPath, quantityElement.getAsString(), ErrorEnum.NOT_ACTUALLY_NUMERIC);
            return null;
        }

        JsonElement indexElement = item.get(INDEX);
        boolean hasIndex = indexElement != null && !(indexElement instanceof JsonNull);
        String indexPath = getPath(currentPath, INDEX);
        if (hasIndex && notNumeric(indexElement.getAsString())) {
            valueIncorrectWarning(indexPath, indexElement.getAsString(), ErrorEnum.NOT_ACTUALLY_NUMERIC);
            return null;
        }

        JsonElement materialElement = item.get(MATERIAL);
        String materialPath = getPath(currentPath, MATERIAL);
        if (materialElement == null || materialElement instanceof JsonNull) {
            noKeyFoundWarning(materialPath);
            return null;
        }

        Material material;
        try {
            material = Material.valueOf(materialElement.getAsString());
        } catch (IllegalArgumentException ignore) {
            valueIncorrectWarning(materialPath, materialElement.getAsString(), ErrorEnum.VALUE_INCORRECT);
            return null;
        }

        ItemStack itemStack = new ItemStack(material);

        // Generate Potion Type
        if (Material.POTION.equals(material)) {
            JsonElement potionTypeElement = item.get(POTION_TYPE);
            String potionTypePath = getPath(currentPath, POTION_TYPE);
            if (potionTypeElement == null || potionTypeElement instanceof JsonNull) {
                noKeyFoundWarning(potionTypePath);
                return null;
            }

            PotionType potionType;
            try {
                potionType = PotionType.valueOf(potionTypeElement.getAsString());
            } catch (IllegalArgumentException ignore) {
                valueIncorrectWarning(potionTypePath, potionTypeElement.getAsString(), ErrorEnum.VALUE_INCORRECT);
                return null;
            }

            itemStack.editMeta(PotionMeta.class, meta -> meta.setBasePotionData( new PotionData(potionType)));
        }

        return CustomItemStack.builder().itemStack(itemStack).quantity(hasQuantity ? quantityElement.getAsInt() : 1).index(hasIndex ? Double.parseDouble(decimalFormat.format(indexElement.getAsDouble())) : 1D).build();
    }
    // endregion

    // region Create ItemsAdder ItemStack
    private CustomItemStack createItemsAdderItemStack(final JsonObject item) {
        JsonElement quantityElement = item.get(QUANTITY);
        boolean hasQuantity = quantityElement != null && !(quantityElement instanceof JsonNull);
        String quantityPath = getPath(currentPath, QUANTITY);
        if (hasQuantity && notNumeric(quantityElement.getAsString())) {
            valueIncorrectWarning(quantityPath, quantityElement.getAsString(), ErrorEnum.NOT_ACTUALLY_NUMERIC);
            return null;
        }

        JsonElement indexElement = item.get(INDEX);
        boolean hasIndex = indexElement != null && !(indexElement instanceof JsonNull);
        String indexPath = getPath(currentPath, INDEX);
        if (hasIndex && notNumeric(indexElement.getAsString())) {
            valueIncorrectWarning(indexPath, indexElement.getAsString(), ErrorEnum.NOT_ACTUALLY_NUMERIC);
            return null;
        }

        JsonElement namespaceElement = item.get(NAMESPACE);
        String namespacePath = getPath(currentPath, NAMESPACE);
        if (namespaceElement == null || namespaceElement instanceof JsonNull) {
            noKeyFoundWarning(namespacePath);
            return null;
        }
        String namespace = namespaceElement.getAsString();
        if (namespace.isBlank()) {
            valueIncorrectWarning(namespacePath, namespace, ErrorEnum.STRING_IS_BLANK);
            return null;
        }

        JsonElement idElement = item.get(ID);
        String idPath = getPath(currentPath, ID);
        if (idElement == null || idElement instanceof JsonNull) {
            noKeyFoundWarning(idPath);
            return null;
        }
        String id = idElement.getAsString();
        if (id.isBlank()) {
            valueIncorrectWarning(idPath, id, ErrorEnum.STRING_IS_BLANK);
            return null;
        }

        String namespacedId = namespace + ':' + id;
        String namespacedIdPath = getPath(currentPath, NAMESPACED_ID);

        CustomStack itemInstance = CustomStack.getInstance(namespacedId); // NPE
        if (itemInstance == null) {
            valueIncorrectWarning(namespacedIdPath, namespacedId, ErrorEnum.NAMESPACE_ID_INCORRECT);
            return null;
        }

        ItemStack itemStack = ItemStack.deserializeBytes(itemInstance.getItemStack().serializeAsBytes()); // Prevent IA internal bug

        return CustomItemStack.builder().itemStack(itemStack).quantity(hasQuantity ? quantityElement.getAsInt() : 1).index(hasIndex ? Double.parseDouble(decimalFormat.format(indexElement.getAsDouble())) : 1D).build();
    }
    // endregion

    // region Create MMOItem ItemStack
    private CustomItemStack createMMOItemItemStack(final JsonObject item) {
        JsonElement quantityElement = item.get(QUANTITY);
        boolean hasQuantity = quantityElement != null && !(quantityElement instanceof JsonNull);
        String quantityPath = getPath(currentPath, QUANTITY);
        if (hasQuantity && notNumeric(quantityElement.getAsString())) {
            valueIncorrectWarning(quantityPath, quantityElement.getAsString(), ErrorEnum.NOT_ACTUALLY_NUMERIC);
            return null;
        }

        JsonElement indexElement = item.get(INDEX);
        boolean hasIndex = indexElement != null && !(indexElement instanceof JsonNull);
        String indexPath = getPath(currentPath, INDEX);
        if (hasIndex && notNumeric(indexElement.getAsString())) {
            valueIncorrectWarning(indexPath, indexElement.getAsString(), ErrorEnum.NOT_ACTUALLY_NUMERIC);
            return null;
        }

        JsonElement typeElement = item.get(TYPE);
        String typePath = getPath(currentPath, TYPE);
        if (typeElement == null || typeElement instanceof JsonNull) {
            noKeyFoundWarning(typePath);
            return null;
        }
        Type type = mmoItemsPlugin.getTypes().get(typeElement.getAsString());
        if (type == null) {
            valueIncorrectWarning(typePath, typeElement.getAsString(), ErrorEnum.VALUE_INCORRECT);
            return null;
        }

        JsonElement idElement = item.get(ID);
        String idPath = getPath(currentPath, TYPE);
        if (idElement == null || idElement instanceof JsonNull){
            noKeyFoundWarning(idPath);
            return null;
        }
        String id = idElement.getAsString();

        if (mmoItemsPlugin.getTemplates().getTemplate(type, id) == null) {
            valueIncorrectWarning(idPath, id, ErrorEnum.VALUE_INCORRECT);
            return null;
        }

        MMOItem mmoItem;
        JsonElement levelElement = item.get(SCALED);
        String levelPath = getPath(currentPath, SCALED);

        JsonElement tierElement = item.get(TIER);
        String tierPath = getPath(currentPath, TIER);

        if (!((levelElement == null || levelElement instanceof JsonNull) || (tierElement == null || tierElement instanceof JsonNull))) {
            String levelString = levelElement.getAsString();
            if (notNumeric(levelString)) {
                valueIncorrectWarning(levelPath, levelString, ErrorEnum.NOT_ACTUALLY_NUMERIC);
            }
            if (notInteger(levelString)) {
                valueIncorrectWarning(levelPath, levelString, ErrorEnum.NUMBER_NOT_INTEGER);
            }
            int level = levelElement.getAsInt();
            if (level < 1) {
                valueIncorrectWarning(levelPath, level, ErrorEnum.NUMBER_TOO_SMALL);
                return null;
            }

            String tierString = tierElement.getAsString();
            if (tierString.isBlank()) {
                valueIncorrectWarning(tierPath, tierString, ErrorEnum.STRING_IS_BLANK);
            }
            ItemTier tier = mmoItemsPlugin.getTiers().get(tierString);
            if (tier == null) {
                valueIncorrectWarning(tierPath, tierString, ErrorEnum.VALUE_INCORRECT);
                return null;
            }

            mmoItem = mmoItemsPlugin.getMMOItem(type, id, level, tier);

        } else if (!(levelElement == null || levelElement instanceof JsonNull)) {
            String levelString = levelElement.getAsString();
            if (notNumeric(levelString)) {
                valueIncorrectWarning(levelPath, levelString, ErrorEnum.NOT_ACTUALLY_NUMERIC);
            }
            if (notInteger(levelString)) {
                valueIncorrectWarning(levelPath, levelString, ErrorEnum.NUMBER_NOT_INTEGER);
            }
            int level = levelElement.getAsInt();
            if (level < 1) {
                valueIncorrectWarning(levelPath, level, ErrorEnum.NUMBER_TOO_SMALL);
                return null;
            }

            mmoItem = mmoItemsPlugin.getMMOItem(type, id, level, null);

        } else if (!(tierElement == null || tierElement instanceof JsonNull)) {
            String tierString = tierElement.getAsString();
            if (tierString.isBlank()) {
                valueIncorrectWarning(tierPath, tierString, ErrorEnum.STRING_IS_BLANK);
            }
            ItemTier tier = mmoItemsPlugin.getTiers().get(tierString);
            if (tier == null) {
                valueIncorrectWarning(tierPath, tierString, ErrorEnum.VALUE_INCORRECT);
                return null;
            }

            mmoItem = mmoItemsPlugin.getMMOItem(type, id, 0, tier);

        } else {
            mmoItem = mmoItemsPlugin.getMMOItem(type, id, 0, null);
        }

        return mmoItem != null ? CustomItemStack.builder().itemStack(mmoItem.newBuilder().build()).quantity(hasQuantity ? quantityElement.getAsInt() : 1).index(hasIndex ? Double.parseDouble(decimalFormat.format(indexElement.getAsDouble())) : 1D).build() : null;
    }
    // endregion

    // region utils
    private void done(String key) {
        String message = String.format("The \"%s\" section in %s has been initialized", key, FILE_NAME);
        Brewing.getInstance().getSLF4JLogger().info(message);
    }

    private void fail(String key) {
        String message = String.format("The \"%s\" section in %s has an error and this section will not take effect", key, FILE_NAME);
        Brewing.getInstance().getSLF4JLogger().error(message);
    }

    private void noKeyFoundWarning(String path) {
        String message = String.format("cannot read %s in %s, does the key exist or the value not null?", path, FILE_NAME);
        Brewing.getInstance().getSLF4JLogger().warn(message);
    }

    private void valueIncorrectWarning(String path, Object value, EnumBase errorEnum) {
        String message = String.format("cannot read %s:%s in %s, caused by %s", path, value, FILE_NAME, errorEnum.getString());
        Brewing.getInstance().getSLF4JLogger().warn(message);
    }

    private boolean notInteger(String integer) {
        return Double.compare(2147483648.0, Double.parseDouble(integer)) <= 0 || Double.compare(-2147483649.0, Double.parseDouble(integer)) >= 0;
    }

    private boolean notNumeric(final CharSequence cs) {
        if (StringUtils.isEmpty(cs)) {
            return true;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i)) && cs.charAt(i) != '.') {
                return i != 0 || cs.charAt(i) != '-';
            }
        }
        return false;
    }

    private String getPath(ConfigurationSection section, String current) {
        return section.getCurrentPath() + '.' + current;
    }

    private String getPath(String path, String append) {
        return path + '.' + append;
    }
    // endregion
}