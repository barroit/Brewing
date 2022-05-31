package homeward.plugin.brewing.enumerate;

import net.kyori.adventure.text.Component;

import java.util.Collection;

public interface EnumBase {
    default Component getComponent() {
        return null;
    }

    default Collection<?> getCollection() {
        return null;
    }

    default String getString() {
        return null;
    }
}
