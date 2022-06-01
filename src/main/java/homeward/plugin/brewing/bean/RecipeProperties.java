package homeward.plugin.brewing.bean;


import homeward.plugin.brewing.enumerate.ItemType;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true, chain = true)
@EqualsAndHashCode
public class RecipeProperties implements Serializable {
    @NotNull String id, level;
    @NotNull ItemProperties.Content display;
    @Nullable ArrayList<ItemProperties.Content> lore;
    @NotNull LinkedHashSet<ItemStack> substrates, containers;
    @Nullable LinkedHashSet<CustomItem> yeasts, extras;
    @NotNull ItemStack output;
    int minYield, maxYield, cycle;

    @Getter @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    public static class CustomItem {
        @NotNull ItemType type;
        @NotNull ItemStack item;
        double amplify;

        @Override
        public String toString() {
            return String.format("{item:%s,amplify:%f}", this.item, this.amplify);
        }
    }

    public static RecipePropertiesBuilder builder() {
        return new RecipePropertiesBuilder();
    }

    public static CustomItem getCustomItem() {
        return new CustomItem();
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Setter
    @Accessors(fluent = true, chain = true)
    public static class RecipePropertiesBuilder {
        String id, level;
        ItemProperties.Content display;
        ArrayList<ItemProperties.Content> lore;
        LinkedHashSet<ItemStack> substrates, containers;
        LinkedHashSet<CustomItem> yeasts, extras;
        ItemStack output;
        int minYield, maxYield, cycle;

        private RecipePropertiesBuilder() {
            lore = new ArrayList<>();
            substrates = containers = new LinkedHashSet<>();
            yeasts = extras = new LinkedHashSet<>();
        }

        public RecipePropertiesBuilder lore(ArrayList<ItemProperties.Content> lore) {
            if (lore == null) return this;
            this.lore.addAll(lore);
            return this;
        }

        public RecipeProperties build() {
            return new RecipeProperties(this.id, this.level, this.display, this.lore, this.substrates, this.containers, this.yeasts, this.extras, this.output, this.minYield, this.maxYield, this.cycle);
        }
    }

    @Override
    public String toString() {
        return String.format("{id:%s,level:%s,display:%s,lore:%s,substrates:%s,yeasts:%s,extras:%s,containers:%s,output:%s,minYield:%d,maxYield:%d,cycle:%d}", this.id, this.level, this.display, this.lore, this.substrates, this.yeasts, this.extras, this.containers, this.output, this.minYield, this.maxYield, this.cycle);
    }
}
