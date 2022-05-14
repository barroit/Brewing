package homeward.plugin.brewing.listeners;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import homeward.plugin.brewing.Brewing;
import homeward.plugin.brewing.configurations.RecipesConfigurationLoader;
import homeward.plugin.brewing.configurations.RecipesConfigurationLoader.RecipesConfigurationLoaderBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.RoundingMode;
import java.util.Locale;

public class ItemsAdderLoadDataListener implements Listener {
    private final Brewing plugin;

    public ItemsAdderLoadDataListener() {
        plugin = Brewing.getInstance();
    }

    @EventHandler
    public void onItemsAdderLoadedEvent(ItemsAdderLoadDataEvent event) {
        String roundingModeString = plugin.getConfig().getString("rounding-mode", "HALF_UP");
        RoundingMode[] roundingMode = {RoundingMode.HALF_UP};
        try {
            roundingMode[0] = RoundingMode.valueOf(roundingModeString.replaceAll("[-\\x20]", "_").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignore) {
            plugin.getSLF4JLogger().error("rounding mode is invalid, will fallback to HALF_UP");
        }

        String roundingPatternString = plugin.getConfig().getString("rounding-pattern", "#.##");
        String[] roundingPattern = {"#.##"};
        if (roundingPatternString.matches("#\\.#{1,23}")) {
            roundingPattern[0] = roundingPatternString;
        } else {
            plugin.getSLF4JLogger().error("rounding pattern is invalid, will fallback to #.##");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                RecipesConfigurationLoaderBuilder configurationLoaderBuilder = RecipesConfigurationLoader.builder();
                RecipesConfigurationLoader recipesConfigurationLoader = configurationLoaderBuilder.roundingMode(roundingMode[0]).roundingPattern(roundingPattern[0]).build();
                recipesConfigurationLoader.load();
            }
        }.run();
    }
}
