package homeward.plugin.brewing;

import homeward.plugin.brewing.loader.ConfigurationLoader;
import lombok.AccessLevel;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.java.JavaPlugin;

@Accessors(fluent = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class Main extends JavaPlugin {
    private static Main plugin;

    @Override
    public void onEnable() {
        ConfigurationLoader.reload();
        Register.registerCommand();
    }

    public Main() {
        plugin = this;
    }

    public static Main getInstance() {
        return plugin;
    }
}
