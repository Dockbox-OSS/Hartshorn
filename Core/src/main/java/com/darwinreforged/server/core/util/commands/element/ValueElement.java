package com.darwinreforged.server.core.util.commands.element;

import java.util.Comparator;
import java.util.List;
import com.darwinreforged.server.core.util.commands.command.CommandException;
import com.darwinreforged.server.core.util.commands.command.Context;
import com.darwinreforged.server.core.util.commands.command.Input;
import com.darwinreforged.server.core.util.commands.element.function.Filter;
import com.darwinreforged.server.core.util.commands.element.function.Options;
import com.darwinreforged.server.core.util.commands.element.function.ValueParser;


public class ValueElement implements Element {

    private final String key;
    private final int priority;
    private final Filter filter;
    private final Options options;
    private final ValueParser<?> parser;

    public ValueElement(String key, int priority, Options options, Filter filter, ValueParser<?> parser) {
        this.key = key;
        this.priority = priority;
        this.filter = filter;
        this.options = options;
        this.parser = parser;
    }

    @Override
    public String toString() {
        return "Value: " + key;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void parse(Input input, Context context) throws CommandException {
        Object value = parser.parse(input);
        context.add(getKey(), value);
    }

    @Override
    public void suggest(Input input, Context context, List<String> suggestions) {
        if (!input.hasNext()) {
            getOptions().get().sorted().forEach(suggestions::add);
            return;
        }

        try {
            String next = input.next();
            if (getOptions() == Options.EMPTY) {
                return;
            }

            if (getOptions().get().anyMatch(s -> s.equalsIgnoreCase(next))) {
                return;
            }

            Comparator<String> sorter = sorter(next);
            getOptions().get().filter(s -> getFilter().test(s, next)).sorted(sorter).forEach(suggestions::add);
        } catch (CommandException e) {
            e.printStackTrace();
        }
    }

    public String getKey() {
        return key;
    }

    public ValueParser<?> getParser() {
        return parser;
    }

    public Options getOptions() {
        return options;
    }

    public Filter getFilter() {
        return filter;
    }

    protected Comparator<String> sorter(String input) {
        String match = input.toLowerCase();
        return (s1, s2) -> {
            int i1 = s1.toLowerCase().indexOf(match);
            int i2 = s2.toLowerCase().indexOf(match);

            if (i1 == i2) {
                i1 = s1.length();
                i2 = s2.length();
                if (i1 == i2) {
                    return s1.compareTo(s2);
                }
            }
            return Integer.compare(i1, i2);
        };
    }
}
