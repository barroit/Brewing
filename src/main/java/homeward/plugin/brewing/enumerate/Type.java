package homeward.plugin.brewing.enumerate;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public enum Type implements EnumBase {
    // item type
    TIER("tier"),
    SUBSTRATE("substrate"),
    YEAST("yeast"),
    OUTPUT("output"),
    CONTAINER("container"),

    // gui type
    RECIPES_PREVIEW_GUI("recipes preview gui");

    private final String type;

    private static final Map<String, Type> BY_NAME = Maps.newHashMap();
    static {
        for (Type item : values()) {
            BY_NAME.put(item.name(), item);
        }
    }

    Type(String type) {
        this.type = type;
    }

    public static @Nullable Type getType(String name) {
        return BY_NAME.getOrDefault(name, null);
    }

    @Override
    public String getString() {
        return type;
    }
}
