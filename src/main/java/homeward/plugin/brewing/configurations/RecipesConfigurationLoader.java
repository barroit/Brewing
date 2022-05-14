package homeward.plugin.brewing.configurations;

import com.google.gson.*;
import homeward.plugin.brewing.beans.CustomItemStack;
import homeward.plugin.brewing.beans.RecipesItem;
import homeward.plugin.brewing.constants.RecipesConfiguration;
import homeward.plugin.brewing.enumerates.EnumBase;
import homeward.plugin.brewing.enumerates.ErrorEnum;
import org.bukkit.configuration.ConfigurationSection;

import java.math.RoundingMode;
import java.util.Map;

import static homeward.plugin.brewing.constants.RecipesConfiguration.*;
import static homeward.plugin.brewing.utils.HomewardUtils.notInteger;
import static homeward.plugin.brewing.utils.HomewardUtils.notNumeric;

public class RecipesConfigurationLoader extends ConfigurationBase{
    RecipesConfigurationLoader(String roundingPattern, RoundingMode roundingMode) {
        super(roundingPattern, roundingMode);
    }

    @Override
    public void load() {
        super.load();
    }

    // region Set "display-name" into bean object
    @Override
    void setBrewingRecipeDisplayName() {
        keyName = DISPLAY_NAME;
        String displayName = configurationSection.getString(DISPLAY_NAME);
        String sectionPath = getPath(configurationSection, DISPLAY_NAME);
        if (displayName == null) {
            noKeyFoundWarning(sectionPath);
            return;
        } else if (displayName.isBlank()) {
            valueIncorrectWarning(sectionPath, displayName, ErrorEnum.STRING_IS_BLANK);
            return;
        }

        recipesBuilder.displayName(displayName);
        display$set = true;
    }
    // endregion

    // region Set "(substrate|restriction|yeast)" ItemStack into bean object
    @Override
    void setBrewingMaterialItemStack(final EnumBase materialType) {
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

            CustomItemStack itemStack;
            currentPath = itemPath;
            if (RecipesConfiguration.VANILLA.equalsIgnoreCase(provider)) {
                itemStack = createVanillaItemStack(item);
            } else if (RecipesConfiguration.ITEMSADDER.equalsIgnoreCase(provider)) {
                itemStack = createItemsAdderItemStack(item);
            } else if (RecipesConfiguration.MMOITEMS.equalsIgnoreCase(provider)) {
                itemStack = createMMOItemItemStack(item);
            } else {
                valueIncorrectWarning(providerPath, provider, ErrorEnum.VALUE_INCORRECT);
                return;
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

    // region Set "output" ItemStack into bean object
    @Override
    void setBrewingOutputItemStack() {
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

        CustomItemStack itemStack;
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
        } else {
            valueIncorrectWarning(providerPath, provider, ErrorEnum.VALUE_INCORRECT);
            return;
        }

        if (itemStack == null) return;

        recipesBuilder.output(itemStack);
        output$set = true;
    }
    // endregion

    // region Set "yield" into bean object
    @Override
    void setBrewingYield() {
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
        } else if (notNumeric(maxString)) {
            valueIncorrectWarning(maxPath, maxString, ErrorEnum.NOT_ACTUALLY_NUMERIC);
            return;
        } else if (maxString.contains(".") || notInteger(maxString)) {
            valueIncorrectWarning(maxPath, maxString, ErrorEnum.NUMBER_NOT_INTEGER);
            return;
        } else if (Integer.parseInt(maxString) < 1) {
            valueIncorrectWarning(maxPath, maxString, ErrorEnum.NUMBER_TOO_SMALL);
            return;
        }
        int max = Integer.parseInt(maxString);

        String minString = yieldSection.getString(MIN);
        String minPath = getPath(sectionPath, MIN);
        if (minString != null) {
            if (notNumeric(minString)) {
                valueIncorrectWarning(minPath, minString, ErrorEnum.NOT_ACTUALLY_NUMERIC);
                return;
            } else if (minString.contains(".") || notInteger(minString)) {
                valueIncorrectWarning(minPath, minString, ErrorEnum.NUMBER_NOT_INTEGER);
                return;
            } else if (Integer.parseInt(minString) < 1) {
                valueIncorrectWarning(minPath, minString, ErrorEnum.NUMBER_TOO_SMALL);
                return;
            } else if (Integer.parseInt(minString) > max) {
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

    // region Set "index" into bean object
    @Override
    void setBrewingIndex() {
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
        } else if (notNumeric(restrictionIndexString)) {
            valueIncorrectWarning(restrictionIndexPath, restrictionIndexString, ErrorEnum.NOT_ACTUALLY_NUMERIC);
            return;
        }
        double restrictionIndex = Double.parseDouble(decimalFormat.format(Double.valueOf(restrictionIndexString)));
        if (restrictionIndex < 0) {
            valueIncorrectWarning(restrictionIndexPath, restrictionIndex, ErrorEnum.NUMBER_TOO_SMALL);
            return;
        }

        String yeastIndexString = indexSection.getString(YEAST_INDEX);
        String yeastIndexPath = getPath(sectionPath, YEAST_INDEX);
        if (yeastIndexString == null) {
            noKeyFoundWarning(yeastIndexPath);
            return;
        } else if (notNumeric(yeastIndexString)) {
            valueIncorrectWarning(yeastIndexPath, yeastIndexString, ErrorEnum.NOT_ACTUALLY_NUMERIC);
            return;
        }
        double yeastIndex = Double.parseDouble(decimalFormat.format(Double.valueOf(yeastIndexString)));
        if (yeastIndex < 0) {
            valueIncorrectWarning(yeastIndexPath, yeastIndex, ErrorEnum.NUMBER_TOO_SMALL);
            return;
        }

        String yieldIndexString = indexSection.getString(YIELD_INDEX);
        String yieldIndexPath = getPath(sectionPath, YIELD_INDEX);
        if (yieldIndexString == null) {
            noKeyFoundWarning(yieldIndexPath);
            return;
        } else if (notNumeric(yieldIndexString)) {
            valueIncorrectWarning(yieldIndexPath, yieldIndexString, ErrorEnum.NOT_ACTUALLY_NUMERIC);
            return;
        }
        double yieldIndex = Double.parseDouble(decimalFormat.format(Double.valueOf(yieldIndexString)));
        if (yieldIndex < 0) {
            valueIncorrectWarning(yieldIndexPath, yieldIndex, ErrorEnum.NUMBER_TOO_SMALL);
            return;
        }

        recipesBuilder.globalRestrictionsIndex(restrictionIndex).globalYeastsIndex(yeastIndex).globalYieldIndex(yieldIndex);
        index$set = true;
    }
    // endregion

    // region Set "cycle" into bean object
    @Override
    void setBrewingCycle() {
        keyName = BREWING_CYCLE;
        String brewingCycle = configurationSection.getString(BREWING_CYCLE);
        String path = getPath(configurationSection, BREWING_CYCLE);
        if (brewingCycle == null) {
            noKeyFoundWarning(path);
            return;
        } else if (notNumeric(brewingCycle)) {
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

    public static RecipesConfigurationLoaderBuilder builder() {
        return new RecipesConfigurationLoaderBuilder();
    }

    public static class RecipesConfigurationLoaderBuilder {
        private String roundingPattern;
        private RoundingMode roundingMode;

        public RecipesConfigurationLoaderBuilder roundingPattern(String roundingPattern) {
            this.roundingPattern = roundingPattern;
            return this;
        }

        public RecipesConfigurationLoaderBuilder roundingMode(RoundingMode roundingMode) {
            this.roundingMode = roundingMode;
            return this;
        }

        public RecipesConfigurationLoader build() {
            return new RecipesConfigurationLoader(this.roundingPattern, this.roundingMode);
        }
    }
}
