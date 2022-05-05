package homeward.plugin.brewing.enumerates;

public enum OutputType implements EnumBase {
    OLD_VINES("old vines");

    private final String name;

    OutputType(String name) {
        this.name = name;
    }

    @Override
    public String getString() {
        return name;
    }
}
