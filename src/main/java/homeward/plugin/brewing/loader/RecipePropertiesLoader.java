package homeward.plugin.brewing.loader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import homeward.plugin.brewing.Main;
import homeward.plugin.brewing.bean.ItemProperties;
import homeward.plugin.brewing.bean.RecipeProperties;
import homeward.plugin.brewing.enumerate.ItemTypeEnum;
import homeward.plugin.brewing.utilitie.BrewingUtils;
import homeward.plugin.brewing.utilitie.ItemPropertiesUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.slf4j.Logger;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

class RecipePropertiesLoader {
    private final Logger logger = Main.getInstance().getSLF4JLogger();

    private final Map<ConfigurationLoader.ConfigEnum, LinkedHashMap<File, YamlConfiguration>> configurationList = ConfigurationLoader.getInstance().getConfigurationList();

    // region load recipe properties
    public void loadRecipeProperties() {
        LinkedHashMap<File, YamlConfiguration> configurationFileList = configurationList.get(ConfigurationLoader.ConfigEnum.RECIPE_CONFIG);
        if (configurationFileList == null || configurationFileList.isEmpty()) return;

        Main.clearRecipes();

        configurationFileList.forEach((file, configuration) -> {
            boolean disabled = BrewingUtils.isDisabled(configuration);
            if (disabled) return;

            Set<String> topKeySet = configuration.getKeys(false);
            topKeySet.forEach(topKey -> {
                ConfigurationSection topSection = configuration.getConfigurationSection(topKey);

                assert topSection != null;

                String level = getLevel(file, topSection);
                if (level == null) return;

                ItemProperties.Content display = ItemPropertiesUtils.getDisplay(file, topSection);
                if (display == null) {
                    logger.warn(String.format("The key %s in %s does not exist or incorrect", BrewingUtils.getPath(topSection, "display"), file.getAbsolutePath()));
                    return;
                }

                LinkedHashSet<ItemStack> substrates = getSubstrates(file, topSection);
                if (substrates == null) return;

                LinkedHashSet<RecipeProperties.CustomItem> yeasts = getCustomItem(file, topSection, "yeast", false);
                if (yeasts == null) return;

                LinkedHashSet<RecipeProperties.CustomItem> extras = getCustomItem(file, topSection, "extra", true);
                if (extras == null) return;

                ItemStack output = getOutput(file, topSection);
                if (output == null) return;

                int[] yield = getYield(file, topSection);
                if (yield == null) return;

                int minYield = yield[0];
                int maxYield = yield[1];

                int cycle = getCycle(file, topSection);
                if (cycle == -1) return;

                ArrayList<ItemProperties.Content> lore = ItemPropertiesUtils.getLore(file, topSection);

                RecipeProperties recipeProperties = RecipeProperties.builder()
                        .id(topKey)
                        .level(level)
                        .display(display)
                        .lore(lore)
                        .substrates(substrates)
                        .yeasts(yeasts)
                        .extras(extras)
                        .output(output)
                        .minYield(minYield)
                        .maxYield(maxYield)
                        .cycle(cycle)
                        .build();

                Main.recipes(topKey, recipeProperties);
            });
        });
    }
    // endregion

    // region get level
    private String getLevel(final File file, final ConfigurationSection section) {
        String level = section.getString("level");
        if (level == null) {
            logger.warn(String.format("The key %s in %s does not exist or incorrect", BrewingUtils.getPath(section, "level"), file.getAbsolutePath()));
        }
        return level;
    }
    // endregion

    // region get substrate
    private LinkedHashSet<ItemStack> getSubstrates(final File file, final ConfigurationSection section) {
        List<?> substrateList = section.getList("substrate");
        if (substrateList == null || substrateList.size() == 0) {
            logger.warn(String.format("The key %s in %s does not exist or incorrect", BrewingUtils.getPath(section, "substrate"), file.getAbsolutePath()));
            return null;
        }

        LinkedHashSet<ItemStack> substrates = new LinkedHashSet<>();
        AtomicInteger atomicInteger = new AtomicInteger();
        substrateList.forEach(s -> {
            int index = atomicInteger.getAndIncrement();
            if (s instanceof String itemString) {
                if (!Main.substrateItemStackMap().containsKey(itemString)) {
                    logger.warn(String.format("The value %s of key %s in %s incorrect", itemString, BrewingUtils.getPath(section, "substrate[" + index + "]"), file.getAbsolutePath()));
                    return;
                }

                substrates.add(Main.substrateItemStackMap().get(itemString));
            } else {
                logger.warn(String.format("The object %s of key %s in %s is not a string", s, BrewingUtils.getPath(section, "substrate[" + index + "]"), file.getAbsolutePath()));
            }
        });

        return substrateList.size() == substrates.size() ? substrates : null;
    }
    // endregion

    // region get custom item
    private LinkedHashSet<RecipeProperties.CustomItem> getCustomItem(final File file, final ConfigurationSection section, String sectionName, boolean hasType) {
        List<?> customItemList = section.getList(sectionName);
        LinkedHashSet<RecipeProperties.CustomItem> customItem = new LinkedHashSet<>();

        if (customItemList == null) {
            return customItem;
        }

        AtomicInteger atomicInteger = new AtomicInteger();
        customItemList.forEach(rawYeast -> {
            int index = atomicInteger.getAndIncrement();
            if (!(rawYeast instanceof Map)) return;
            JsonObject customItemObject = JsonParser.parseString(rawYeast.toString()).getAsJsonObject();

            JsonElement itemElement = customItemObject.get("item");
            if (itemElement == null) {
                logger.warn(String.format("The key %s in %s does not exist or incorrect", BrewingUtils.getPath(section, sectionName + "[" + index + "]" + ".item"), file.getAbsolutePath()));
                return;
            }
            String itemString = itemElement.getAsString();

            JsonElement typeElement = customItemObject.get("type");
            ItemTypeEnum type = null;
            ItemStack itemStack;
            if (typeElement == null && hasType) {
                logger.warn(String.format("The key %s in %s does not exist or incorrect", BrewingUtils.getPath(section, sectionName + "[" + index + "]" + ".type"), file.getAbsolutePath()));
                return;
            } else if (typeElement != null && hasType) {
                String typeString = typeElement.getAsString();
                type = ItemTypeEnum.getItemType(typeString.toUpperCase(Locale.ROOT));
                if (type == null) {
                    logger.warn(String.format("The value %s of key %s in %s incorrect", typeString, BrewingUtils.getPath(section, sectionName + "[" + index + "]" + ".type"), file.getAbsolutePath()));
                    return;
                }

                boolean contains;
                switch (type) {
                    case SUBSTRATE -> {
                        contains = Main.substrateItemStackMap().containsKey(itemString);
                        itemStack = Main.substrateItemStackMap().get(itemString);
                    }
                    case YEAST -> {
                        contains = Main.yeastItemStackMap().containsKey(itemString);
                        itemStack = Main.yeastItemStackMap().get(itemString);
                    }
                    case OUTPUT -> {
                        contains = Main.outputItemStackMap().containsKey(itemString);
                        itemStack = Main.outputItemStackMap().get(itemString);
                    }
                    default -> {
                        return;
                    }
                }
                if (!contains) {
                    logger.warn(String.format("The item %s of type %s of key %s in %s not exist", itemString, type, BrewingUtils.getPath(section, sectionName + "[" + index + "]"), file.getAbsolutePath()));
                    return;
                }
            } else {
                if (!Main.yeastItemStackMap().containsKey(itemString)) {
                    logger.warn(String.format("The value %s of key %s in %s incorrect", itemString, BrewingUtils.getPath(section, sectionName + "[" + index + "]" + ".item"), file.getAbsolutePath()));
                    return;
                }
                itemStack = Main.yeastItemStackMap().get(itemString);
            }

            JsonElement amplifyElement = customItemObject.get("amplify");
            double amplify = 0;
            if (amplifyElement != null) {
                amplify = amplifyElement.getAsDouble();
            }

            RecipeProperties.CustomItem ci = RecipeProperties.getCustomItem().item(itemStack).amplify(amplify);
            if (type != null) ci.type(type);

            customItem.add(ci);
        });

        return customItemList.size() == customItem.size() ? customItem : null;
    }
    // endregion

    // region get output
    private ItemStack getOutput(final File file, final ConfigurationSection section) {
        String outputString = section.getString("output");
        if (outputString == null) {
            logger.warn(String.format("The key %s in %s does not exist or incorrect", BrewingUtils.getPath(section, "output"), file.getAbsolutePath()));
            return null;
        }

        if (!Main.outputItemStackMap().containsKey(outputString)) {
            logger.warn(String.format("The value %s of key %s in %s incorrect", outputString, BrewingUtils.getPath(section, "output"), file.getAbsolutePath()));
            return null;
        }

        return Main.outputItemStackMap().get(outputString);
    }
    // endregion

    // region get yield
    private int[] getYield(final File file, final ConfigurationSection section) {
        int min = section.getInt("yield.min");
        if (min < 0) {
            logger.warn(String.format("The value %s of key %s in %s is too small", min, BrewingUtils.getPath(section, "yield.min"), file.getAbsolutePath()));
            return null;
        }

        if (!section.contains("yield.max")) {
            logger.warn(String.format("The key %s in %s does not exist or incorrect", BrewingUtils.getPath(section, "yield.min"), file.getAbsolutePath()));
            return null;
        }
        int max = section.getInt("yield.max");
        if (max < min) {
            logger.warn(String.format("The value %s of key %s in %s is too small", max, BrewingUtils.getPath(section, "yield.max"), file.getAbsolutePath()));
            return null;
        }

        return new int[]{min, max};
    }
    // endregion

    // region get cycle
    private int getCycle(final File file, final ConfigurationSection section) {
        int cycle = section.getInt("cycle");
        if (cycle < 1) {
            logger.warn(String.format("The value %s of key %s in %s is too small", cycle, BrewingUtils.getPath(section, "cycle"), file.getAbsolutePath()));
        }
        return cycle < 1 ? -1 : cycle;
    }
    // endregion

    // region get instance
    private volatile static RecipePropertiesLoader instance;

    public static RecipePropertiesLoader getInstance() {
        if (null == instance) {
            synchronized (RecipePropertiesLoader.class) {
                if (null == instance) {
                    instance = new RecipePropertiesLoader();
                }
            }
        }
        return instance;
    }
    // endregion
}
