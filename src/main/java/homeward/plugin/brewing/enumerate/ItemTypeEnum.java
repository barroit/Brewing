package homeward.plugin.brewing.enumerate;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public enum ItemTypeEnum implements EnumBase {
    TIER("tier"),
    SUBSTRATE("substrate"),
    YEAST("yeast"),
    OUTPUT("output"),
    CONTAINER("container");

    private final String type;

    private static final Map<String, ItemTypeEnum> BY_NAME = Maps.newHashMap();
    static {
        for (ItemTypeEnum item : values()) {
            BY_NAME.put(item.name(), item);
        }
    }

    ItemTypeEnum(String type) {
        this.type = type;
    }

    public static @Nullable ItemTypeEnum getItemType(String name) {
        return BY_NAME.getOrDefault(name, null);
    }

    @Override
    public String getString() {
        return type;
    }
}
