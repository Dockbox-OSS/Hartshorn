package com.darwinreforged.server.core.util.commands.element;

import com.darwinreforged.server.core.util.commands.command.CommandException;
import com.darwinreforged.server.core.util.commands.command.Context;
import com.darwinreforged.server.core.util.commands.command.Input;
import com.darwinreforged.server.core.util.commands.element.function.ChainOptions;
import com.darwinreforged.server.core.util.commands.element.function.ChainParser;
import com.darwinreforged.server.core.util.commands.element.function.Filter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class ChainElement<D, T> implements Element {

    private final String key;
    private final Class<D> dependency;
    private final ChainParser<D, T> mapper;
    private final ChainOptions<D> options;
    private final Filter filter;

    protected ChainElement(Builder<D, T> builder) {
        key = builder.key;
        dependency = builder.dependency;
        mapper = builder.mapper;
        options = builder.options;
        filter = builder.filter;
    }

    public String getKey() {
        return key;
    }

    public Class<D> getDependency() {
        return dependency;
    }

    @Override
    public void parse(Input input, Context context) throws CommandException {
        D d = context.getLast(dependency.getCanonicalName());
        if (d == null) {
            throw new CommandException("No %s present", dependency.getSimpleName());
        }

        T t = mapper.map(input, d);
        if (t == null) {
            throw new CommandException("Unable to parse a value for %s", d);
        }

        context.add(key, t);
    }

    @Override
    public void suggest(Input input, Context context, List<String> suggestions) {
        if(!input.hasNext()) {
            Collection<String> options = getOptions(context);
            suggestions.addAll(options);
        } else {
            try {
                String e = input.next();
                Collection<String> options = getOptions(context);
                if(options.isEmpty()) {
                    return;
                }

                if (options.stream().anyMatch(s -> s.equalsIgnoreCase(e))) {
                    return;
                }

                String upper = e.toUpperCase();
                for (String option : options) {
                    if (getFilter().test(option, upper)) {
                        suggestions.add(option);
                    }
                }
            } catch (CommandException e) {
                e.printStackTrace();
            }
        }
    }

    public Filter getFilter() {
        return filter;
    }

    public Collection<String> getOptions(Context context) {
        D d = context.getLast(dependency.getCanonicalName());

        if (d == null) {
            return Collections.emptyList();
        }

        return options.get(d).collect(Collectors.toList());
    }

    public static <D, T> Builder<D, T> builder() {
        return new Builder<>();
    }

    public static class Builder<D, T> {

        private String key;
        private Class<D> dependency;
        private ChainParser<D, T> mapper;
        private ChainOptions<D> options;
        private Filter filter;

        public Builder<D, T> key(String key) {
            this.key = key;
            return this;
        }

        public Builder<D, T> dependency(Class<D> dependency) {
            this.dependency = dependency;
            return this;
        }

        public Builder<D, T> mapper(ChainParser<D, T> mapper) {
            this.mapper = mapper;
            return this;
        }

        public Builder<D, T> options(ChainOptions<D> options) {
            this.options = options;
            return this;
        }

        public Builder<D, T> filter(Filter filter) {
            this.filter = filter;
            return this;
        }

        public ChainElement<D, T> build() {
            return new ChainElement<>(this);
        }
    }
}
