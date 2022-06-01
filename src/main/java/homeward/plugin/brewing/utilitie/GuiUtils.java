package homeward.plugin.brewing.utilitie;

import homeward.plugin.brewing.enumerate.Components;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Arrays;

@UtilityClass
public class GuiUtils {
    public static Component getTitle(NamedTextColor color, Components...title) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(title).toList().forEach(c -> sb.append(((TextComponent) c.getComponent()).content()));
        return Component.text(sb.toString(), color);
    }
}
