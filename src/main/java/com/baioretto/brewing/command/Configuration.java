package com.baioretto.brewing.command;

import com.baioretto.brewing.Container;
import com.baioretto.brewing.util.BrewingUtils;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;


@Command("brewing")
@SuppressWarnings("unused")
public class Configuration extends CommandBase {
    @SubCommand("reload")
    public void reload(final CommandSender commandSender) {
        this.clearContainer();
        BrewingUtils.load(true);
    }

    private void clearContainer() {
        Container.RECIPE_DETAIL_GUI.clear();
        Container.RECIPE_PREVIEW_GUI.clear();
    }
}
