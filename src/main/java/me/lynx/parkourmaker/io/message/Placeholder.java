package me.lynx.parkourmaker.io.message;

public class Placeholder {

    private final String placeholder;
    private final String value;
    private final String schemeColor;

    public Placeholder(String placeholder, String value, String schemeColor) {
        this.placeholder = placeholder;
        this.value = value;
        this.schemeColor = schemeColor;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public String getValue() {
        return value;
    }

    public String getSchemeColor() {
        return schemeColor;
    }

}