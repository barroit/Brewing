package homeward.plugin.brewing;

import homeward.plugin.brewing.commands.MainCommand;
import homeward.plugin.brewing.constants.BaseInfo;
import homeward.plugin.brewing.events.BrewDataProcessEvent;
import homeward.plugin.brewing.utils.ConfigurationUtils;
import me.mattstudios.mf.base.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.reflections.Reflections;

import java.util.logging.Level;

public final class Brewing extends JavaPlugin {
    private static Brewing plugin;
    private final CommandSender commandSender;
    private final String packageName;

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

        this.registerListeners();

        this.processFerment();

        this.onEnableMessage();
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

    public void update() {
        Bukkit.getScheduler().cancelTasks(this);
        processFerment();
    }
}
