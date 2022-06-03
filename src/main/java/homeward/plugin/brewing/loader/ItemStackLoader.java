package homeward.plugin.brewing.loader;

import com.google.common.collect.Maps;
import de.tr7zw.nbtapi.NBTItem;
import homeward.plugin.brewing.Container;
import homeward.plugin.brewing.bean.ItemProperties;
import homeward.plugin.brewing.enumerate.Type;
import homeward.plugin.brewing.enumerate.Provider;
import homeward.plugin.brewing.utilitie.BrewingUtils;
import homeward.plugin.brewing.utilitie.ConfigurationUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

// loading all item stack from item properties
class ItemStackLoader {
    private final Set<ItemProperties> itemPropertiesSet = ItemPropertiesLoader.getInstance().getItemPropertiesSet();

    // region convert recipe to item stack
    public void convertRecipeToItemStack() {
        if (Container.RECIPE_PROPERTIES.size() == 0) return;
        Container.RECIPE_PROPERTIES.forEach((id, recipeProperties) -> {
            ItemProperties.Content display = recipeProperties.display();

            ArrayList<ItemProperties.Content> lore = recipeProperties.lore(); // nullable

            ItemStack output = recipeProperties.output();
            Material material = output.getType();
            int customModelData = output.getItemMeta().getCustomModelData();

            ItemStack itemStack = new ItemStack(material);
            itemStack.editMeta(itemMeta -> {
                itemMeta.displayName(ConfigurationUtils.getDisplayComponent(display));
                itemMeta.setCustomModelData(customModelData);
                itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                if (lore != null) itemMeta.lore(ConfigurationUtils.getLoreComponent(lore, itemMeta));
            });

            String level = recipeProperties.level();
            Map<String, ItemStack> recipeMap = Container.RECIPE_DISPLAY_ITEMS.get(level);
            if (recipeMap == null) recipeMap = new TreeMap<>();
            recipeMap.put(id, itemStack);
            Container.RECIPE_DISPLAY_ITEMS.put(level, recipeMap);
        });
    }
    // endregion

    // region convert properties to item stack
    public void convertPropertiesToItemStack() {
        if (itemPropertiesSet.size() == 0) {
            return;
        }

        Container.ITEM_STACK_MAP.clear();

        itemPropertiesSet.forEach(this::categorizeItem);
    }

    // region categorize item
    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private void categorizeItem(final ItemProperties itemProperties) {
        Provider provider = itemProperties.provider();
        String id = itemProperties.id();

        ItemStack itemStack;
        switch (provider) {
            case VANILLA -> itemStack = buildVanillaItemStack(itemProperties);
            default -> {
                return;
            }
        }

        // add to map
        switch (itemProperties.type()) {
            case TIER -> push(Type.TIER, id, itemStack);
            case SUBSTRATE -> push(Type.SUBSTRATE, id, itemStack);
            case YEAST -> push(Type.YEAST, id, itemStack);
            case OUTPUT -> push(Type.OUTPUT, id, itemStack);
            case CONTAINER -> push(Type.CONTAINER, id, itemStack);
        }
    }
    // endregion

    // region push to map
    private void push(Type type, String id, ItemStack itemStack) {
        Map<String, ItemStack> map = Container.ITEM_STACK_MAP.get(type);
        if (map == null) map = Maps.newHashMap();
        map.put(id, getItemStackWithNbtTag(type, getItemStackWithNbtTag(type, itemStack)));
        Container.ITEM_STACK_MAP.put(type, map);
    }
    // endregion

    // region add nbt tag
    private ItemStack getItemStackWithNbtTag(Type type, ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setObject("BrewingItemType", type);
        return nbtItem.getItem();
    }
    // endregion

    // region build item stack
    @SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
    private ItemStack buildVanillaItemStack(final ItemProperties itemProperties) {
        Material material = itemProperties.material();

        ItemStack itemStack = new ItemStack(material);

        itemStack.editMeta(itemMeta -> {
            // hide effect (no effect)
            itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

            // custom model data
            itemMeta.setCustomModelData(itemProperties.customModelData());

            // display
            ItemProperties.Content display = itemProperties.display();
            if (display != null) {
                itemMeta.displayName(ConfigurationUtils.getDisplayComponent(display));
            }

            // restore food/health/saturation
            switch (itemProperties.type()) {
                case SUBSTRATE, OUTPUT -> {
                    int food = itemProperties.restoreFood();
                    double health = itemProperties.restoreHealth();
                    float saturation = itemProperties.restoreSaturation();

                    if (food != 0 || health != 0 || saturation != 0) {
                        List<Component> loreList = ConfigurationUtils.getLoreList(itemMeta);
                        if (food != 0) loreList.add(generateRestoreLore(food, "Food"));
                        if (health != 0) loreList.add(generateRestoreLore(health, "Health"));
                        if (health != 0) loreList.add(generateRestoreLore(saturation, "Saturation"));
                        itemMeta.lore(loreList);
                    }
                }
            }

            // lore
            ArrayList<ItemProperties.Content> lore = itemProperties.lore();
            if (lore != null) {
                List<Component> loreComponents = ConfigurationUtils.getLoreComponent(lore, itemMeta);
                itemMeta.lore(loreComponents);
            }

            // effect
            ArrayList<ItemProperties.Effect> effects = itemProperties.effects();
            if (effects != null) {
                List<Component> loreList = ConfigurationUtils.getLoreList(itemMeta);
                effects.forEach(effect -> {
                    TextComponent name = Component.text(ChatColor.translateAlternateColorCodes('&', "&a>&8| &7" + BrewingUtils.capitalizeFirst(effect.potionType().getName())));

                    // todo load from config.yml
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                    TextComponent duration = Component.text(ChatColor.translateAlternateColorCodes('&', " &3>&8|&7 Duration&8: &f" + decimalFormat.format(BigDecimal.valueOf(effect.duration()).divide(BigDecimal.valueOf(20)))));

                    loreList.add(name);
                    loreList.add(duration);
                });
                itemMeta.lore(loreList);
            }

            // tier
            String tier = itemProperties.tier();
            if (tier != null) {
                List<Component> loreList = ConfigurationUtils.getLoreList(itemMeta);
                loreList.add(ConfigurationUtils.getTierComponent(Container.ITEM_TIER.get(tier)));
                itemMeta.lore(loreList);
            }

            // required level
            int requiredLevel = itemProperties.requiredLevel();
            if (requiredLevel != 0) {
                switch (itemProperties.type()) {
                    case SUBSTRATE, OUTPUT, YEAST -> setRequiredLevel(requiredLevel, itemMeta);
                }
            }
        });

        return itemStack;
    }
    // endregion

    // region set required level lore
    private void setRequiredLevel(final int requiredLevel, final ItemMeta itemMeta) {
        List<Component> loreList = ConfigurationUtils.getLoreList(itemMeta);
        loreList.add(Component.text("Requires Level " + requiredLevel, NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        itemMeta.lore(loreList);
    }
    // endregion

    // region get restore lore
    private TextComponent generateRestoreLore(Object value, String name) {
        TextComponent blank = Component.text(" ", NamedTextColor.DARK_AQUA);
        TextComponent restorePrefix = Component.text("■ Restores ", NamedTextColor.GRAY);
        TextComponent restoreValue = Component.text(value + " ", NamedTextColor.WHITE);
        TextComponent restoreSuffix = Component.text(name, NamedTextColor.GRAY);
        return Component.text().append(blank, restorePrefix, restoreValue, restoreSuffix).decoration(TextDecoration.ITALIC, false).build();
    }
    // endregion
    // endregion

    // region get instance
    private static class LoaderInstance {
        static ItemStackLoader instance = new ItemStackLoader();
    }

    public static ItemStackLoader getInstance() {
        return LoaderInstance.instance;
    }
    // endregion
}