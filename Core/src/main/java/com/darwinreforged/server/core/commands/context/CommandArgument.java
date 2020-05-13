package com.darwinreforged.server.core.commands.context;

/**
 The type Command argument.

 @param <T>
 the type parameter
 */
public class CommandArgument<T> extends AbstractCommandValue<T> {

    private final boolean joined;

    /**
     Instantiates a new Command argument.

     @param argument
     the argument
     @param joined
     the joined
     @param key
     the key
     */
    public CommandArgument(T argument, boolean joined, String key) {
        super(argument, key);
        this.joined = joined;
    }

    /**
     Is joined boolean.

     @return the boolean
     */
    public boolean isJoined() {
        return joined;
    }

    /**
     Value of command argument.

     @param value
     the value
     @param joined
     the joined
     @param key
     the key

     @return the command argument
     */
    public static CommandArgument<?> valueOf(String value, boolean joined, String key) {
        if (value != null && !joined) {
            // Boolean argument, by default will return false if the value is true, ignoring case. Therefore
            // any other values will return false, which in this case should be prevented.
            if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
                Boolean bool = Boolean.parseBoolean(value);
                return new CommandArgument<Boolean>(bool, false, key);
            }

            // Number argument, if the number can be parsed, we have a number flag. If a NumberFormatException
            // occurs there is no number present.
            try {
                if (isInteger(value)) return new CommandArgument<Integer>(Integer.parseInt(value), false, key);
                if (isDouble(value)) return new CommandArgument<Double>(Double.parseDouble(value), false, key);
                if (isFloat(value)) return new CommandArgument<Float>(Float.parseFloat(value), false, key);
            } catch (NumberFormatException ignored) {}
        }
        // Argument is neither a number or boolean, default to String value
        return new CommandArgument<String>(value, joined, key);
    }
}
