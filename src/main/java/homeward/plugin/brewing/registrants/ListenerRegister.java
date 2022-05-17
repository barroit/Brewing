package homeward.plugin.brewing.registrants;

import homeward.plugin.brewing.Main;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import java.util.Set;

import static homeward.plugin.brewing.utilities.BrewingUtils.getPath;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ListenerRegister {
    public void register() {
        String listenerPath = getPath(Main.packageName(), LISTENER_PACKAGE_NAME);
        Set<Class<? extends Listener>> classes = new Reflections(listenerPath).getSubTypesOf(Listener.class);
        classes.forEach(var -> {
            try {
                if (var.getDeclaredConstructor().getParameterCount() == 0) {
                    Listener listener = var.getDeclaredConstructor().newInstance();
                    Bukkit.getServer().getPluginManager().registerEvents(listener, Main.getInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            }
        });
    }

    private static class RegisterInstance {
        private static final ListenerRegister instance = new ListenerRegister();
    }

    public static ListenerRegister getInstance() {
        return RegisterInstance.instance;
    }

    private static final String LISTENER_PACKAGE_NAME = "listeners";
}
