package homeward.plugin.brewing.utils;

import homeward.plugin.brewing.Brewing;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Baioretto
 * @version 1.0.0
 */
public class ConfigurationUtils {
    private static Map<String, File> fileMap;
    private static Map<String, FileConfiguration> fileConfigurationMap;


    /**
     * 获取指定文件key list
     *
     * @param fileName file name
     * @return key set
     */
    public static Set<String> getKeys(String fileName) {
        fileName = generateName(fileName);
        return fileConfigurationMap.containsKey(fileName) ? fileConfigurationMap.get(fileName).getKeys(false) : null;
    }

    /**
     * 获取指定文件key list
     *
     * @param fileName file name
     * @param deep Whether or not to get a deep list, as opposed to a shallow list
     * @return key set
     */
    public static Set<String> getKeys(String fileName, boolean deep) {
        fileName = generateName(fileName);
        return fileConfigurationMap.containsKey(fileName) ? fileConfigurationMap.get(fileName).getKeys(deep) : null;
    }

    /**
     *
     * Returns the instance of configuration from the given name
     *
     * @param configName config name
     * @return FileConfiguration
     */
    public static FileConfiguration get(String configName) {
        configName = configName.replaceAll("([\\w-]*)\\.yml|yaml$", "$1") + ".yml";
        utils.checkConfigExist(configName);
        return fileConfigurationMap.get(configName);
    }

    /**
     *
     * Saves the config information to the local disk from the given name
     *
     * @param configName config name
     */
    public static void save(String configName) {
        configName = configName.replaceAll("([\\w-]*)\\.yml|yaml$", "$1") + ".yml";
        utils.checkConfigExist(configName);
        FileConfiguration configurationFile = fileConfigurationMap.get(configName);
        utils.checkFileExist(configName);
        try {
            configurationFile.save(fileMap.get(configName));
        } catch (IOException e) {
            System.out.println("Could not save file");
        }
    }

    /**
     *
     * Reload all configurations from your local disk and load them into memory
     *
     */
    public static void reload() {
        fileConfigurationMap.replaceAll(utils::reloadInner);
    }

    protected static class utils {
        public static void checkConfigExist(String configName) {
            if (!fileConfigurationMap.containsKey(configName)) {
                throw new RuntimeException("You need provide an exist config name");
            }
        }
        public static void checkFileExist(String fileName) {
            if (!fileMap.containsKey(fileName)) {
                throw new RuntimeException("You need provide an exist config name");
            }
        }
        public static FileConfiguration reloadInner(String k, FileConfiguration v) {
            return YamlConfiguration.loadConfiguration(fileMap.get(k));
        }
    }

    private void setValue(Brewing plugin, String ...configname) {
        Set<String> configurationList = new HashSet<>(Arrays.stream(configname).map(v ->
                v.replaceAll("([\\w-]*)\\.yml|yaml$", "$1") + ".yml").toList());

        configurationList.forEach(v -> {

            File file = new File(plugin.getDataFolder(), v);

            if (!file.exists()) plugin.saveResource(v, false);

            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

            fileConfigurationMap.put(v, configuration);
            fileMap.put(v, file);
        });
    }

    private static String generateName(String fileName) {
        return (fileName == null || fileName.isBlank()) ? null : fileName.replaceAll("([\\w-]*)\\.yml|yaml$", "$1") + ".yml";
    }

    /**
     *
     * Initialize configuration files
     * <h3>Must and should only be called on plugin initialing</h3>
     *
     * @param plugin the plugin instance
     * @param configname your config name without extension
     */
    public ConfigurationUtils(@NotNull Brewing plugin, @NotNull String ...configname) {
        fileMap = new HashMap<>();
        fileConfigurationMap = new HashMap<>();
        setValue(plugin, configname);
    }
}
