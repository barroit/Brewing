package com.baioretto.brewing.enumerate;

import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.Map;

public enum Tag implements EnumBase {
    BARREL("Brewing", "barrel");

    @Getter
    private final String key;
    @Getter
    private final Object value;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Map<String, Tag> BY_NAME = Maps.newHashMap();

    static {
        for (Tag item : values()) {
            BY_NAME.put(item.name(), item);
        }
    }

    Tag(String key, Object value) {
        this.key = key;
        this.value = value;
    }


}
