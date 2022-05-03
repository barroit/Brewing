package homeward.plugin.brewing.constants;

import homeward.plugin.brewing.Brewing;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class BaseInfo {
    public final static String PLUGIN_NAME = "Homeward Brewing";
    public final static String PLUGIN_VERSION = "1.1.0";
    public final static String PLUGIN_PATH = Brewing.getInstance().getDataFolder().toString().replaceAll("\\\\", "/") + "/";
    public final static Component BARREL_TITLE = Component.text( "\uF808").append(Component.text("섍", NamedTextColor.WHITE));
}
