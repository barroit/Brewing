package homeward.plugin.brewing.enumerates;

public enum StringEnum implements EnumBase{
    GUI_GUI("gui"),
    GUI_STORAGE("storage"),
    GUI_SCROLLING("scrolling"),
    GUI_PAGINATED("paginated");

    private final String value;
    StringEnum(String value) {
        this.value = value;
    }

    @Override
    public String getString() {
        return value;
    }
}
