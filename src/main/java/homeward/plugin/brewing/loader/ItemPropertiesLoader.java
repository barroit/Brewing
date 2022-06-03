package homeward.plugin.brewing.loader;


import com.google.common.collect.Sets;
import homeward.plugin.brewing.Main;
import homeward.plugin.brewing.bean.ItemProperties;
import homeward.plugin.brewing.enumerate.Type;
import homeward.plugin.brewing.enumerate.Provider;
import homeward.plugin.brewing.utilitie.BrewingUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;

import java.io.File;
import java.util.*;

import static homeward.plugin.brewing.utilitie.ConfigurationUtils.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
// loading item properties from items folder
class ItemPropertiesLoader {
    private final Logger logger = Main.getInstance().getSLF4JLogger();

    @Getter private final Set<ItemProperties> itemPropertiesSet = Sets.newLinkedHashSet();

    // region load items properties
    public void loadItemsProperties() {
        LinkedHashMap<File, YamlConfiguration> configurationFileList = ConfigurationLoader.CONFIGURATION_LIST.get(ConfigurationLoader.ConfigEnum.ITEM_CONFIG);

        if (configurationFileList == null || configurationFileList.isEmpty()) return;

        itemPropertiesSet.clear();

        configurationFileList.forEach(this::configurationFileAction);
    }
    // endregion

    // region foreach configuration file top keys
    private void configurationFileAction(final File file, final YamlConfiguration configuration) {
        boolean disabled = BrewingUtils.isDisabled(configuration);
        if (disabled) return;
        Set<String> configurationKeySet = configuration.getKeys(false);
        configurationKeySet.forEach(key -> this.topKeyAction(file, configuration, key));
    }
    // endregion

    // region get top section by top key and foreach item by section.getKeys
    // topKey: tier | substrate | yeast | output
    private void topKeyAction(final File file, final YamlConfiguration fileConfiguration, final String topKey) {
        ConfigurationSection topSection = fileConfiguration.getConfigurationSection(topKey);
        if (topSection == null) {
            logger.warn(String.format("The key %s in %s does not exist or incorrect", topKey, file.getAbsolutePath()));
            return;
        }

        Set<String> itemKeySet = topSection.getKeys(false);
        itemKeySet.forEach(itemKey -> {
            Type type = Type.getType(topKey.toUpperCase(Locale.ROOT));
            if (type == null) {
                logger.warn(String.format("illegal key %s in %s", topKey, file.getAbsolutePath()));
                return;
            }

            ItemProperties itemProperties = buildItem(file, topSection, itemKey, type);
            if (itemProperties == null) return;

            itemPropertiesSet.add(itemProperties);
        });
    }
    // endregion

    // region build item basic information and add ItemProperties to itemPropertiesSet
    // itemSection: topKey.xxx
    private ItemProperties buildItem(final File file, final ConfigurationSection topSection, final String itemKey, final Type type) {
        ConfigurationSection itemSection = topSection.getConfigurationSection(itemKey);
        if (itemSection == null) {
            logger.warn(String.format("The key %s in %s does not exist or incorrect", BrewingUtils.getPath(topSection.getCurrentPath(), itemKey), file.getAbsolutePath()));
            return null;
        }

        // required
        Material material = getMaterial(file, itemSection);
        if (material == null) return null;
        if (Type.OUTPUT.equals(type) && !Material.POTION.equals(material)) {
            logger.warn(String.format("The material %s of key %s in %s is not a potion", material, BrewingUtils.getPath(topSection.getCurrentPath(), itemKey), file.getAbsolutePath()));
            return null;
        }

        // required
        Provider provider = getProvider(file, itemSection);
        if (provider == null) return null;

        // default: null
        ItemProperties.Content display = getDisplay(file, itemSection);

        // default: null
        ArrayList<ItemProperties.Content> lore = getLore(file, itemSection);

        // default: 0
        int customModuleData = getCustomModuleData(itemSection);

        // default: null
        String tier = getTier(file, itemSection);

        ItemProperties.ItemPropertiesBuilder builder = ItemProperties.builder();

        switch (type) {
            case TIER -> {}
            case SUBSTRATE, OUTPUT -> builder = buildSubstrate(file, itemSection, builder);
            case YEAST, CONTAINER -> builder = buildYeast(itemSection, builder);
        }
        if (builder == null) return null;

        return builder
                .id(itemKey)
                .type(type)
                .provider(provider)
                .display(display)
                .lore(lore)
                .material(material)
                .customModelData(customModuleData)
                .tier(tier)
                .build();
    }
    // endregion

    // region build substrate
    private ItemProperties.ItemPropertiesBuilder buildSubstrate(final File file, final ConfigurationSection itemSection, final ItemProperties.ItemPropertiesBuilder builder) {
        int restoreFood = getRestoreFood(itemSection); // default: 0
        double restoreHealth = getRestoreHealth(itemSection); // default: 0.0D
        double restoreSaturation = getRestoreSaturation(itemSection); // default: 0.0D

        ArrayList<ItemProperties.Effect> effect = getEffect(file, itemSection); // default: null
        if (effect != null && effect.size() == 0) return null;

        ArrayList<String> command = getCommand(itemSection); // default: null

        builder.restoreFood(restoreFood)
                .restoreHealth(restoreHealth)
                .restoreSaturation(Float.parseFloat(String.valueOf(restoreSaturation)))
                .effects(effect)
                .command(command);

        return buildYeast(itemSection, builder);
    }
    // endregion

    // region build yeast
    private ItemProperties.ItemPropertiesBuilder buildYeast(final ConfigurationSection itemSection, final ItemProperties.ItemPropertiesBuilder builder) {
        return builder.requiredLevel(getRequiredLevel(itemSection));
    }
    // endregion

    // region initialize class
    private volatile static ItemPropertiesLoader instance;

    public static ItemPropertiesLoader getInstance() {
        if (null == instance) {
            synchronized (ItemPropertiesLoader.class) {
                if (null == instance) {
                    instance = new ItemPropertiesLoader();
                }
            }
        }
        return instance;
    }
    // endregion
}