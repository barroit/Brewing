package homeward.plugin.brewing;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import homeward.plugin.brewing.commands.MainCommand;
import homeward.plugin.brewing.constants.BaseInfo;
import homeward.plugin.brewing.events.BrewDataProcessEvent;
import homeward.plugin.brewing.utils.ConfigurationUtils;
import lombok.Getter;
import me.mattstudios.mf.base.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.reflections.Reflections;

import java.util.*;
import java.util.logging.Level;

public final class Brewing extends JavaPlugin {
    private static Brewing plugin;
    private final CommandSender commandSender;
    private final String packageName;
    @Getter private final static Map<String, World> worldMap = new LinkedHashMap<>();
    @Getter private static final Map<String, JsonObject> configurationMap = new LinkedHashMap<>();
    @Getter private static final Map<String, JsonObject> recipes = new LinkedHashMap<>();

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
        CommandManager commandManager = new CommandManager(this);
        // register command here
        commandManager.register(new MainCommand());

        registerListeners();

        processFerment();

        setWorldMap();

        onEnableMessage();
    }

    public Brewing() {
        this.checkDepend();
        plugin = this;
        this.commandSender = super.getServer().getConsoleSender();
        this.packageName = getClass().getPackageName();
        this.initializeConfiguration();
    }

    private void initializeConfiguration() {
        super.saveDefaultConfig();
        new ConfigurationUtils(this, "data-resource", "recipes", "block-surface");
    }

    private void onEnableMessage() {
        // change on loaded message here
        String message = String.format("&7[&a+&7] 「%s」loaded, version: &6%s", BaseInfo.PLUGIN_NAME, BaseInfo.PLUGIN_VERSION);
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
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

    // private void processFerment() {
    //     BukkitRunnable runnable = new BukkitRunnable() {
    //         @Override
    //         public void run() {
    //             List<World> worlds = Bukkit.getServer().getWorlds();
    //             worlds.forEach(world -> {
    //                 if (world.getTime() == 1000) {
    //                     commandSender.sendMessage(world.getName() + "的周期+1, 当前世界时间为" + world.getTime());
    //                     //触发事件处理事件，传入世界和世界文件夹
    //                     Bukkit.getServer().getPluginManager().callEvent(new BrewDataProcessEvent(world, world.getWorldFolder()));
    //                 }
    //             });
    //         }
    //     };
    //     runnable.runTaskTimerAsynchronously(this, 0, 20);
    // }

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

    private void initializeRecipes() {
        FileConfiguration recipesFileConfiguration = ConfigurationUtils.get("recipes");
        ConfigurationSection configurationSection = recipesFileConfiguration.getConfigurationSection("wine");

        JsonElement substrate = JsonParser.parseString(configurationSection.getString("substrate").replaceAll("=", ":"));
        substrate.getAsJsonArray().forEach(System.out::println);
    }

    private void initializeConfigurationData() {
        Set<String> shallowKeys = ConfigurationUtils.getKeys("recipes");
        FileConfiguration fileConfiguration = ConfigurationUtils.get("recipes");
        if (shallowKeys == null) return;

        Gson gson = new Gson();

        shallowKeys.forEach(k -> {
            ConfigurationSection section = fileConfiguration.getConfigurationSection(k);
            if (section == null) return;
            ConfigurationSection yieldSection = section.getConfigurationSection("yield");
            if (yieldSection == null) return;

            String substrate = section.getString("substrate");
            if (substrate == null) return;
            String restriction = gson.toJson(section.getStringList("restriction"), List.class);
            if (restriction.length() == 0) return;
            String yeast = gson.toJson(section.getStringList("yeast"), List.class);
            if (yeast.length() == 0) return;
            int maxYield = yieldSection.getInt("max-yield");
            if (maxYield == 0) return;
            int minYield = yieldSection.getInt("min-yield");
            if (minYield == 0) return;
            double restrictionIndex = yieldSection.getDouble("restriction-index");
            if (restrictionIndex == 0) return;
            double yeastIndex = yieldSection.getDouble("yeast-index");
            if (yeastIndex == 0) return;
            int brewingCycle = section.getInt("brewing-cycle");
            if (brewingCycle == 0) return;
            String output = section.getString("output");
            if (output == null) return;

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("substrate", substrate);
            jsonObject.addProperty("restriction", restriction);
            jsonObject.addProperty("yeast", yeast);
            jsonObject.addProperty("maxYield", maxYield);
            jsonObject.addProperty("minYield", minYield);
            jsonObject.addProperty("restrictionIndex", restrictionIndex);
            jsonObject.addProperty("yeastIndex", yeastIndex);
            jsonObject.addProperty("brewingCycle", brewingCycle);
            jsonObject.addProperty("output", output);

            configurationMap.put(k, jsonObject);
        });
    }
}
