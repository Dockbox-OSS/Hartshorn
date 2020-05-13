package com.darwinreforged.server.core.commands.context;

public class AbstractCommandValue<T> {

    protected final T value;
    protected final String key;

    public AbstractCommandValue(T value, String key) {
        this.value = value;
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    protected static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    protected static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    protected static boolean isFloat(String s) {
        try {
            Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
