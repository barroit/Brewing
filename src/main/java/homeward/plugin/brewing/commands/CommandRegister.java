package homeward.plugin.brewing.commands;

import homeward.plugin.brewing.Brewing;
import homeward.plugin.brewing.utils.HomewardUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.mattstudios.mf.base.CommandBase;
import me.mattstudios.mf.base.CommandManager;
import org.bukkit.Bukkit;
import org.reflections.Reflections;

import java.util.LinkedHashSet;
import java.util.Set;

import static homeward.plugin.brewing.constants.PluginInformation.COMMAND_PACKAGE_NAME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandRegister {
    private final Set<CommandBase> commands = new LinkedHashSet<>();

    public void register() {
        CommandManager commandManager = new CommandManager(Brewing.getInstance());

        getCommandClassInstance();

        commandManager.register(commands.toArray(new CommandBase[0]));
    }

    private void getCommandClassInstance() {
        String commandPath = HomewardUtils.getPath(Brewing.packageName(), COMMAND_PACKAGE_NAME);
        Set<Class<? extends CommandBase>> classes = new Reflections(commandPath).getSubTypesOf(CommandBase.class);

        classes.forEach(var -> {
            try {
                if (var.getDeclaredConstructor().getParameterCount() == 0) {
                    commands.add(var.getDeclaredConstructor().newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getPluginManager().disablePlugin(Brewing.getInstance());
            }
        });
    }

    private static class RegisterInstance {
        private static final CommandRegister instance = new CommandRegister();
    }

    public static CommandRegister getInstance() {
        return CommandRegister.RegisterInstance.instance;
    }
}
