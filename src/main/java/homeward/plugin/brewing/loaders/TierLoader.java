package homeward.plugin.brewing.loaders;

import homeward.plugin.brewing.Main;
import homeward.plugin.brewing.beans.ItemProperties;
import homeward.plugin.brewing.utilities.BrewingUtils;
import homeward.plugin.brewing.utilities.ItemPropertiesUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TierLoader {
    private final Logger logger = Main.getInstance().getSLF4JLogger();

    Map<ConfigurationLoader.ConfigEnum, LinkedHashMap<File, YamlConfiguration>> configurationList = ConfigurationLoader.getInstance().getConfigurationList();

    // region load item tier
    public void loadItemTier() {
        LinkedHashMap<File, YamlConfiguration> configurationFileList = configurationList.get(ConfigurationLoader.ConfigEnum.DEFAULT_CONFIG);

        if (configurationFileList == null || configurationFileList.isEmpty()) return;

        configurationFileList.forEach((file, configuration) -> {
            ConfigurationSection itemTierSection = configuration.getConfigurationSection("item-tier");

            if (itemTierSection == null) {
                logger.warn(String.format("The key %s in %s does not exist or incorrect", "item-tier", file.getAbsolutePath()));
                logger.error("Brewing plugin cannot working without an item tier, this plugin will be disabled");
                Main.disable();
                return;
            }
            Set<String> tierKeySet = itemTierSection.getKeys(false);
            tierKeySet.forEach(tierKey -> {
                ConfigurationSection keySection = itemTierSection.getConfigurationSection(tierKey);
                if (keySection == null) {
                    logger.warn(String.format("The key %s in %s does not exist or incorrect", BrewingUtils.getPath(itemTierSection.getCurrentPath(), tierKey), file.getAbsolutePath()));
                    return;
                }

                ItemProperties.Content tier = ItemPropertiesUtils.getContent(file, keySection);
                if (tier == null) return;
                Main.itemTier(tierKey, tier);
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
