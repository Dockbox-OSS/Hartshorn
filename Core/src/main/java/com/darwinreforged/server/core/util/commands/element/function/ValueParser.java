package com.darwinreforged.server.core.util.commands.element.function;

import com.darwinreforged.server.core.util.commands.command.CommandException;
import com.darwinreforged.server.core.util.commands.command.Input;
import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;


@FunctionalInterface
public interface ValueParser<T> {

    T parse(String input) throws CommandException;

    default T parse(Input input) throws CommandException {
        return parse(input.next());
    }

    ValueParser<?> EMPTY = input -> null;

    Map<Class<?>, ValueParser<?>> DEFAULTS = ImmutableMap.<Class<?>, ValueParser<?>>builder()
            .put(byte.class, ValueParser.of(Byte::parseByte))
            .put(Byte.class, ValueParser.of(Byte::parseByte))
            .put(boolean.class, ValueParser.bool())
            .put(Boolean.class, ValueParser.bool())
            .put(double.class, ValueParser.of(Double::parseDouble))
            .put(Double.class, ValueParser.of(Double::parseDouble))
            .put(float.class, ValueParser.of(Float::parseFloat))
            .put(Float.class, ValueParser.of(Float::parseFloat))
            .put(int.class, ValueParser.of(Integer::parseInt))
            .put(Integer.class, ValueParser.of(Integer::parseInt))
            .put(long.class, ValueParser.of(Long::parseLong))
            .put(Long.class, ValueParser.of(Long::parseLong))
            .put(short.class, ValueParser.of(Short::parseShort))
            .put(Short.class, ValueParser.of(Short::parseShort))
            .put(String.class, s -> s)
            .build();

    static ValueParser<?> get(Class<?> c) {
        return DEFAULTS.getOrDefault(c, EMPTY);
    }

    static ValueParser<String> joinedString(String separator) {
        return new ValueParser<String>() {
            @Override
            public String parse(Input input) throws CommandException {
                StringBuilder sb = new StringBuilder();
                while (input.hasNext()) {
                    sb.append(sb.length() > 0 ? separator : "").append(input.next());
                }
                return sb.toString();
            }

            @Override
            public String parse(String input) throws CommandException {
                return input;
            }
        };
    }

    static ValueParser<Object> node(Collection<String> options) {
        return next -> {
            for (String option : options) {
                if (option.equalsIgnoreCase(next)) {
                    return null;
                }
            }
            throw new CommandException("Invalid arg '%s'", next);
        };
    }

    static ValueParser<Object> enumParser(Class<? extends Enum> c) {
        return input -> {
            try {
                input = input.toUpperCase();
                return Enum.valueOf(c, input);
            } catch (IllegalArgumentException e) {
                throw new CommandException("Invalid enum value '%s'", input);
            }
        };
    }

    static ValueParser<Boolean> bool() {
        return input -> {
            if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                return Boolean.valueOf(input);
            }
            throw new CommandException("Invalid boolean value '%s", input);
        };
    }

    static <T> ValueParser<T> of(Function<String, T> func) {
        return input -> {
            try {
                return func.apply(input);
            } catch (Throwable e) {
                throw new CommandException(e.getMessage());
            }
        };
    }
}
