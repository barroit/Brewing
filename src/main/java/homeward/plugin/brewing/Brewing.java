package homeward.plugin.brewing;

import homeward.plugin.brewing.commands.MainCommand;
import homeward.plugin.brewing.constants.BaseInfo;
import homeward.plugin.brewing.utils.ConfigurationUtil;
import me.mattstudios.mf.base.CommandManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class Brewing extends JavaPlugin {
    private static Brewing plugin;
    private final CommandSender commandSender;

    /**
     *
     * Get current plugin instance
     *
     * @return plugin instance
     */
    public static Brewing getInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {
        CommandManager commandManager = new CommandManager(this);
        commandManager.register(new MainCommand());

        this.onEnableMessage();
    }

    public Brewing() {
        plugin = this;
        this.commandSender = super.getServer().getConsoleSender();
        this.initializeConfiguration();
    }

    private void initializeConfiguration() {
        super.saveDefaultConfig();
        new ConfigurationUtil(this, "data-resource", "recipes");
    }

    private void onEnableMessage() {
        String message = String.format("&7[&a+&7] 「%s」loaded, version: &6%s", BaseInfo.PLUGIN_NAME, BaseInfo.PLUGIN_VERSION);
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
