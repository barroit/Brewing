package minecraft.homeward.homewardpluginbrewing;

import me.mattstudios.mf.base.CommandManager;
import minecraft.homeward.homewardpluginbrewing.commands.MainCommand;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class HomewardPluginBrewing extends JavaPlugin {

    //常量部分
    public final String PLUGIN_NAME = "Homeward Brewing";
    public final String VERSION = "1.0.2";

    //全局plugin 无需再次初始化但是必须在插件注册前
    private static HomewardPluginBrewing plugin;
    private static CommandManager commandManager;

    //覆写默认构造器
    public HomewardPluginBrewing() {
        plugin = this;
    }

    //储存配置文件类
    public static FileConfiguration config;

    @Override
    public void onEnable() {

        //注册默认Config,没有的话创建一个
        saveDefaultConfig();
        config = getConfig();

        //注册插件指令中心
        //来源 Matt's Framework https://mf.mattstudios.me/mf/mf-1/getting-started
        CommandManager commandManager = new CommandManager(this);
        HomewardPluginBrewing.commandManager = commandManager; //same with this

        // Registering multiple 注册指令
        commandManager.register(new MainCommand());
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "&7[&a+&7] 插件加载成功 版本" + "&6" + VERSION));

    }

    /**
     * 关闭逻辑
     */
    @Override
    public void onDisable() {

    }

    public static HomewardPluginBrewing getPlugin() {
        return plugin;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

}
