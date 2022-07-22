package com.baioretto.brewing.util;

import com.baioretto.baiolib.util.ReflectionUtil;
import com.baioretto.baiolib.util.Util;
import com.baioretto.brewing.Brewing;
import com.baioretto.brewing.enumerate.Type;
import com.baioretto.brewing.exception.BrewingInternalException;
import lombok.experimental.UtilityClass;
import me.mattstudios.mf.base.CommandBase;
import me.mattstudios.mf.base.CommandManager;
import me.mattstudios.mf.base.components.TypeResult;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

@UtilityClass
public class Register {
    private final ClassLoader classLoader = Register.class.getClassLoader();
    private final CommandManager commandManager = new CommandManager(Brewing.instance());

    // listener
    public void registerListeners() {
        Util.getReflections(BrewingUtils.getPath(Brewing.class.getPackageName(), "listener"), classLoader).getSubTypesOf(Listener.class).forEach(listener -> {
            try {
                Constructor<? extends Listener> constructor = listener.getDeclaredConstructor();
                if (constructor.getParameterCount() == 0) {
                    Bukkit.getPluginManager().registerEvents(constructor.newInstance(), Brewing.instance());
                }
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                Bukkit.getPluginManager().disablePlugin(Brewing.instance());
                throw new BrewingInternalException(e);
            }
        });
    }

    // reload
    public void registerCommands() {
        Util.getReflections(BrewingUtils.getPath(Brewing.class.getPackageName(), "command"), classLoader)
                .getSubTypesOf(CommandBase.class)
                .forEach(command -> commandManager.register(ReflectionUtil.newInstance(command)));
    }

    public void registerParameter() {
        commandManager.getParameterHandler().register(Type.class, argument -> {
            Type type = Type.getType(argument.toString().toUpperCase(Locale.ROOT));
            if (type == null) return new TypeResult(argument);
            return new TypeResult(type, argument);
        });
    }
}
