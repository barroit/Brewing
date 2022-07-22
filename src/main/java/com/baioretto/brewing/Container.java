package com.baioretto.brewing;

import com.baioretto.brewing.bean.ItemProperties;
import com.baioretto.brewing.bean.OpenedBarrel;
import com.baioretto.brewing.bean.RecipeProperties;
import com.baioretto.brewing.enumerate.Type;
import com.baioretto.brewing.gui.RecipesDetailGui;
import com.baioretto.brewing.gui.RecipesPreviewGui;
import com.google.common.collect.Maps;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.WeakHashMap;

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
     * Recipe gui display item mapping.
     *
     * <p><pre>
     *  String  -----  (String  -----  ItemStack)
     *    |               |                |
     *  level         outputId       outputItemStack
     *  </pre>
     */
    public static final Map<String, Map<String, ItemStack>> RECIPE_DISPLAY_ITEMS = Maps.newHashMap();

    /**
     * Stores tier, substrate, yeast, output, container.
     *
     * <p><pre>
     *  Type  -----  (String  -----  ItemStack)
     *    |             |                |
     *  itemType      itemId          itemStack
     *  </pre>
     */
    public static final Map<Type, Map<String, ItemStack>> ITEM_STACK_MAP = Maps.newHashMap();

    /**
     * mapping of player and gui
     */
    public static final Map<HumanEntity, RecipesPreviewGui> RECIPE_PREVIEW_GUI = Maps.newHashMap();

    /**
     * mapping of player and gui
     */
    public static final Map<HumanEntity, RecipesDetailGui> RECIPE_DETAIL_GUI = Maps.newHashMap();

    /**
     * location - openedBarrel mapping
     */
    public static final WeakHashMap<Location, OpenedBarrel> OPENED_BARREL = new WeakHashMap<>(); // fix

    private Container() {
        throw new UnsupportedOperationException();
    }
}