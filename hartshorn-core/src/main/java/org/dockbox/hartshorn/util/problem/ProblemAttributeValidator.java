package org.dockbox.hartshorn.util.problem;

import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.option.Option;

public final class ProblemAttributeValidator {

    private ProblemAttributeValidator() {
    }

    public static Option<String> instanceOf(final Class<?> type, final String what, final Object value) {
        return nonNull(what, value).orComputeFlat(() -> {
            if (type.isInstance(value)) return Option.empty();
            return Option.of(StringUtilities.format(what + " %s is not an instance of %s", value, type));
        });
    }

    public static Option<String> nonNull(final String what, final Object value) {
        return value == null
                ? Option.of(what + " cannot be null")
                : Option.empty();
    }

    public static Option<String> nonEmpty(final String what, final Object value) {
        return instanceOf(String.class, what, value).orComputeFlat(() -> {
            if (value.toString().isEmpty()) return Option.of(what + " cannot be empty");
            return Option.empty();
        });
    }
}
