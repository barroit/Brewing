package homeward.plugin.brewing.enumerate;

import homeward.plugin.brewing.utilitie.GuiUtils;
import net.kyori.adventure.text.Component;

@SuppressWarnings("unused")
public enum Title implements EnumBase {
    GAP_REGULAR(Component.text("\uF80C" + "\uF80A" + "\uF809" + "\uF801")),

    POSITIVE_1(Component.text("\uF822")),
    POSITIVE_2(Component.text("\uF823")),
    POSITIVE_3(Component.text("\uF824")),
    POSITIVE_4(Component.text("\uF825")),
    POSITIVE_5(Component.text("\uF826")),
    POSITIVE_6(Component.text("\uF827")),
    POSITIVE_7(Component.text("\uF828")),
    POSITIVE_15(Component.text("\uF829")),
    POSITIVE_31(Component.text("\uF82A")),
    POSITIVE_63(Component.text("\uF82B")),
    POSITIVE_127(Component.text("\uF82C")),
    POSITIVE_256(Component.text("\uF82D")),

    // negative
    NEGATIVE_3(Component.text("\uF801")),
    NEGATIVE_4(Component.text("\uF802")),
    NEGATIVE_5(Component.text("\uF803")),
    NEGATIVE_6(Component.text("\uF804")),
    NEGATIVE_7(Component.text("\uF805")),
    NEGATIVE_8(Component.text("\uF806")),
    NEGATIVE_9(Component.text("\uF807")),
    NEGATIVE_10(Component.text("\uF808")),
    NEGATIVE_18(Component.text("\uF809")),
    NEGATIVE_34(Component.text("\uF80A")),
    NEGATIVE_66(Component.text("\uF80B")),
    NEGATIVE_130(Component.text("\uF80C")),
    NEGATIVE_259(Component.text("\uF80D")),

    PREVIEW_SLOT_SELECTED(Component.text("\uF130")),
    DETAIL_SLOT_SELECTED(Component.text("\uF131")),

    RECIPES_PREVIEW_CONTAINER(Component.text("\uF114")),
    RECIPES_PREVIEW_CONTAINER_LVL1(Component.text("\uF115")),
    RECIPES_PREVIEW_CONTAINER_LVL3(Component.text("\uF116")),
    RECIPES_PREVIEW_CONTAINER_LVL4(Component.text("\uF117")),

    RECIPES_DETAIL_CONTAINER(Component.text("\uF118"));

    private final Component component;

    Title(Component component) {
        this.component = component;
    }

    @Override
    public Component getComponent() {
        return component;
    }

    public static Component getRecipeDetailGuiTitle() {
        return GuiUtils.getTitle(NEGATIVE_10, RECIPES_DETAIL_CONTAINER);
    }

    public enum Amount {
        x1,
        x2,
        x3,
        x4
    }
}
