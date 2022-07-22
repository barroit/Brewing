package com.baioretto.brewing.loader;


import com.baioretto.baiolib.api.extension.sender.command.ConsoleCommandSenderImpl;
import com.google.common.collect.Maps;
import com.baioretto.brewing.Brewing;
import com.baioretto.brewing.enumerate.EnumBase;
import lombok.experimental.ExtensionMethod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

// loading all file from plugin data folder
@ExtensionMethod(ConsoleCommandSenderImpl.class)
public class ConfigurationLoader {
    private final File dataFolder = Brewing.instance().getDataFolder();

    private final File defaultConfigFile = new File(dataFolder, "config.yml");

    private final File itemFolder = new File(dataFolder, "items");
    private final File recipeFolder = new File(dataFolder, "recipes");

    /**
     * map(configType, map(fileObject, configurationObject))
     */
    public static final Map<ConfigEnum, LinkedHashMap<File, YamlConfiguration>> CONFIGURATION_LIST = Maps.newHashMap();

    // load
    public void load() {
        long start = System.currentTimeMillis();
        TextComponent prefix = Component.text("[Brewing] ", NamedTextColor.AQUA);
        TextComponent startMessage = Component.text("loading plugin, please wait...", NamedTextColor.YELLOW);
        Brewing.instance().getServer().getConsoleSender().sendMessage(Component.text().append(prefix, startMessage));

        loadFiles();

        loadItems();

        loadRecipes();

        long end = System.currentTimeMillis();
        TextComponent endMessage = Component.text("Loaded all config files in ", NamedTextColor.GREEN).append(Component.text(end - start + " ms", NamedTextColor.AQUA));
        Brewing.instance().getServer().getConsoleSender().sendMessage(Component.text().append(prefix, endMessage));
    }

    // load file
    private void loadFiles() {
        this.createDefaultConfigDirectory();
        this.createItemConfigDirectory();
        this.createRecipeConfigDirectory();

        CONFIGURATION_LIST.clear();

        this.loadDefaultConfig();
        this.loadItemConfig();
        this.loadRecipeConfig();
    }

    // load item
    private void loadItems() {
        TierLoader.getInstance().loadItemTierContents();
        ItemPropertiesLoader.instance().loadItemsProperties();
        ItemStackLoader.getInstance().convertPropertiesToItemStack();
    }

    // load recipe
    private void loadRecipes() {
        TierLoader.getInstance().loadRecipeTier();
        RecipePropertiesLoader.getInstance().loadRecipeProperties();
        ItemStackLoader.getInstance().convertRecipeToItemStack();
    }

    // load default config files
    private void loadDefaultConfig() {
        this.updateConfigurationList(ConfigEnum.DEFAULT_CONFIG, defaultConfigFile, YamlConfiguration.loadConfiguration(defaultConfigFile));
    }

    // load item config
    private void loadItemConfig() {
        this.loadCustomConfig(itemFolder, ConfigEnum.ITEM_CONFIG);
    }

    // load recipe config
    private void loadRecipeConfig() {
        this.loadCustomConfig(recipeFolder, ConfigEnum.RECIPE_CONFIG);
    }

    // load custom config
    private void loadCustomConfig(File folder, ConfigEnum type) {
        File[] files = folder.listFiles();

        if (files != null && files.length != 0) {
            for (File file : files) {
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                this.updateConfigurationList(type, file, configuration);
            }
        }
    }

    // update configuration list
    private void updateConfigurationList(ConfigEnum type, File file, YamlConfiguration configuration) {
        LinkedHashMap<File, YamlConfiguration> configMap = CONFIGURATION_LIST.get(type);

        if (configMap == null) {
            configMap = new LinkedHashMap<>();
        }

        configMap.put(file, configuration);
        CONFIGURATION_LIST.put(type, configMap);
    }

    // create default config
    private void createDefaultConfigDirectory() {
        if (!defaultConfigFile.exists()) {
            Brewing.instance().saveResource("config.yml", false);
        }
    }

    // create items config
    private void createItemConfigDirectory() {
        String[] itemList = itemFolder.list();
        if (!itemFolder.exists() || itemList == null || itemList.length == 0) {
            Brewing.instance().saveResource("items/tier.yml", false);
            Brewing.instance().saveResource("items/template.yml", false);
        }
    }

    // create recipes config
    private void createRecipeConfigDirectory() {
        String[] recipeList = recipeFolder.list();
        if (!recipeFolder.exists() || recipeList == null || recipeList.length == 0) {
            Brewing.instance().saveResource("recipes/wine.yml", false);
        }
    }

    private ConfigurationLoader() {
    }

    private static volatile ConfigurationLoader instance;

    /**
     * cannot be used when plugin main class instancing
     */
    public static ConfigurationLoader getInstance() {
        if (instance == null) {
            synchronized (ConfigurationLoader.class) {
                if (instance == null) {
                    instance = new ConfigurationLoader();
                }
            }
        }
        return instance;
    }

    // config enum
    enum ConfigEnum implements EnumBase {
        DEFAULT_CONFIG("default config"),
        ITEM_CONFIG("item config"),
        RECIPE_CONFIG("recipe config");

        private final String type;

        ConfigEnum(String type) {
            this.type = type;
        }

        @Override
        public String getString() {
            return type;
        }
    }
}