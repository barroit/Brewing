package homeward.plugin.brewing;

import homeward.plugin.brewing.enumerate.Type;
import homeward.plugin.brewing.exception.BrewingInternalException;
import homeward.plugin.brewing.utilitie.BrewingUtils;
import me.mattstudios.mf.base.CommandBase;
import me.mattstudios.mf.base.CommandManager;
import me.mattstudios.mf.base.ParameterHandler;
import me.mattstudios.mf.base.components.TypeResult;
import org.bukkit.Bukkit;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class Register {
    private final CommandManager commandManager;

    public Register() {
        this.commandManager = new CommandManager(Main.getInstance());
    }

    public static void registerCommand() {
        new Register().commandConsumer();
    }

    public void commandConsumer() {
        registerParameter(commandManager.getParameterHandler());
        commandManager.register(getCommandInstance());
    }

    private void registerParameter(ParameterHandler handler) {
        handler.register(Type.class, argument -> {
            Type type = Type.getType(argument.toString().toUpperCase(Locale.ROOT));
            if (type == null) return new TypeResult(argument);
            return new TypeResult(type, argument);
        });
    }

    private CommandBase[] getCommandInstance() {
        String commandPath = BrewingUtils.getPath(Main.class.getPackageName(), "command");
        Set<Class<? extends CommandBase>> classes = new Reflections(commandPath).getSubTypesOf(CommandBase.class);

        Set<CommandBase> commands = new LinkedHashSet<>();

        classes.forEach(commandClass -> {
            try {
                if (commandClass.getDeclaredConstructor().getParameterCount() == 0) {
                    commands.add(commandClass.getDeclaredConstructor().newInstance());
                }
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                Bukkit.getPluginManager().disablePlugin(Main.getInstance());
                throw new BrewingInternalException(e);
            }
        });

        return commands.toArray(new CommandBase[]{});
    }
}
