package homeward.plugin.brewing;

import homeward.plugin.brewing.loader.ConfigurationLoader;
import homeward.plugin.brewing.registrant.CommandRegister;
import homeward.plugin.brewing.registrant.ListenerRegister;
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
        ConfigurationLoader.getInstance().reload();

        CommandRegister.getInstance().register();
        ListenerRegister.getInstance().register();
    }

    public Main() {
        plugin = this;
    }

    public static Main getInstance() {
        return plugin;
    }
}
