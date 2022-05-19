package homeward.plugin.brewing.loaders;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import homeward.plugin.brewing.Main;
import homeward.plugin.brewing.utilities.BrewingUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LevelDefaultLoader {
    private final Main plugin;

    LevelDefaultLoader() {
        plugin = Main.getInstance();
    }

    public ItemStack load(JsonObject itemObject) {
        JsonElement idElement = itemObject.get("id");
        if (idElement == null || idElement instanceof JsonNull) {
            String message = String.format("The item %s of the array from %s in config.yml is empty", "id", "recipe-level");
            plugin.getSLF4JLogger().warn(message);
            return null;
        }

        String id = idElement.getAsString();
        if (!RecipesLevelLoader.levelMap.containsKey(id)) {
            String message = String.format("The item %s of the array from %s in config.yml is incorrect", "id", "recipe-level");
            plugin.getSLF4JLogger().warn(message);
            return null;
        }

        ItemStack levelIcon = new ItemStack(Material.PAPER);

        JsonElement displayElement = itemObject.get("display"); // nullable

        levelIcon.editMeta(ItemMeta.class, meta -> {
            if (displayElement == null || displayElement instanceof JsonNull) {
                meta.displayName(Component.text(id));
            } else {
                JsonObject displayObjet = displayElement.getAsJsonObject();
                JsonElement nameElement = displayObjet.get("name");
                String name;
                if (nameElement == null || nameElement instanceof JsonNull) {
                    name = id;
                } else {
                    name = nameElement.getAsString();
                }

                TextComponent text;

                JsonElement colorElement = displayObjet.get("color");
                if (colorElement != null) {
                    Integer red = null;
                    Integer green = null;
                    Integer blue = null;

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
                        text = Component.text(ChatColor.translateAlternateColorCodes('&', name));
                    }
                } else {
                    text = Component.text(ChatColor.translateAlternateColorCodes('&', name));
                }
                meta.displayName(text);
            }
            meta.setCustomModelData(RecipesLevelLoader.levelMap.get(id));
        });
        return levelIcon;
    }
}
