package homeward.plugin.brewing;

import homeward.plugin.brewing.loaders.RecipesLevelLoader;
import homeward.plugin.brewing.registrants.CommandRegister;
import homeward.plugin.brewing.registrants.ListenerRegister;
import homeward.plugin.brewing.utilities.ConfigurationUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.TreeMap;

@Accessors(fluent = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
public final class Main extends JavaPlugin {
    private static Main plugin;
    @Getter(lazy = true) private final static String packageName = Main.class.getPackageName();
    Map<String, ItemStack> recipesLevelMap;

    @Override
    public void onEnable() {
        // 我需要把配置文件的读取和存储写好





        // if (!RecipesLevelLoader.getInstance().loadLevelList()) {
        //     return;
        // }

        // CommandRegister.getInstance().register();
        // ListenerRegister.getInstance().register();
    }

    public Main() {
        plugin = this;
        // this.initializeConfiguration();
        this.recipesLevelMap = new TreeMap<>();
    }

    public static Main getInstance() {
        return plugin;
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
        new ConfigurationUtils(this, "recipes");
    }
    // endregion

    // region Set recipes level map
    public void recipesLevelMap(String level, ItemStack icon) {
        this.recipesLevelMap.put(level, icon);
    }
    // endregion
}
