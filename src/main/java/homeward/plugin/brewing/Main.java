package homeward.plugin.brewing;

import com.google.common.collect.Maps;
import homeward.plugin.brewing.beans.ItemProperties;
import homeward.plugin.brewing.loaders.ConfigurationLoader;
import homeward.plugin.brewing.registrants.CommandRegister;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@Accessors(fluent = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
public final class Main extends JavaPlugin {
    private static boolean disable = false;
    @Getter private static final Map<String, ItemProperties.Content> itemTier = Maps.newLinkedHashMap();
    @Getter private static final Map<String, ItemProperties> tierItemPropertiesMap = Maps.newLinkedHashMap();
    @Getter private static final Map<String, ItemProperties> substrateItemPropertiesMap = Maps.newLinkedHashMap();
    @Getter private static final Map<String, ItemProperties> yeastItemPropertiesMap = Maps.newLinkedHashMap();
    @Getter private static final Map<String, ItemProperties> outputItemPropertiesMap = Maps.newLinkedHashMap();

    private static Main plugin;

    @Override
    public void onEnable() {
        ConfigurationLoader.getInstance().reload();

        CommandRegister.getInstance().register();
        // ListenerRegister.getInstance().register();

        if (disable) {
            Bukkit.getPluginManager().disablePlugin(Main.getInstance());
        }
    }

    public Main() {
        plugin = this;
    }

    public static void tierItemPropertiesMap(String id, ItemProperties itemProperties) {
        tierItemPropertiesMap.put(id, itemProperties);
    }

    public static void substrateItemPropertiesMap(String id, ItemProperties itemProperties) {
        substrateItemPropertiesMap.put(id, itemProperties);
    }

    public static void yeastItemPropertiesMap(String id, ItemProperties itemProperties) {
        yeastItemPropertiesMap.put(id, itemProperties);
    }

    public static void outputItemPropertiesMap(String id, ItemProperties itemProperties) {
        outputItemPropertiesMap.put(id, itemProperties);
    }

    public static void itemTier(String id, ItemProperties.Content content) {
        itemTier.put(id, content);
    }

    public static Main getInstance() {
        return plugin;
    }

    public static void disable() {
        disable = true;
    }
}
