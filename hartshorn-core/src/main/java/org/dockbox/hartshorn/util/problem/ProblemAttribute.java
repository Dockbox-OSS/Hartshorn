package org.dockbox.hartshorn.util.problem;

import org.dockbox.hartshorn.util.option.Option;

public interface ProblemAttribute<T> {

    String name();

    T defaultValue();

    Option<String> validateValue(T value);

    String[] formatValue(T value);
}
