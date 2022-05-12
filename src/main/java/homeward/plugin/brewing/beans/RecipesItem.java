package homeward.plugin.brewing.beans;

import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.Set;

@Data
@Accessors(chain = true, fluent = true, makeFinal = true)
public class RecipesItem implements Serializable {
    private Set<ItemStack> substrate;
}
