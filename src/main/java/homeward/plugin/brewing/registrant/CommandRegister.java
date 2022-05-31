package homeward.plugin.brewing.registrant;

import homeward.plugin.brewing.Main;
import homeward.plugin.brewing.utilitie.BrewingUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.mattstudios.mf.base.CommandBase;
import me.mattstudios.mf.base.CommandManager;
import me.mattstudios.mf.base.MessageHandler;
import org.bukkit.Bukkit;
import org.reflections.Reflections;

import java.util.LinkedHashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandRegister {
    private final Set<CommandBase> commands = new LinkedHashSet<>();

    public void register() {
        CommandManager commandManager = new CommandManager(Main.getInstance());

        getCommandClassInstance();

        commandManager.register(commands.toArray(new CommandBase[0]));
    }

    private void getCommandClassInstance() {
        String commandPath = BrewingUtils.getPath(Main.class.getPackageName(), COMMAND_PACKAGE_NAME);
        Set<Class<? extends CommandBase>> classes = new Reflections(commandPath).getSubTypesOf(CommandBase.class);

        classes.forEach(var -> {
            try {
                if (var.getDeclaredConstructor().getParameterCount() == 0) {
                    commands.add(var.getDeclaredConstructor().newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            }
        });
    }

    private static class RegisterInstance {
        private static final CommandRegister instance = new CommandRegister();
    }

    public static CommandRegister getInstance() {
        return RegisterInstance.instance;
    }

    private static final String COMMAND_PACKAGE_NAME = "command";
}
