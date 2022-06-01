package homeward.plugin.brewing.bean;

import homeward.plugin.brewing.enumerate.ItemType;
import homeward.plugin.brewing.enumerate.Provider;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true, chain = true)
@EqualsAndHashCode
public class ItemProperties {
    @NotNull String id; // required
    @NotNull ItemType type; // required
    @NotNull Provider provider; // required

    @Nullable Content display;
    @Nullable ArrayList<Content> lore;
    @Nullable String tier;

    @NotNull Material material; // required
    int customModelData; // required

    int restoreFood;
    double restoreHealth;
    float restoreSaturation;

    ArrayList<Effect> effects;

    ArrayList<String> command;
    int requiredLevel;

    // region Content class
    @Getter @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Content {
        @NotNull String text;
        @Nullable ArrayList<Integer> color;

        @Override
        public String toString() {
            return String.format("{text:\"%s\",color:%s}", this.text, this.color);
        }
    }

    public static Content getContent() {
        return new Content();
    }
    // endregion

    // region Effects class
    @Getter @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Effect {
        PotionEffectType potionType;
        int duration;
        int amplifier;
        boolean ambient;
        boolean showParticles;
        boolean showIcon;

        @Override
        public String toString() {
            return String.format("{potionType:%s,duration:%d,amplifier:%d,ambient:%b,showParticles:%b,showIcon:%b}", this.potionType.getName(), this.duration, this.amplifier, this.ambient, this.showParticles, this.showIcon);
        }
    }

    public static Effect getEffect() {
        return new Effect();
    }
    // endregion

    // region get builder
    public static ItemPropertiesBuilder builder() {
        return new ItemPropertiesBuilder();
    }
    // endregion

    // region builder class
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Setter
    @Accessors(fluent = true, chain = true)
    public static class ItemPropertiesBuilder {
        String id;
        ItemType type;
        Provider provider;

        Content display;
        ArrayList<Content> lore;
        String tier;

        Material material;
        int customModelData;

        int restoreFood;
        double restoreHealth;
        float restoreSaturation;

        @Nullable ArrayList<Effect> effects;

        ArrayList<String> command;
        int requiredLevel;

        private ItemPropertiesBuilder() {
            lore = new ArrayList<>();
        }

        public ItemPropertiesBuilder lore(ArrayList<Content> lore) {
            if (lore == null) return this;
            this.lore.addAll(lore);
            return this;
        }

        public ItemProperties build() {
            return new ItemProperties(this.id, this.type, this.provider, this.display, this.lore, this.tier, this.material, this.customModelData, this.restoreFood, this.restoreHealth, this.restoreSaturation, this.effects, this.command, this.requiredLevel);
        }
    }
    // endregion

    // region toString
    @Override
    public String toString() {
        StringBuilder commandList;
        if (command != null) {
            commandList = new StringBuilder();
            command.forEach(s -> commandList.append("\"").append(s).append("\","));
            commandList.insert(0, "[").deleteCharAt(commandList.length() - 1).insert(commandList.length(), "]");
        } else commandList = null;
        return String.format("{id:%s,type:%s,provider:%s,display:%s,lore:%s,tier:%s,material:%s,customModuleData:%d,restoreFood:%s,restoreHealth:%f,restoreSaturation:%f,effects:%s,command:%s,requiredLevel:%d}", this.id, this.type, this.provider, this.display, this.lore, this.tier, this.material, this.customModelData, this.restoreFood, this.restoreHealth, this.restoreSaturation, this.effects, commandList, this.requiredLevel);
    }
    // endregion
}
