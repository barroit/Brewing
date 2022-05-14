package homeward.plugin.brewing.configurations;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import dev.lone.itemsadder.api.CustomStack;
import homeward.plugin.brewing.Brewing;
import homeward.plugin.brewing.beans.CustomItemStack;
import homeward.plugin.brewing.beans.Recipe;
import homeward.plugin.brewing.enumerates.EnumBase;
import homeward.plugin.brewing.enumerates.ErrorEnum;
import homeward.plugin.brewing.enumerates.StringEnum;
import homeward.plugin.brewing.utils.ConfigurationUtils;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Material;
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
import static homeward.plugin.brewing.constants.RecipesConfiguration.TIER;
import static homeward.plugin.brewing.utils.HomewardUtils.*;

public abstract class ConfigurationBase {
    final DecimalFormat decimalFormat;

    final MMOItems mmoItemsPlugin = MMOItems.plugin;
    FileConfiguration recipesFileConfiguration;
    final Set<EnumBase> materialTypeSet;

    Recipe.RecipeBuilder recipesBuilder;
    String sectionName, keyName, currentPath;
    ConfigurationSection configurationSection;
    boolean display$set, material$set, output$set, yield$set, index$set, cycle$set, level$set = false;

    ConfigurationBase(String roundingPattern, RoundingMode roundingMode) {
        this.materialTypeSet = new LinkedHashSet<>(Arrays.asList(StringEnum.SUBSTRATE, StringEnum.RESTRICTION, StringEnum.YEAST));
        decimalFormat = new DecimalFormat(roundingPattern);
        decimalFormat.setRoundingMode(roundingMode);
    }

    void load() {
        if (Brewing.getInstance().recipesMap().size() != 0) Brewing.getInstance().recipesMap().clear();

        recipesFileConfiguration = ConfigurationUtils.get("recipes");
        Set<String> recipeKeys = recipesFileConfiguration.getKeys(false);
        recipeKeys.forEach(this::keyAction);
    }

    private void keyAction(final String key) {
        display$set = material$set = output$set = yield$set = index$set = cycle$set = level$set = false;
        configurationSection = recipesFileConfiguration.getConfigurationSection(key);
        if (configurationSection == null) {
            noKeyFoundWarning(sectionName);
            return;
        }
        recipesBuilder = Recipe.builder();
        sectionName = key;

        setBrewingRecipeDisplayName(); // Set Display Name
        setBrewingRecipeLevel(); // Set Recipe Level
        materialTypeSet.forEach(this::setBrewingMaterialItemStack); // Set (Substrate|Restriction|Yeast) Name
        setBrewingOutputItemStack(); // Set Output ItemStack
        setBrewingYield(); // Set Yield
        setBrewingIndex(); // Set Index
        setBrewingCycle(); // Set Brewing Cycle

        if (display$set && material$set && output$set && yield$set && index$set && cycle$set && level$set) {
            Brewing.getInstance().recipesMap(key, recipesBuilder.build());
            done(key);
        } else {
            fail(key);
        }
    }

    abstract void setBrewingRecipeDisplayName();
    abstract void setBrewingRecipeLevel();
    abstract void setBrewingMaterialItemStack(final EnumBase materialType);
    abstract void setBrewingOutputItemStack();
    abstract void setBrewingYield();
    abstract void setBrewingIndex();
    abstract void setBrewingCycle();

    // region Generate Vanilla ItemStack
    CustomItemStack createVanillaItemStack(final JsonObject item) {
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

    // region Generate ItemsAdder ItemStack
    CustomItemStack createItemsAdderItemStack(final JsonObject item) {
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

    // region Generate MMOItem ItemStack
    CustomItemStack createMMOItemItemStack(final JsonObject item) {
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

    void done(String key) {
        String message = String.format("The recipe \"%s\" in %s was loaded successfully", key, FILE_NAME);
        Brewing.getInstance().getSLF4JLogger().info(message);
    }

    void fail(String key) {
        String message = String.format("The recipe \"%s\" in %s has an error and this recipe will not take effect", key, FILE_NAME);
        Brewing.getInstance().getSLF4JLogger().error(message);
    }

    void noKeyFoundWarning(String path) {
        String message = String.format("cannot read %s in %s, does the key exist or the value not null?", path, FILE_NAME);
        Brewing.getInstance().getSLF4JLogger().warn(message);
    }

    void valueIncorrectWarning(String path, Object value, EnumBase errorEnum) {
        String message = String.format("cannot read %s:%s in %s, caused by %s", path, value, FILE_NAME, errorEnum.getString());
        Brewing.getInstance().getSLF4JLogger().warn(message);
    }
}
