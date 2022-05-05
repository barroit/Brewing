package homeward.plugin.brewing.enumerates;

public enum BrewingType implements EnumBase {
    WINE("wine");

    private final String name;

    BrewingType(String name) {
        this.name = name;
    }

    @Override
    public String getString() {
        return name;
    }
}
