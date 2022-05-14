package homeward.plugin.brewing.constants;

import homeward.plugin.brewing.Brewing;
import lombok.experimental.FieldNameConstants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;


public class PluginInformation {
    public final static String PLUGIN_NAME = "Homeward Brewing";
    public final static String PLUGIN_VERSION = "1.1.0";
    public final static String COMMAND_PACKAGE_NAME = "commands";
    public final static String LISTENER_PACKAGE_NAME = "listeners";

    public final static String PLUGIN_PATH;
    public final static Set<Integer> BARREL_DESCRIPTION_CUSTOM_MODEL_DATA_LIST;
    public final static Set<Integer> BARREL_DESCRIPTION_MANIPULATIVE_LIST;

    static {
        String var10000 = Brewing.getInstance().getDataFolder().toString();
        PLUGIN_PATH = var10000.replaceAll("\\\\", "/") + "/";
        BARREL_DESCRIPTION_CUSTOM_MODEL_DATA_LIST = new LinkedHashSet<>(Arrays.asList(4500, 4501, 4502, 4503, 4504, 4505, 4506, 4507, 4508, 4509));
        BARREL_DESCRIPTION_MANIPULATIVE_LIST = new LinkedHashSet<>(Arrays.asList(2, 11, 20));
    }
}
