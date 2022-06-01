package homeward.plugin.brewing.registrant;

import homeward.plugin.brewing.Main;
import homeward.plugin.brewing.enumerate.ItemType;
import homeward.plugin.brewing.utilitie.BrewingUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.mattstudios.mf.base.*;
import me.mattstudios.mf.base.components.TypeResult;
import org.bukkit.Bukkit;
import org.reflections.Reflections;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandRegister {

    public void register() {
        CommandManager commandManager = new CommandManager(Main.getInstance());

        registerParameter(commandManager.getParameterHandler());

        CommandBase[] commandInstance = getCommandInstance();
        commandManager.register(commandInstance);
    }

    private void registerParameter(ParameterHandler handler) {
        handler.register(ItemType.class, argument -> {
            ItemType itemType = ItemType.getItemType(argument.toString().toUpperCase(Locale.ROOT));
            if (itemType == null) return new TypeResult(argument);
            return new TypeResult(itemType, argument);
        });
    }

    private CommandBase[] getCommandInstance() {
        String commandPath = BrewingUtils.getPath(Main.class.getPackageName(), COMMAND_PACKAGE_NAME);
        Set<Class<? extends CommandBase>> classes = new Reflections(commandPath).getSubTypesOf(CommandBase.class);

        Set<CommandBase> commands = new LinkedHashSet<>();

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

        return commands.toArray(new CommandBase[]{});
    }

    private static class RegisterInstance {
        private static final CommandRegister instance = new CommandRegister();
    }

    public static CommandRegister getInstance() {
        return RegisterInstance.instance;
    }

    private static final String COMMAND_PACKAGE_NAME = "command";
}
