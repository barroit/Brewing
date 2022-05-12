package homeward.plugin.brewing.beans;

import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true, fluent = true, makeFinal = true)
public class RecipesItem implements Serializable {
    private final Set<ItemStack> substrate;

    public RecipesItem substrate(ItemStack substrate) {
        this.substrate.add(substrate);
        return this;
    }

    public RecipesItem() {
        substrate = new HashSet<>();
    }
}
