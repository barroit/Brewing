package com.baioretto.brewing.util;

import com.baioretto.brewing.Brewing;
import com.baioretto.brewing.enumerate.Tag;
import com.baioretto.brewing.loader.ConfigurationLoader;
import de.tr7zw.nbtapi.NBTBlock;
import lombok.experimental.UtilityClass;
import me.mattstudios.mf.base.CommandManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@UtilityClass
@SuppressWarnings("unused")
public class BrewingUtils {
    private final CommandManager manager = new CommandManager(Brewing.instance());

    public boolean isDisabled(YamlConfiguration configuration) {
        List<String> headerList = configuration.options().getHeader();
        return headerList.size() != 0 && headerList.get(0).equalsIgnoreCase("disable");
    }

    public String capitalizeFirst(String str) {
        return str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1).toLowerCase(Locale.ROOT);
    }

    public String getPath(ConfigurationSection section, String current) {
        return getPath(section.getCurrentPath(), current);
    }

    public String getPath(String path, String append) {
        return path + '.' + append;
    }

    public int getIntervalRandom(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }

    public void load(boolean async) {
        if (async) {
            CompletableFuture.runAsync(ConfigurationLoader.getInstance()::load)
                    .thenRunAsync(Register::registerListeners);
            Register.registerParameter();
            Register.registerCommands();
        } else {
            ConfigurationLoader.getInstance().load();
            Register.registerCommands();
            Register.registerListeners();
        }
    }

    public boolean notBrewingBarrel(Block craftBlock) {
        if (craftBlock == null || craftBlock.getType().equals(Material.AIR)) return true;

        NBTBlock block = new NBTBlock(craftBlock);
        return !(block.getData().hasKey(Tag.BARREL.key()) && block.getData().getObject(Tag.BARREL.key(), Object.class).equals(Tag.BARREL.value()));
    }
}