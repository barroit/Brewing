package homeward.plugin.brewing;

import com.google.common.collect.Maps;
import homeward.plugin.brewing.bean.ItemProperties;
import homeward.plugin.brewing.bean.RecipeProperties;
import homeward.plugin.brewing.enumerate.Type;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@SuppressWarnings("unused")
public final class Container {
    /**
     * item-tier mapping in config.yml
     * fill after tierLoader.loadItemTierContents();
     */
    public static final Map<String, ItemProperties.Content> ITEM_TIER = Maps.newHashMap();

    /**
     * recipe-tier mapping in config.yml
     * key is recipe-tier.level
     * value is ((ItemStack) recipe-tier.item)
     * fill after tierLoader.loadRecipeTier();
     */
    public static final Map<String, ItemStack> RECIPE_TIER = Maps.newTreeMap();

    /**
     * recipes mapping in recipes folder
     * fill after itemStackLoader.convertRecipeToItemStack();
     */
    public static final Map<String, RecipeProperties> RECIPE_PROPERTIES = Maps.newHashMap();

    /**
     * gui recipe display mapping
     */
    public static final Map<String, Map<String, ItemStack>> RECIPE_DISPLAY_ITEMS = Maps.newHashMap();

    /**
     * stores tier, substrate, yeast, output, container
     */
    public static final Map<Type, Map<String, ItemStack>> ITEM_STACK_MAP = Maps.newHashMap();

    private Container() {
        throw new UnsupportedOperationException();
    }
}
