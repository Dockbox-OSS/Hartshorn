package org.dockbox.hartshorn.util.problem;

import org.dockbox.hartshorn.util.option.Option;

import java.util.function.Function;

public final class SimpleProblemAttribute<T> implements ProblemAttribute<T> {

    private final String name;
    private final T defaultValue;
    private final Function<T, Option<String>> predicate;
    private final Function<T, String[]> formatter;

    private SimpleProblemAttribute(final String name, final T defaultValue, final Function<T, Option<String>> predicate, final Function<T, String[]> formatter) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.predicate = predicate;
        this.formatter = formatter;
    }

    public static <T> SimpleProblemAttribute<T> of(final String name, final T defaultValue, final Function<T, Option<String>> predicate, final Function<T, String[]> formatter) {
        return new SimpleProblemAttribute<T>(name, defaultValue, predicate, formatter);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public T defaultValue() {
        if (this.defaultValue == null) {
            throw new UnsupportedOperationException("No default value for attribute " + this.name);
        }
        return this.defaultValue;
    }

    @Override
    public Option<String> validateValue(final T value) {
        return this.predicate.apply(value);
    }

    @Override
    public String[] formatValue(final T value) {
        return this.formatter.apply(value);
    }
}
