package homeward.plugin.brewing.loaders;

import com.google.gson.*;
import homeward.plugin.brewing.Main;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RecipesLevelLoader {
    private final Main plugin;
    private final LevelDefaultLoader levelDefaultLoader;

    private RecipesLevelLoader() {
        plugin = Main.getInstance();
        levelDefaultLoader = new LevelDefaultLoader();
    }

    public boolean load() {
        if (plugin.recipesLevelMap().size() != 0) plugin.recipesLevelMap().clear();
        String recipeLevelString = plugin.getConfig().getString("recipe-level");
        if (recipeLevelString == null) {
            plugin.getSLF4JLogger().error("Brewing plugin cannot working without recipe level. This plugin will be disable");
            plugin.disable();
            return false;
        }
        JsonArray recipeLevelElements = new Gson().fromJson(recipeLevelString.replaceAll("=", ":"), JsonArray.class);

        recipeLevelElements.forEach(jsonElement -> {
            JsonObject recipeLevelObject = jsonElement.getAsJsonObject();

            JsonElement levelElement = recipeLevelObject.get("level");
            if (levelElement == null || levelElement instanceof JsonNull) {
                String message = String.format("The level of the array from %s in config.yml is empty", "recipe-level");
                plugin.getSLF4JLogger().warn(message);
                return;
            }

            JsonElement itemElement = recipeLevelObject.get("item");
            if (itemElement == null || itemElement instanceof JsonNull) {
                String message = String.format("The item of the array from %s in config.yml is empty", "recipe-level");
                plugin.getSLF4JLogger().warn(message);
                return;
            }

            JsonObject itemObject = itemElement.getAsJsonObject();
            JsonElement providerElement = itemObject.get("provider"); // nullable

            if (providerElement == null || providerElement instanceof JsonNull || providerElement.getAsString().equals("default")) { // default
                ItemStack levelIcon = levelDefaultLoader.load(itemObject);
                if (levelIcon == null) return;
                plugin.recipesLevelMap(levelElement.getAsString(), levelIcon);
                return;
            }
        });

        System.out.println(plugin.recipesLevelMap());

        if (plugin.recipesLevelMap().size() < 1) {
            plugin.getSLF4JLogger().error("Brewing plugin cannot working without recipe level. This plugin will be disable");
            plugin.disable();
            return false;
        }
        return true;
    }

    private static class LoaderInstance {
        private static final RecipesLevelLoader instance = new RecipesLevelLoader();
    }

    public static RecipesLevelLoader getInstance() {
        return LoaderInstance.instance;
    }

    public static final Map<String, Integer> levelMap = new LinkedHashMap<>();

    static {
        levelMap.put("default", 114500);
    }
}
