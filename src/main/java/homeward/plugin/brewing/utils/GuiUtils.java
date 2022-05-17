package homeward.plugin.brewing.utils;

import homeward.plugin.brewing.enumerates.ComponentEnum;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Arrays;

@UtilityClass
public class GuiUtils {
    public static Component getTitle(NamedTextColor color, ComponentEnum ...title) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(title).toList().forEach(c -> sb.append(((TextComponent) c.getComponent()).content()));
        return Component.text(sb.toString(), color);
    }
}
