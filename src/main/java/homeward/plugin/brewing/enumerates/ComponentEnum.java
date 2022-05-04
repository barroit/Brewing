package homeward.plugin.brewing.enumerates;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public enum ComponentEnum implements EnumBase {
    BARREL_TITLE(Component.text("\uF808" + "\uF001", NamedTextColor.WHITE)),
    BARREL_TITLE_WITH_SUBSTRATE(Component.text("\uF808" + "\uF001" + "\uF80C" + "\uF80A" + "\uF809" + "\uF002", NamedTextColor.WHITE)),
    BARREL_TITLE_WITH_RESTRICTION(Component.text("\uF808" + "\uF003" + "\uF001", NamedTextColor.WHITE)),
    BARREL_TITLE_WITH_YEAST(Component.text("\uF808" + "\uF004" + "\uF001", NamedTextColor.WHITE)),

    SLOT_SUBSTRATE(Component.text("底物 Substrate", NamedTextColor.YELLOW)),
    SLOT_RESTRICTION(Component.text("抑制剂 Restriction", NamedTextColor.YELLOW)),
    SLOT_YEAST(Component.text("酵母 Yeast", NamedTextColor.YELLOW)),

    SLOT_BARREL(Component.text("是否开始", NamedTextColor.YELLOW)),

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
