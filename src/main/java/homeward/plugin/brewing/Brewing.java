package homeward.plugin.brewing;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import homeward.plugin.brewing.beans.RecipesItem;
import homeward.plugin.brewing.commands.ConfigurationCommand;
import homeward.plugin.brewing.commands.MainCommand;
import homeward.plugin.brewing.commands.MockFileConfigurationTest;
import homeward.plugin.brewing.configurations.RecipesConfigurationLoader;
import homeward.plugin.brewing.constants.PluginInformation;
import homeward.plugin.brewing.events.BrewDataProcessEvent;
import homeward.plugin.brewing.utils.ConfigurationUtils;
import lombok.Getter;
import me.mattstudios.mf.base.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.reflections.Reflections;

import java.math.RoundingMode;
import java.util.*;
import java.util.logging.Level;

public final class Brewing extends JavaPlugin {
    private static Brewing plugin;
    private final String packageName;
    @Getter private final static Map<String, World> worldMap = new LinkedHashMap<>();
    @Deprecated
    @Getter private static final Map<String, JsonObject> configurationMap = new LinkedHashMap<>();

    private final Map<String, RecipesItem> recipesMap;

    /**
     * <h3>Get current plugin instance</h3>
     * <h2>Can only be used after the plugin main class instantiated</h2>
     *
     * @return plugin instance
     */
    public static Brewing getInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {
        loadRecipes(); // load recipes configuration

        CommandManager commandManager = new CommandManager(this);
        // register command here
        commandManager.register(new MainCommand());
        commandManager.register(new MockFileConfigurationTest());
        commandManager.register(new ConfigurationCommand());

        registerListeners();

        processFerment();

        setWorldMap();

        onEnableMessage();
    }

    public Brewing() {
        this.checkDepend();
        plugin = this;
        this.packageName = getClass().getPackageName();
        this.initializeConfiguration();
        this.recipesMap = new LinkedHashMap<>();
    }

    private void initializeConfiguration() {
        super.saveDefaultConfig();
        new ConfigurationUtils(this, "data-resource", "recipes", "block-surface");
    }

    private void onEnableMessage() {
        // change on loaded message here
        String message = String.format("&7[&a+&7] 「%s」loaded, version: &6%s", PluginInformation.PLUGIN_NAME, PluginInformation.PLUGIN_VERSION);
        getSLF4JLogger().info(ChatColor.translateAlternateColorCodes('&', message));
    }

    private void checkDepend() {
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") == null) {
            getLogger().log(Level.WARNING, "Could not find ItemsAdder! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void registerListeners() {
        new Reflections(packageName + ".listeners").getSubTypesOf(Listener.class).forEach(v -> {
            try {
                if (v.getDeclaredConstructor().getParameterCount() == 0) {
                    Listener listener = v.getDeclaredConstructor().newInstance();
                    Bukkit.getServer().getPluginManager().registerEvents(listener, this);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getPluginManager().disablePlugin(this);
            }
        });
    }

    private void loadRecipes() {
        new BukkitRunnable() {
            @Override
            public void run() {
                RecipesConfigurationLoader.RecipesConfigurationLoaderBuilder configurationLoaderBuilder = RecipesConfigurationLoader.builder();
                RecipesConfigurationLoader recipesConfigurationLoader = configurationLoaderBuilder.roundingMode(RoundingMode.HALF_UP).roundingPattern("#.##").build();
                recipesConfigurationLoader.load();
            }
        }.run();
    }

    private void processFerment() {
        getServer().getWorlds().forEach(world -> {
            final Boolean[] shouldUpdate = {false};
            new BukkitRunnable() {
                @Override
                public void run() {
                    long time = world.getTime();
                    if (900L < time && time < 1100L) {
                        if (shouldUpdate[0]) {
                            // do something here
                            Bukkit.getServer().getPluginManager().callEvent(new BrewDataProcessEvent(world, world.getWorldFolder()));
                            shouldUpdate[0] = false;
                        }
                    } else {
                        shouldUpdate[0] = true;
                    }
                }
            }.runTaskTimerAsynchronously(this, 0L, 40L);
        });
    }

    private void setWorldMap() {
        worldMap.clear();
        getServer().getWorlds().forEach(world -> worldMap.put(world.getName(), world));
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    public void update() {
        Bukkit.getScheduler().cancelTasks(this);
        processFerment();
        setWorldMap();
    }

    public Brewing recipesMap(String key, RecipesItem recipesItem) {
        this.recipesMap.put(key, recipesItem);
        return this;
    }

    public Map<String, RecipesItem> recipesMap() {
        return this.recipesMap;
    }
}
