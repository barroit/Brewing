package homeward.plugin.brewing.commands;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import homeward.plugin.brewing.Brewing;
import homeward.plugin.brewing.utils.ConfigurationUtils;
import lombok.Getter;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.LinkedHashSet;
import java.util.Set;

@Command("mock")
@Alias("m")
public class MockFileConfigurationTest extends CommandBase {
    private final FileConfiguration recipesFileConfiguration;
    @Getter private final Set<Object> substrateSets = new LinkedHashSet<>();

    public MockFileConfigurationTest() {
        recipesFileConfiguration = ConfigurationUtils.get("recipes");
    }

    @SubCommand("substrate")
    public void substrateSection(CommandSender commandSender) {
        Set<String> recipeKeys = recipesFileConfiguration.getKeys(false);
        recipeKeys.forEach(this::keysAction);

        substrateSets.forEach(System.out::println);
    }

    private void keysAction(final String key) {
        ConfigurationSection objectSection = recipesFileConfiguration.getConfigurationSection(key);
        if (objectSection == null) return;

        String substrateStringSection = objectSection.getString("substrate");
        if (substrateStringSection == null) return;

        JsonArray substratesArray = new Gson().fromJson(substrateStringSection.replaceAll("=", ":"), JsonArray.class);
        substratesArray.forEach(this::initSubstrateSection);
    }

    private void initSubstrateSection(final JsonElement jsonElement) {
        JsonObject substrateSection = (JsonObject) jsonElement;
        String provider = substrateSection.get("from").getAsString();
        JsonObject item = substrateSection.get("item").getAsJsonObject();

        if ("Vanilla".equalsIgnoreCase(provider)) {
            ItemStack vanillaItem = initVanillaItem(item);
            substrateSets.add(vanillaItem);
        } else if ("ItemsAdder".equalsIgnoreCase(provider)) {

        } else if ("MMOItems".equalsIgnoreCase(provider)) {

        }
    }



    // region 初始化原版物品
    private ItemStack initVanillaItem(final JsonObject item) {
        JsonElement materialElement = item.get("material");

        if (materialElement == null) {
            Brewing.getInstance().getSLF4JLogger().warn("could not load material in recipes.yml " + item);
            return null;
        }

        Material material;
        try {
            material = Material.valueOf(materialElement.getAsString());
        } catch (IllegalArgumentException e) {
            Brewing.getInstance().getSLF4JLogger().warn("could not load material in recipes.yml " + item + "\n" + "make sure the value is correct");
            return null;
        }

        ItemStack itemStack = new ItemStack(material);

        // 给予potion种类
        if (Material.POTION.equals(material)) {
            PotionType potionType;
            JsonElement potionTypeElement = item.get("potion-type");
            if (potionTypeElement == null) {
                Brewing.getInstance().getSLF4JLogger().warn("could not load potion-type in recipes.yml " + item);
                return null;
            }

            try {
                potionType = PotionType.valueOf(potionTypeElement.getAsString());
            } catch (IllegalArgumentException e) {
                Brewing.getInstance().getSLF4JLogger().warn("could not load potion-type in recipes.yml " + item + "\n" + "make sure the value is correct");
                return null;
            }

            itemStack.editMeta(PotionMeta.class, meta -> meta.setBasePotionData( new PotionData(potionType)));
        }

        return itemStack;
    }
    // endregion
}
