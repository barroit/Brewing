package homeward.plugin.brewing.beans;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true, fluent = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RecipesItem implements Serializable {
    Set<CustomItemStack> substrate;
    Set<CustomItemStack> restriction;
    Set<CustomItemStack> yeast;
    @NonFinal CustomItemStack output;

    public RecipesItem substrate(CustomItemStack substrate) {
        this.substrate.add(substrate);
        return this;
    }

    public RecipesItem restriction(CustomItemStack restriction) {
        this.restriction.add(restriction);
        return this;
    }

    public RecipesItem yeast(CustomItemStack yeast) {
        this.yeast.add(yeast);
        return this;
    }

    public RecipesItem() {
        this.substrate = this.restriction = this.yeast = new HashSet<>();
    }
}
