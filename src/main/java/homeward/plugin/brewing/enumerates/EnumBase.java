package homeward.plugin.brewing.enumerates;

import net.kyori.adventure.text.Component;

import java.util.Collection;

public interface EnumBase {
    default Component getComponent() {
        return null;
    }

    default Collection<?> getCollection() {
        return null;
    }
}
