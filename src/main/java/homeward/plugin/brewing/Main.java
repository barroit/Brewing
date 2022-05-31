package homeward.plugin.brewing;

import com.google.common.collect.Maps;
import homeward.plugin.brewing.bean.ItemProperties;
import homeward.plugin.brewing.bean.RecipeProperties;
import homeward.plugin.brewing.loader.ConfigurationLoader;
import homeward.plugin.brewing.registrant.CommandRegister;
import homeward.plugin.brewing.registrant.ListenerRegister;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@Accessors(fluent = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class Main extends JavaPlugin {
    @Getter private static final Map<String, ItemProperties.Content> itemTier = Maps.newLinkedHashMap();
    @Getter private static final Map<String, ItemStack> recipeTier = Maps.newTreeMap();

    @Getter private static final Map<String, RecipeProperties> recipes = Maps.newLinkedHashMap();
    @Getter private static final Map<String, ItemStack> recipeItemStackMap = Maps.newLinkedHashMap();

    @Getter private static final Map<String, ItemStack> tierItemStackMap = Maps.newLinkedHashMap();
    @Getter private static final Map<String, ItemStack> substrateItemStackMap = Maps.newLinkedHashMap();
    @Getter private static final Map<String, ItemStack> yeastItemStackMap = Maps.newLinkedHashMap();
    @Getter private static final Map<String, ItemStack> outputItemStackMap = Maps.newLinkedHashMap();

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

    public static void tierItemStackMap(String id, ItemStack itemStack) {
        tierItemStackMap.put(id, itemStack);
    }

    public static void substrateItemStackMap(String id, ItemStack itemStack) {
        substrateItemStackMap.put(id, itemStack);
    }

    public static void yeastItemStackMap(String id, ItemStack itemStack) {
        yeastItemStackMap.put(id, itemStack);
    }

    public static void outputItemStackMap(String id, ItemStack itemStack) {
        outputItemStackMap.put(id, itemStack);
    }

    public static void itemTier(String id, ItemProperties.Content content) {
        itemTier.put(id, content);
    }

    public static void recipeTier(String level, ItemStack item) {
        recipeTier.put(level, item);
    }

    public static void recipes(String id, RecipeProperties recipeProperties) {
        recipes.put(id, recipeProperties);
    }

    public static ItemProperties.Content itemTier(String id) {
        return itemTier.get(id);
    }

    public static void clearItemTier() {
        itemTier.clear();
    }

    public static void clearRecipeTier() {
        recipeTier.clear();
    }

    public static void clearRecipes() {
        recipes.clear();
    }

    public static void clearItemStackMap() {
        tierItemStackMap.clear();
        substrateItemStackMap.clear();
        yeastItemStackMap.clear();
        outputItemStackMap.clear();
    }

    public static Main getInstance() {
        return plugin;
    }
}
