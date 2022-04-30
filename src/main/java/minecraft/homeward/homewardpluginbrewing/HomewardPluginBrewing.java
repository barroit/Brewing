package minecraft.homeward.homewardpluginbrewing;

import me.mattstudios.mf.base.CommandManager;
import minecraft.homeward.homewardpluginbrewing.commands.MainCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class HomewardPluginBrewing extends JavaPlugin {

    //常量部分
    public final String PLUGIN_NAME = "Homeward Brewing";
    public final String VERSION = "1.0.0";

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

        getLogger().log(Level.INFO, "嘻嘻");

        CommandManager commandManager = new CommandManager(this);
        this.commandManager = commandManager;


        // Registering multiple 注册指令
        commandManager.register(new MainCommand());

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
