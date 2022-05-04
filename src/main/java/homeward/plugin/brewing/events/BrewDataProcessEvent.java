package homeward.plugin.brewing.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BrewDataProcessEvent extends Event implements Cancellable {



    @Override
    public boolean isCancelled() {
        return true;
    }

    @Override
    public void setCancelled(boolean cancel) {

    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return null;
    }
}
