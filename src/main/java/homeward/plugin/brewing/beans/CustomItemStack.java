package homeward.plugin.brewing.beans;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

@Builder
@Getter
@EqualsAndHashCode
@Accessors(fluent = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ToString
public class CustomItemStack implements Serializable {
    ItemStack itemStack;
    @Builder.Default Integer quantity = 1;
    @Builder.Default double index = 1.0D;
}
