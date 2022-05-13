package homeward.plugin.brewing.enumerates;

public enum ErrorEnum implements EnumBase{
    VALUE_INCORRECT("VALUE INCORRECT"),
    NAMESPACE_ID_INCORRECT("NAMESPACE ID INCORRECT, The path is virtual, Check The NAMESPACE and ID is correct"),
    NOT_ACTUALLY_NUMERIC("VALUE IS NOT NUMERIC"),
    STRING_IS_BLANK("STRING IS BLANK"),
    NUMBER_TOO_LARGE("NUMBER IS TOO BIG"),
    NUMBER_TOO_SMALL("NUMBER IS TOO SMALL"),
    NUMBER_NOT_INTEGER("NUMBER IS NOT IN INTEGER RANGE");

    private final String value;
    ErrorEnum(String value) {
        this.value = value;
    }

    @Override
    public String getString() {
        return value;
    }
}
