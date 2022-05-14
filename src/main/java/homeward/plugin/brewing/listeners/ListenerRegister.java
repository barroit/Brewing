package homeward.plugin.brewing.listeners;

import homeward.plugin.brewing.Brewing;
import homeward.plugin.brewing.utils.HomewardUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import java.util.Set;

import static homeward.plugin.brewing.constants.PluginInformation.LISTENER_PACKAGE_NAME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ListenerRegister {
    private final Brewing instance = Brewing.getInstance();

    public void register() {
        String listenerPath = HomewardUtils.getPath(Brewing.packageName(), LISTENER_PACKAGE_NAME);
        Set<Class<? extends Listener>> classes = new Reflections(listenerPath).getSubTypesOf(Listener.class);
        classes.forEach(var -> {
            try {
                if (var.getDeclaredConstructor().getParameterCount() == 0) {
                    Listener listener = var.getDeclaredConstructor().newInstance();
                    Bukkit.getServer().getPluginManager().registerEvents(listener, instance);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getPluginManager().disablePlugin(instance);
            }
        });
    }

    private static class RegisterInstance {
        private static final ListenerRegister instance = new ListenerRegister();
    }

    public static ListenerRegister getInstance() {
        return RegisterInstance.instance;
    }
}
