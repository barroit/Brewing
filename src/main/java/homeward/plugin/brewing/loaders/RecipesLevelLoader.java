package homeward.plugin.brewing.loaders;

import com.google.gson.*;
import homeward.plugin.brewing.Main;
import homeward.plugin.brewing.enumerates.EnumBase;
import homeward.plugin.brewing.enumerates.ErrorEnum;
import homeward.plugin.brewing.loaders.RecipesLevelLoader.Constants.Fields;
import homeward.plugin.brewing.utilities.BrewingUtils;
import lombok.experimental.FieldNameConstants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RecipesLevelLoader {
    public boolean loadLevelList() {
        if (plugin.recipesLevelMap().size() != 0) plugin.recipesLevelMap().clear();

        String recipeLevelString = plugin.getConfig().getString(Fields.recipe_level, null);
        if (recipeLevelString == null) {
            plugin.getSLF4JLogger().error("Brewing plugin cannot working without recipe level. This plugin will be disable");
            plugin.disable();
            return false;
        }

        JsonArray recipeLevelElements = new Gson().fromJson(recipeLevelString.replaceAll("=([^{}\\[\\]]+?),", "=\"$1\","), JsonArray.class);
        AtomicInteger atomicInteger = new AtomicInteger(0);

        recipeLevelElements.forEach(jsonElement -> {
            JsonObject recipeLevelObject = jsonElement.getAsJsonObject();

            JsonElement levelElement = recipeLevelObject.get(Fields.level);
            String levelPath = BrewingUtils.getPath(Fields.recipe_level + '[' + atomicInteger.get() + ']', Fields.level);
            if (levelElement == null || levelElement instanceof JsonNull) {
                noKeyFoundWarning(levelPath);
                return;
            }

            JsonElement itemElement = recipeLevelObject.get(Fields.item);
            String itemPath = BrewingUtils.getPath(Fields.recipe_level + '[' + atomicInteger.get() + ']', Fields.item);
            if (itemElement == null || itemElement instanceof JsonNull) {
                noKeyFoundWarning(itemPath);
                return;
            }
            JsonObject itemObject = itemElement.getAsJsonObject();

            JsonElement providerElement = itemObject.get(Fields.provider); // nullable
            String providerPath = BrewingUtils.getPath(itemPath, Fields.provider);

            ItemStack levelIcon;

            if (providerElement == null || providerElement instanceof JsonNull || providerElement.getAsString().equals(Fields.level_default)) { // default
                JsonElement idElement = itemObject.get(Fields.id);
                String itemIdPath = BrewingUtils.getPath(itemPath, Fields.id);
                if (idElement == null || idElement instanceof JsonNull) {
                    noKeyFoundWarning(itemIdPath);
                    return;
                }
                String id = idElement.getAsString();
                if (!defaultLevelMap.containsKey(id)) {
                    valueIncorrectWarning(itemIdPath, id, ErrorEnum.VALUE_INCORRECT);
                    return;
                }

                levelIcon = loadLevelList(itemObject, Material.PAPER, Material.PAPER.name(), defaultLevelMap.get(id));

            } else if (providerElement.getAsString().equalsIgnoreCase(Fields.vanilla)) {
                JsonElement materialElement = itemObject.get(Fields.material);
                String itemMaterialPath = BrewingUtils.getPath(itemPath, Fields.material);
                if (materialElement == null || materialElement instanceof JsonNull) {
                    noKeyFoundWarning(itemMaterialPath);
                    return;
                }
                Material material = Material.getMaterial(materialElement.getAsString());
                if (material == null) {
                    valueIncorrectWarning(itemMaterialPath, materialElement.getAsString(), ErrorEnum.VALUE_INCORRECT);
                    return;
                }

                JsonElement customModelDataElement = itemObject.get(Fields.custom_model_data);
                levelIcon = loadLevelList(itemObject, material, material.name(), customModelDataElement != null && BrewingUtils.isInteger(customModelDataElement.getAsString()) ? customModelDataElement.getAsInt() : 0);

            } else {
                valueIncorrectWarning(providerPath, providerElement.getAsString(), ErrorEnum.VALUE_INCORRECT);
                return;
            }

            plugin.recipesLevelMap(levelElement.getAsString(), levelIcon);
            atomicInteger.incrementAndGet();
        });

        if (plugin.recipesLevelMap().size() < 1) {
            plugin.getSLF4JLogger().error("Brewing plugin cannot working without recipe level. This plugin will be disable");
            plugin.disable();
            return false;
        }

        return true;
    }

    // region Get Item Level Showcase Icon
    private @NotNull ItemStack loadLevelList(JsonObject itemObject, Material materialType, String materialName, int customModelData) {
        ItemStack levelIcon = new ItemStack(materialType);

        levelIcon.editMeta(ItemMeta.class, meta -> {
            JsonElement displayElement = itemObject.get("display"); // nullable

            if (displayElement != null && !(displayElement instanceof JsonNull)) {
                JsonObject displayObjet = displayElement.getAsJsonObject();
                JsonElement nameElement = displayObjet.get("name");
                String name;
                if (nameElement == null || nameElement instanceof JsonNull) {
                    name = materialName.toLowerCase(Locale.ROOT);
                } else {
                    name = nameElement.getAsString();
                }

                name = ChatColor.translateAlternateColorCodes('&', name);
                TextComponent text;

                JsonElement colorElement = displayObjet.get("color");
                if (colorElement != null) {
                    Integer red = null, green = null, blue = null;

                    JsonElement redElement = colorElement.getAsJsonArray().get(0);
                    if (redElement != null && BrewingUtils.isInteger(redElement.getAsString())) {
                        red = redElement.getAsInt();
                    }
                    JsonElement greenElement = colorElement.getAsJsonArray().get(1);
                    if (greenElement != null && BrewingUtils.isInteger(greenElement.getAsString())) {
                        green = greenElement.getAsInt();
                    }
                    JsonElement blueElement = colorElement.getAsJsonArray().get(2);
                    if (blueElement != null && BrewingUtils.isInteger(blueElement.getAsString())) {
                        blue = blueElement.getAsInt();
                    }

                    if (red != null && green != null && blue != null) {
                        text = Component.text(name, TextColor.color(red, green, blue));
                    } else {
                        text = Component.text(name);
                    }
                } else {
                    text = Component.text(name);
                }
                meta.displayName(text);
            }

            meta.setCustomModelData(customModelData);
        });
        return levelIcon;
    }
    // endregion

    // region Warning Utils
    private void noKeyFoundWarning(String path) {
        BrewingUtils.noKeyFoundWarning(path, Fields.config_yml);
    }

    private void valueIncorrectWarning(String path, Object value, EnumBase errorEnum) {
        BrewingUtils.valueIncorrectWarning(path, value, Fields.config_yml, errorEnum);
    }
    // endregion

    // region Plugin Instance
    private final Main plugin;

    private RecipesLevelLoader() {
        plugin = Main.getInstance();
    }
    // endregion

    // region Default Level Map
    public final Map<String, Integer> defaultLevelMap = new LinkedHashMap<>();

    {
        defaultLevelMap.put("default", 114500);
        defaultLevelMap.put("first", 114501);
        defaultLevelMap.put("second", 114502);
        defaultLevelMap.put("third", 114503);
        defaultLevelMap.put("fourth", 114504);
    }
    // endregion

    // region Get Class Instance
    private static class LoaderInstance {
        private static final RecipesLevelLoader instance = new RecipesLevelLoader();
    }

    public static RecipesLevelLoader getInstance() {
        return LoaderInstance.instance;
    }
    // endregion

    // region Constants
    @FieldNameConstants
    @SuppressWarnings("unused")
    public static class Constants {
        String level;
        String item;
        String vanilla;
        String provider;
        String material;
        String id;

        public static final class Fields {
            public static final String level_default = "default";
            public static final String custom_model_data = "custom-model-data";
            public static final String config_yml = "config.yml";
            public static final String recipe_level = "recipe-level";
        }
    }
    // endregion
}
