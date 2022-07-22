package com.baioretto.brewing.enumerate;

import com.google.common.collect.Maps;

import java.util.Map;

@SuppressWarnings({"SameParameterValue", "unused"})
public enum Provider implements EnumBase {
    VANILLA("vanilla");

    private final String name;

    private static final Map<String, Provider> BY_NAME = Maps.newHashMap();

    static {
        for (Provider provider : values()) {
            BY_NAME.put(provider.name(), provider);
        }
    }

    Provider(String type) {
        this.name = type;
    }

    public static Provider getProvider(String name) {
        return BY_NAME.getOrDefault(name, null);
    }

    @Override
    public String getString() {
        return name;
    }
}
