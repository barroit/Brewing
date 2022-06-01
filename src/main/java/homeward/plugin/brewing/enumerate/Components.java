package homeward.plugin.brewing.enumerate;

import net.kyori.adventure.text.Component;

public enum Components implements EnumBase {
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

    Components(Component component) {
        this.component = component;
    }

    @Override
    public Component getComponent() {
        return component;
    }
}
