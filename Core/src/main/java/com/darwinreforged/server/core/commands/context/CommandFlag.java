package com.darwinreforged.server.core.commands.context;

/**
 The type Command flag.

 @param <T>
 the type parameter
 */
public class CommandFlag<T> extends AbstractCommandValue<T> {

    /**
     Instantiates a new Command flag.

     @param value
     the value
     @param key
     the key
     */
    public CommandFlag(T value, String key) {
        super(value, key);
    }

    /**
     Value of command flag.

     @param key
     the key
     @param value
     the value

     @return the command flag
     */
    public static CommandFlag<?> valueOf(String key, String value) {
        if (value != null) {
            // Boolean flag, by default will return false if the value is true, ignoring case. Therefore
            // any other values will return false, which in this case should be prevented.
            if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
                Boolean bool = Boolean.parseBoolean(value);
                return new CommandFlag<Boolean>(bool, key);
            }

            // Number flag, if the number can be parsed, we have a number flag. If a NumberFormatException
            // occurs there is no number present.
            try {
                if (isInteger(value)) return new CommandFlag<Integer>(Integer.parseInt(value), key);
                if (isDouble(value)) return new CommandFlag<Double>(Double.parseDouble(value), key);
                if (isFloat(value)) return new CommandFlag<Float>(Float.parseFloat(value), key);
            } catch (NumberFormatException ignored) {}

            // Flag is neither a number or boolean, default to String value
            return new CommandFlag<String>(value, key);
        } else return new CommandFlag<Void>(null, key);
    }
}
