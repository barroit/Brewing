package homeward.plugin.brewing.enumerates;

import java.util.Collection;

public enum CollectionEnum implements EnumBase {;

    private final Collection<?> collection;
    CollectionEnum(Collection<?> collection) {
        this.collection = collection;
    }

    @Override
    public Collection<?> getCollection() {
        return this.collection;
    }
}
