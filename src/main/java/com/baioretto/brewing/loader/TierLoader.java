package com.baioretto.brewing.loader;

import com.baioretto.baiolib.api.extension.logger.LoggerImpl;
import com.baioretto.brewing.bean.ItemProperties;
import com.baioretto.brewing.enumerate.Type;
import com.baioretto.brewing.util.BrewingUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.baioretto.brewing.Container;
import com.baioretto.brewing.Brewing;
import com.baioretto.brewing.util.ConfigurationUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

// loading recipe-tier and item-tier from config.yml
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ExtensionMethod(LoggerImpl.class)
class TierLoader {
    private final Logger logger = Brewing.instance().getLogger();

    // load item tier
    public void loadItemTierContents() {
        LinkedHashMap<File, YamlConfiguration> configurationFileList = ConfigurationLoader.CONFIGURATION_LIST.get(ConfigurationLoader.ConfigEnum.DEFAULT_CONFIG);

        if (configurationFileList == null || configurationFileList.isEmpty()) return;

        Container.ITEM_TIER.clear();

        configurationFileList.forEach((file, configuration) -> {
            ConfigurationSection itemTierSection = configuration.getConfigurationSection("item-tier");

            if (itemTierSection == null) {
                return;
            }
            Set<String> tierKeySet = itemTierSection.getKeys(false);
            tierKeySet.forEach(tierKey -> {
                ConfigurationSection keySection = itemTierSection.getConfigurationSection(tierKey);
                if (keySection == null) {
                    String keyString = itemTierSection.getString(tierKey);
                    if (keyString == null || keyString.isBlank()) {
                        logger.warn(String.format("The key %s in %s does not exist or incorrect", BrewingUtils.getPath(itemTierSection.getCurrentPath(), tierKey), file.getAbsolutePath()));
                        return;
                    }
                    Container.ITEM_TIER.put(tierKey, ItemProperties.getContent().text(keyString));
                    return;
                }

                ItemProperties.Content tier = ConfigurationUtils.getContent(file, keySection);
                if (tier == null) return;
                Container.ITEM_TIER.put(tierKey, tier);
            });
        });
    }

    // load recipe tier
    public void loadRecipeTier() {
        LinkedHashMap<File, YamlConfiguration> configurationFileList = ConfigurationLoader.CONFIGURATION_LIST.get(ConfigurationLoader.ConfigEnum.DEFAULT_CONFIG);

        if (configurationFileList == null || configurationFileList.isEmpty()) return;

        Container.RECIPE_TIER.clear();

        configurationFileList.forEach((file, configuration) -> {
            List<?> recipeTierList = configuration.getList("recipe-tier");

            if (recipeTierList == null) {
                logger.warn("The recipe-tier in config.yml is empty. The plugin will disable brewing gui");
                return;
            }

            AtomicInteger atomicInteger = new AtomicInteger();
            recipeTierList.forEach(recipeTier -> {
                int index = atomicInteger.getAndIncrement();
                if (index > 3) {
                    logger.warn("The recipe-tier in config.yml can only have up to four levels. The plugin will not record the excess part");
                    return;
                }

                JsonObject object = JsonParser.parseString(recipeTier.toString()).getAsJsonObject();
                JsonElement levelElement = object.get("level");
                if (levelElement == null) {
                    logger.warn(String.format("The key %s in %s does not exist or incorrect", BrewingUtils.getPath("recipe-tier[" + index + "]", "level"), file.getAbsolutePath()));
                    return;
                }
                String level = levelElement.getAsString();

                JsonElement itemElement = object.get("item");
                if (itemElement == null) {
                    logger.warn(String.format("The key %s in %s does not exist or incorrect", BrewingUtils.getPath("recipe-tier[" + index + "]", "item"), file.getAbsolutePath()));
                    return;
                }
                String itemString = itemElement.getAsString();

                ItemStack item = Container.ITEM_STACK_MAP.get(Type.TIER).getOrDefault(itemString, null);
                if (item == null) {
                    logger.warn(String.format("The value %s of key %s in %s does not exist or incorrect", itemString, BrewingUtils.getPath("recipe-tier[" + index + "]", "item"), file.getAbsolutePath()));
                    return;
                }

                Container.RECIPE_TIER.put(level, item);
            });
        });
    }

    private volatile static TierLoader instance;

    public static TierLoader getInstance() {
        if (null == instance) {
            synchronized (TierLoader.class) {
                if (null == instance) {
                    instance = new TierLoader();
                }
            }
        }
        return instance;
    }
}