package homeward.plugin.brewing.enumerate;

import homeward.plugin.brewing.utilitie.GuiUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@SuppressWarnings("unused")
public enum Title implements EnumBase {
    GAP_REGULAR(Component.text("\uF80C" + "\uF80A" + "\uF809" + "\uF801")),

    POSITIVE_4(Component.text("\uF825")),
    POSITIVE_5(Component.text("\uF826")),
    POSITIVE_6(Component.text("\uF827")),
    POSITIVE_63(Component.text("\uF82B")),
    POSITIVE_127(Component.text("\uF82C")),
    POSITIVE_256(Component.text("\uF82D")),

    NEGATIVE_3(Component.text("\uF801")),
    NEGATIVE_5(Component.text("\uF803")),
    NEGATIVE_10(Component.text("\uF808")),
    NEGATIVE_18(Component.text("\uF809")),
    NEGATIVE_34(Component.text("\uF80A")),
    NEGATIVE_66(Component.text("\uF80B")),
    NEGATIVE_130(Component.text("\uF80C")),

    GUI_RECIPES_PREVIEW_CONTAINER(Component.text("\uF114")),
    GUI_RECIPES_PREVIEW_CONTAINER_LVL1(Component.text("\uF115")),
    GUI_RECIPES_PREVIEW_CONTAINER_LVL3(Component.text("\uF116")),
    GUI_RECIPES_PREVIEW_CONTAINER_LVL4(Component.text("\uF117"));

    private final Component component;

    Title(Component component) {
        this.component = component;
    }

    @Override
    public Component getComponent() {
        return component;
    }

    public static Component getRecipeTitle(Amount amount) {
        switch (amount) {
            case x1 -> {
                return GuiUtils.getTitle(NamedTextColor.WHITE, NEGATIVE_10, GUI_RECIPES_PREVIEW_CONTAINER, GAP_REGULAR, GUI_RECIPES_PREVIEW_CONTAINER_LVL3, GAP_REGULAR, GUI_RECIPES_PREVIEW_CONTAINER_LVL1);
            }
            case x2 -> {
                return GuiUtils.getTitle(NamedTextColor.WHITE, NEGATIVE_10, GUI_RECIPES_PREVIEW_CONTAINER, GAP_REGULAR, GUI_RECIPES_PREVIEW_CONTAINER_LVL3, GAP_REGULAR, GUI_RECIPES_PREVIEW_CONTAINER_LVL4);
            }
            case x3 -> {
                return GuiUtils.getTitle(NamedTextColor.WHITE, NEGATIVE_10, GUI_RECIPES_PREVIEW_CONTAINER, GAP_REGULAR, GUI_RECIPES_PREVIEW_CONTAINER_LVL3);
            }
            case x4 -> {
                return GuiUtils.getTitle(NamedTextColor.WHITE, NEGATIVE_10, GUI_RECIPES_PREVIEW_CONTAINER, GAP_REGULAR, GUI_RECIPES_PREVIEW_CONTAINER_LVL4);
            }
            default -> throw new IllegalArgumentException();
        }
    }

    public enum Amount {
        x1,
        x2,
        x3,
        x4
    }
}
