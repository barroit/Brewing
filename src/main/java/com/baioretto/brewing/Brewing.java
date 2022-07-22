package com.baioretto.brewing;

import com.baioretto.brewing.util.BrewingUtils;
import lombok.AccessLevel;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.java.JavaPlugin;

@Accessors(fluent = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public final class Brewing extends JavaPlugin {
    private static Brewing plugin;

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        BrewingUtils.load(true);
    }

    public Brewing() {
        plugin = this;
    }

    public static Brewing instance() {
        return plugin;
    }
}
