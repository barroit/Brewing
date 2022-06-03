package homeward.plugin.brewing.utilitie;

import homeward.plugin.brewing.enumerate.Title;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Arrays;

@UtilityClass
public class GuiUtils {
    public static net.kyori.adventure.text.Component getTitle(NamedTextColor color, Title...title) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(title).toList().forEach(c -> sb.append(((TextComponent) c.getComponent()).content()));
        return net.kyori.adventure.text.Component.text(sb.toString(), color);
    }
}
