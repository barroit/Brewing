package homeward.plugin.brewing.loader;

import de.tr7zw.nbtapi.NBTItem;
import homeward.plugin.brewing.Main;
import homeward.plugin.brewing.bean.ItemProperties;
import homeward.plugin.brewing.enumerate.ItemTypeEnum;
import homeward.plugin.brewing.enumerate.ProviderEnum;
import homeward.plugin.brewing.utilitie.BrewingUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// loading all item stack from item properties
class ItemStackLoader {
    private final Set<ItemProperties> itemPropertiesSet = ItemPropertiesLoader.getInstance().getItemPropertiesSet();

    // region convert properties to item stack
    public void convertPropertiesToItemStack() {
        if (itemPropertiesSet.size() == 0) {
            return;
        }

        Main.clearItemStackMap();

        itemPropertiesSet.forEach(this::categorizeItem);
    }
    // endregion

    // region categorize item
    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private void categorizeItem(final ItemProperties itemProperties) {
        ProviderEnum provider = itemProperties.provider();
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
            case TIER -> Main.tierItemStackMap(id, addNbtTag(ItemTypeEnum.TIER, itemStack));
            case SUBSTRATE -> Main.substrateItemStackMap(id, addNbtTag(ItemTypeEnum.SUBSTRATE, itemStack));
            case YEAST -> Main.yeastItemStackMap(id, addNbtTag(ItemTypeEnum.YEAST, itemStack));
            case OUTPUT -> Main.outputItemStackMap(id, addNbtTag(ItemTypeEnum.OUTPUT, itemStack));
        }
    }
    // endregion

    // region add nbt tag
    private ItemStack addNbtTag(ItemTypeEnum type, ItemStack itemStack) {
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
                itemMeta.displayName(displayComponent(display));
            }

            // restore food/health/saturation
            switch (itemProperties.type()) {
                case SUBSTRATE, OUTPUT -> {
                    int food = itemProperties.restoreFood();
                    double health = itemProperties.restoreHealth();
                    float saturation = itemProperties.restoreSaturation();

                    if (food != 0 || health != 0 || saturation != 0) {
                        List<Component> loreList = loreList(itemMeta);
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
                List<Component> loreList = loreList(itemMeta);
                lore.forEach(content -> loreList.add(loreComponent(content)));
                itemMeta.lore(loreList);
            }

            // effect
            ArrayList<ItemProperties.Effect> effects = itemProperties.effects();
            if (effects != null) {
                List<Component> loreList = loreList(itemMeta);
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
                List<Component> loreList = loreList(itemMeta);
                loreList.add(tierComponent(Main.itemTier(tier)));
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

    // region get lore list
    private List<Component> loreList(ItemMeta itemMeta) {
        List<Component> loreList = itemMeta.lore();
        if (loreList == null) loreList = new ArrayList<>();
        loreList.add(Component.text().build());
        return loreList;
    }
    // endregion

    // region set required level lore
    private void setRequiredLevel(final int requiredLevel, final ItemMeta itemMeta) {
        List<Component> loreList = loreList(itemMeta);
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

    // region get text component
    private Component text(ItemProperties.Content content) {
        String ccText = ChatColor.translateAlternateColorCodes('&', content.text());
        TextComponent text = Component.text(ccText);
        ArrayList<Integer> color = content.color();
        if (color != null) {
            text = text.color(TextColor.color(color.get(0), color.get(1), color.get(2)));
        }
        return text;
    }
    // endregion

    // region get display component
    private Component displayComponent(ItemProperties.Content content) {
        return text(content).decoration(TextDecoration.ITALIC, false);
    }
    // endregion

    // region get lore component
    private Component loreComponent(ItemProperties.Content content) {
        return text(content).decoration(TextDecoration.ITALIC, false);
    }
    // endregion

    // region get tier component
    private Component tierComponent(ItemProperties.Content content) {
        TextComponent prefix = Component.text(" ", NamedTextColor.DARK_AQUA);
        TextComponent tierString = Component.text("Tier: ", NamedTextColor.GRAY);
        Component tier = text(content).decorate(TextDecoration.BOLD);
        return prefix.append(tierString).append(tier).decoration(TextDecoration.ITALIC, false);
    }
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