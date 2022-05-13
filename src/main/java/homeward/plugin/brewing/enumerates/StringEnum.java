package homeward.plugin.brewing.enumerates;

public enum StringEnum implements EnumBase{
    GUI_GUI("gui"),
    GUI_STORAGE("storage"),
    GUI_SCROLLING("scrolling"),
    GUI_PAGINATED("paginated"),
    ITEMSADDER("ItemsAdder"),
    VANILLA("Vanilla"),
    MMOITEMS("MMOItems"),

    SUBSTRATE("substrate"),
    RESTRICTION("restriction"),
    YEAST("yeast"),
    OUTPUT("output");

    private final String value;
    StringEnum(String value) {
        this.value = value;
    }

    @Override
    public String getString() {
        return value;
    }
}
