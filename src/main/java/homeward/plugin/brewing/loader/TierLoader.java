package homeward.plugin.brewing.loader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import homeward.plugin.brewing.Main;
import homeward.plugin.brewing.bean.ItemProperties;
import homeward.plugin.brewing.utilitie.BrewingUtils;
import homeward.plugin.brewing.utilitie.ItemPropertiesUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.slf4j.Logger;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
// loading recipe-tier and item-tier from config.yml
class TierLoader {
    private final Logger logger = Main.getInstance().getSLF4JLogger();

    Map<ConfigurationLoader.ConfigEnum, LinkedHashMap<File, YamlConfiguration>> configurationList = ConfigurationLoader.getInstance().getConfigurationList();

    // region load item tier
    public void loadItemTier() {
        LinkedHashMap<File, YamlConfiguration> configurationFileList = configurationList.get(ConfigurationLoader.ConfigEnum.DEFAULT_CONFIG);

        if (configurationFileList == null || configurationFileList.isEmpty()) return;

        Main.clearItemTier();

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
                    Main.itemTier(tierKey, ItemProperties.getContent().text(keyString));
                    return;
                }

                ItemProperties.Content tier = ItemPropertiesUtils.getContent(file, keySection);
                if (tier == null) return;
                Main.itemTier(tierKey, tier);
            });
        });
    }
    // endregion

    // region load recipe tier
    public void loadRecipeTier() {
        LinkedHashMap<File, YamlConfiguration> configurationFileList = configurationList.get(ConfigurationLoader.ConfigEnum.DEFAULT_CONFIG);

        if (configurationFileList == null || configurationFileList.isEmpty()) return;

        Main.clearRecipeTier();

        configurationFileList.forEach((file, configuration) -> {
            List<?> recipeTierList = configuration.getList("recipe-tier");

            if (recipeTierList == null) {
                return;
            }

            AtomicInteger atomicInteger = new AtomicInteger();
            recipeTierList.forEach(recipeTier -> {
                int index = atomicInteger.getAndIncrement();
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

                ItemStack item = Main.tierItemStackMap().getOrDefault(itemString, null);
                if (item == null) {
                    logger.warn(String.format("The value %s of key %s in %s does not exist or incorrect", itemString, BrewingUtils.getPath("recipe-tier[" + index + "]", "item"), file.getAbsolutePath()));
                    return;
                }

                Main.recipeTier(level, item);
            });
        });
    }
    // endregion

    // region initialize class
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
    // endregion
}
