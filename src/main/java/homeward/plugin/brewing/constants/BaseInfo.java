package homeward.plugin.brewing.constants;

import homeward.plugin.brewing.Brewing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BaseInfo {
    public final static String PLUGIN_NAME = "Homeward Brewing";
    public final static String PLUGIN_VERSION = "1.1.0";
    public final static String PLUGIN_PATH = Brewing.getInstance().getDataFolder().toString().replaceAll("\\\\", "/") + "/";
    public final static Set<Integer> BARREL_DESCRIPTION_CUSTOM_MODEL_DATA_LIST = new HashSet<>(Arrays.asList(4500, 4501, 4502, 4503, 4504, 4505, 4506, 4507, 4508, 4509));
    public final static Set<Integer> BARREL_DESCRIPTION_MANIPULATIVE_LIST = new HashSet<>(Arrays.asList(2, 11, 20));
}
