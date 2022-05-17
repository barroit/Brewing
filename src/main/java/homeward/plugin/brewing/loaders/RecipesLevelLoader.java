package homeward.plugin.brewing.loaders;

import homeward.plugin.brewing.Main;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RecipesLevelLoader {
    private final Main plugin;

    private RecipesLevelLoader() {
        plugin = Main.getInstance();
    }

    public boolean load() {
        if (plugin.recipesLevelMap().size() != 0) plugin.recipesLevelMap().clear();
        List<Map<?, ?>> recipeLevelListMap = plugin.getConfig().getMapList(RECIPE_LEVEL);
        if (recipeLevelListMap.size() < 1) {
            plugin.getSLF4JLogger().error("Brewing plugin cannot working without recipe level. This plugin will be disable");
            plugin.disable();
            return false;
        }

        AtomicInteger count = new AtomicInteger(1);
        recipeLevelListMap.forEach(map -> setRecipesLevelMap(map, count));
        if (plugin.recipesLevelMap().size() < 1) {
            plugin.getSLF4JLogger().error("Brewing plugin cannot working without recipe level. This plugin will be disable");
            plugin.disable();
            return false;
        }
        return true;
    }

    void setRecipesLevelMap(final Map<?, ?> map, AtomicInteger countdown) {
        Object level = map.get(LEVEL);
        int count = countdown.getAndIncrement();
        if (level == null || StringUtils.isBlank(level.toString())) {
            String message = String.format("The level of the array index %d from %s in config.yml is empty", count - 1, RECIPE_LEVEL);
            plugin.getSLF4JLogger().warn(message);
            return;
        }
        Object displayName = map.get(DISPLAY_NAME);
        if (displayName == null || StringUtils.isBlank(displayName.toString())) {
            String message = String.format("The display name of the array index %d from %s in config.yml is empty", count - 1, RECIPE_LEVEL);
            plugin.getSLF4JLogger().warn(message);
            return;
        }
        if (plugin.recipesLevelMap().size() > 3) return;
        plugin.recipesLevelMap(level.toString(), displayName.toString());
    }

    private static class LoaderInstance {
        private static final RecipesLevelLoader instance = new RecipesLevelLoader();
    }

    public static RecipesLevelLoader getInstance() {
        return LoaderInstance.instance;
    }

    public static final String RECIPE_LEVEL = "recipe-level";
    public static final String LEVEL = "level";
    public static final String DISPLAY_NAME = "display-name";

}
