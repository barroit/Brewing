package homeward.plugin.brewing.enumerate;

import com.google.common.collect.Maps;

import java.util.Map;

public enum ProviderEnum implements EnumBase {
    VANILLA("vanilla");

    private final String name;

    private static final Map<String, ProviderEnum> BY_NAME = Maps.newHashMap();
    static {
        for (ProviderEnum provider : values()) {
            BY_NAME.put(provider.name(), provider);
        }
    }

    ProviderEnum(String type) {
        this.name = type;
    }

    public static ProviderEnum getProvider(String name) {
        return BY_NAME.getOrDefault(name, null);
    }

    @Override
    public String getString() {
        return name;
    }
}
