package homeward.plugin.brewing;

import homeward.plugin.brewing.beans.Recipe;
import homeward.plugin.brewing.commands.CommandRegister;
import homeward.plugin.brewing.configurations.RecipesLevelLoader;
import homeward.plugin.brewing.constants.PluginInformation;
import homeward.plugin.brewing.events.BrewDataProcessEvent;
import homeward.plugin.brewing.listeners.ListenerRegister;
import homeward.plugin.brewing.utils.ConfigurationUtils;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;

@Accessors(fluent = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
public class Brewing extends JavaPlugin {
    private static Brewing plugin;
    @Getter(lazy = true) private final static String packageName = Brewing.class.getPackageName();
    Map<String, World> worldMap;
    Map<String, Recipe> recipesMap;
    Map<String, String> recipesLevelMap;

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
        if (!RecipesLevelLoader.getInstance().load()) {
            return;
        }

        ListenerRegister.getInstance().register(); // register listeners
        CommandRegister.getInstance().register(); // register commands

        cycleAction();
        setWorldMap();
        onEnableMessage();
    }

    public Brewing() {
        this.checkDepend();
        plugin = this;
        this.initializeConfiguration();
        this.recipesMap = new LinkedHashMap<>();
        this.recipesLevelMap = new TreeMap<>();
        this.worldMap = new LinkedHashMap<>();
    }

    // region Disable this plugin
    public void disable() {
        Bukkit.getPluginManager().disablePlugin(this);
    }
    // endregion

    // region Initialize Configuration
    private void initializeConfiguration() {
        saveDefaultConfig();
        getConfig().options().copyDefaults();
        new ConfigurationUtils(this, "recipes", "block-surface");
    }
    // endregion

    // region On Enable Message
    private void onEnableMessage() {
        // change on loaded message here
        String message = String.format("&7[&a+&7] 「%s」loaded, version: &6%s", PluginInformation.PLUGIN_NAME, PluginInformation.PLUGIN_VERSION);
        getSLF4JLogger().info(ChatColor.translateAlternateColorCodes('&', message));
    }
    // endregion

    // region Check Depend
    private void checkDepend() {
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") == null) {
            getLogger().log(Level.WARNING, "Could not find ItemsAdder! This plugin is required.");
            disable();
        }
    }
    // endregion

    // region Cycle Action
    private void cycleAction() {
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
    // endregion

    // region On Plugin Disabled
    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this); // Close asynchronous tasks
    }
    // endregion

    // region Update
    /**
     * Restart Cycle Actions
     */
    public void update() {
        Bukkit.getScheduler().cancelTasks(this);

        setWorldMap();
    }
    // endregion

    // region Set recipes map
    public void recipesMap(String key, Recipe recipe) {
        this.recipesMap.put(key, recipe);
    }
    // endregion

    // region Set recipes level map
    public void recipesLevelMap(String level, String display) {
        this.recipesLevelMap.put(level, display);
    }
    // endregion

    // region See how many worlds in this server
    private void setWorldMap() {
        worldMap.clear();
        getServer().getWorlds().forEach(world -> worldMap.put(world.getName(), world));
    }
    // endregion
}
