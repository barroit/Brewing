package homeward.plugin.brewing.beans;

import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class recipesItem implements Serializable {
    private ItemStack substrate;
}
