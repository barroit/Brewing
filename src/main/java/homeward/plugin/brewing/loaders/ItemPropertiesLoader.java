package homeward.plugin.brewing.loaders;


import com.google.common.collect.Sets;
import homeward.plugin.brewing.Main;
import homeward.plugin.brewing.beans.ItemProperties;
import homeward.plugin.brewing.enumerates.ItemTypeEnum;
import homeward.plugin.brewing.enumerates.ProviderEnum;
import homeward.plugin.brewing.utilities.BrewingUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;

import java.io.File;
import java.util.*;

import static homeward.plugin.brewing.utilities.ItemPropertiesUtils.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemPropertiesLoader {
    private final Logger logger = Main.getInstance().getSLF4JLogger();

    Map<ConfigurationLoader.ConfigEnum, LinkedHashMap<File, YamlConfiguration>> configurationList = ConfigurationLoader.getInstance().getConfigurationList();

    private static final Set<ItemProperties> itemPropertiesSet = Sets.newLinkedHashSet();

    // region load item properties
    // region load
    public void loadItems() {
        LinkedHashMap<File, YamlConfiguration> configurationFileList = configurationList.get(ConfigurationLoader.ConfigEnum.ITEM_CONFIG);

        if (configurationFileList == null || configurationFileList.isEmpty()) return;

        configurationFileList.forEach(this::configurationFileAction);

        sort();
    }
    // endregion

    // region foreach configuration file top keys
    private void configurationFileAction(final File file, final YamlConfiguration configuration) {
        List<String> headerList = configuration.options().getHeader();
        for (String header : headerList)
            if (header.equalsIgnoreCase("disable")) return;
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
            ItemTypeEnum itemType = ItemTypeEnum.getItemType(topKey.toUpperCase(Locale.ROOT));
            if (itemType == null) {
                logger.warn(String.format("illegal key %s in %s", topKey, file.getAbsolutePath()));
                return;
            }

            ItemProperties itemProperties = buildItem(file, topSection, itemKey, itemType);
            if (itemProperties == null) return;

            itemPropertiesSet.add(itemProperties);
        });
    }
    // endregion

    // region build item basic information and add ItemProperties to itemPropertiesSet
    // itemSection: topKey.xxx
    @SuppressWarnings("DuplicateBranchesInSwitch")
    private ItemProperties buildItem(final File file, final ConfigurationSection topSection, final String itemKey, final ItemTypeEnum type) {
        ConfigurationSection itemSection = topSection.getConfigurationSection(itemKey);
        if (itemSection == null) {
            logger.warn(String.format("The key %s in %s does not exist or incorrect", BrewingUtils.getPath(topSection.getCurrentPath(), itemKey), file.getAbsolutePath()));
            return null;
        }

        // required
        ProviderEnum provider = getProvider(file, itemSection);
        if (provider == null) return null;

        // default: null
        ItemProperties.Content display = getDisplay(file, itemSection);

        // default: null
        ArrayList<ItemProperties.Content> lore = getLore(file, itemSection);

        // required
        Material material = getMaterial(file, itemSection);
        if (material == null) return null;

        // default: 0
        int customModuleData = getCustomModuleData(itemSection);

        // default: null
        String tier = getTier(file, itemSection);

        ItemProperties.ItemPropertiesBuilder builder = ItemProperties.builder();

        switch (type) {
            case TIER -> {
            }
            case SUBSTRATE -> builder = buildSubstrate(file, itemSection, builder);
            case YEAST -> builder = buildYeast(itemSection, builder);
            case OUTPUT -> builder = buildSubstrate(file, itemSection, builder);
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

    // endregion

    // region sort item
    private void sort() {
        itemPropertiesSet.forEach(itemProperties -> {
            ItemTypeEnum type = itemProperties.type();
            switch (type) {
                case TIER -> Main.tierItemPropertiesMap(itemProperties.id(), itemProperties);
                case SUBSTRATE -> Main.substrateItemPropertiesMap(itemProperties.id(), itemProperties);
                case YEAST -> Main.yeastItemPropertiesMap(itemProperties.id(), itemProperties);
                case OUTPUT -> Main.outputItemPropertiesMap(itemProperties.id(), itemProperties);
            }
        });
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