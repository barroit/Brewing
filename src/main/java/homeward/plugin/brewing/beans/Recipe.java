package homeward.plugin.brewing.beans;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Set;

@Builder
@Getter
@EqualsAndHashCode
@Accessors(fluent = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
public class Recipe implements Serializable {
    String displayName;
    int[] displayColor;
    String[] lore;
    String level;
    @Singular Set<CustomItemStack> substrates;
    @Singular Set<CustomItemStack> restrictions;
    @Singular Set<CustomItemStack> yeasts;
    CustomItemStack output;
    int maxYield;
    int minYield;
    double globalRestrictionsIndex;
    double globalYeastsIndex;
    int brewingCycle;
}
