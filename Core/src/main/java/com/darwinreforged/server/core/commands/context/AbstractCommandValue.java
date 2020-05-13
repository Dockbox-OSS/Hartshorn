package com.darwinreforged.server.core.commands.context;

/**
 The type Abstract command value.

 @param <T>
 the type parameter
 */
public class AbstractCommandValue<T> {

    /**
     The Value.
     */
    protected final T value;
    /**
     The Key.
     */
    protected final String key;

    /**
     Instantiates a new Abstract command value.

     @param value
     the value
     @param key
     the key
     */
    public AbstractCommandValue(T value, String key) {
        this.value = value;
        this.key = key;
    }

    /**
     Gets value.

     @return the value
     */
    public T getValue() {
        return value;
    }

    /**
     Gets key.

     @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     Is integer boolean.

     @param s
     the s

     @return the boolean
     */
    protected static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     Is double boolean.

     @param s
     the s

     @return the boolean
     */
    protected static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     Is float boolean.

     @param s
     the s

     @return the boolean
     */
    protected static boolean isFloat(String s) {
        try {
            Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
