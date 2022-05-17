package homeward.plugin.brewing.enumerates;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public enum ComponentEnum implements EnumBase {
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
    GUI_RECIPES_PREVIEW_CONTAINER_LVL4(Component.text("\uF117")),

    GUI_CONTAINER(Component.text("\uF001")),
    GUI_BARREL(Component.text("\uF005")),
    GUI_SUBSTRATE(Component.text("\uF002")),
    GUI_RESTRICTION(Component.text("\uF003")),
    GUI_YEAST(Component.text("\uF004")),

    SLOT_SUBSTRATE(Component.text("底物 Substrate", NamedTextColor.YELLOW)),
    SLOT_RESTRICTION(Component.text("抑制剂 Restriction", NamedTextColor.YELLOW)),
    SLOT_YEAST(Component.text("酵母 Yeast", NamedTextColor.YELLOW)),

    SLOT_SUBSTRATE_STATE(Component.text("是否添加底物", NamedTextColor.YELLOW)),
    SLOT_RESTRICTION_STATE(Component.text("是否添加抑制剂", NamedTextColor.YELLOW)),
    SLOT_YEAST_STATE(Component.text("是否添加酵母", NamedTextColor.YELLOW));

    private final Component component;

    ComponentEnum(Component component) {
        this.component = component;
    }

    @Override
    public Component getComponent() {
        return component;
    }
}
