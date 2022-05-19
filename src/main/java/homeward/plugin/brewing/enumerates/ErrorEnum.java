package homeward.plugin.brewing.enumerates;

public enum ErrorEnum implements EnumBase{
    VALUE_INCORRECT("Value Incorrect"),
    NAMESPACE_ID_INCORRECT("NamespacedId Incorrect, The path is virtual, Check The Namespace and Id is correct"),
    NOT_ACTUALLY_NUMERIC("Value Is Not Numeric"),
    STRING_IS_BLANK("String Is Blank"),
    NUMBER_TOO_LARGE("Number Is Too Big"),
    NUMBER_TOO_SMALL("Number Is Too Small"),
    NUMBER_NOT_INTEGER("Number Is Not In Integer Range"),
    RECIPE_LEVEL_NOT_MATCH("The Recipe Level Is Not Match Your provided.");

    private final String value;
    ErrorEnum(String value) {
        this.value = value;
    }

    @Override
    public String getString() {
        return value;
    }
}
