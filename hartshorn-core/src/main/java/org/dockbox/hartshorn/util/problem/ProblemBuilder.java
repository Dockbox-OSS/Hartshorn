package org.dockbox.hartshorn.util.problem;

import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class ProblemBuilder {

    private final Map<ProblemAttribute<?>, Object> attributes = new ConcurrentHashMap<>();

    public ProblemBuilder severity(final ProblemSeverity severity) {
        return this.attribute(ProblemAttributes.SEVERITY, severity);
    }

    public ProblemBuilder message(final String message) {
        return this.attribute(ProblemAttributes.MESSAGE, message);
    }

    public ProblemBuilder shortDescription(final String shortDescription) {
        return this.attribute(ProblemAttributes.SHORT_DESCRIPTION, shortDescription);
    }

    public ProblemBuilder longDescription(final String longDescription) {
        return this.attribute(ProblemAttributes.LONG_DESCRIPTION, longDescription);
    }

    public ProblemBuilder reason(final String reason) {
        return this.attribute(ProblemAttributes.REASON, reason);
    }

    public ProblemBuilder documentationLink(final String documentationLink) {
        return this.attribute(ProblemAttributes.DOCUMENTATION_LINK, documentationLink);
    }

    public ProblemBuilder solutions(final List<String> solutions) {
        return this.attribute(ProblemAttributes.SOLUTIONS, solutions);
    }

    public ProblemBuilder solution(final String solution) {
        return this.solutions(List.of(solution));
    }

    public ProblemBuilder cause(final Throwable cause) {
        return this.attribute(ProblemAttributes.CAUSE, cause);
    }

    public ProblemBuilder origin(final Class<?> origin) {
        return this.attribute(ProblemAttributes.ORIGIN, origin);
    }

    public <V> ProblemBuilder attribute(final ProblemAttribute<V> attribute, final V value) {
        final Option<String> validation = attribute.validateValue(TypeUtils.adjustWildcards(value, Object.class));
        if (validation.present()) throw new IllegalArgumentException(validation.get());

        this.attributes.put(attribute, value);
        return this;
    }

    public ProblemBuilder attributes(final Map<ProblemAttribute<?>, Object> attributes) {
        for (final Entry<ProblemAttribute<?>, Object> entry : attributes.entrySet()) {
            this.attribute(entry.getKey(), TypeUtils.adjustWildcards(entry.getValue(), Object.class));
        }
        return this;
    }

    public ProblemBuilder attributes(final ProblemBuilder builder) {
        return this.attributes(builder.attributes);
    }

    public ProblemBuilder attributes(final Problem problem) {
        return this.attributes(problem.attributes());
    }

    public Problem build() {
        return new Problem(this.attributes);
    }
}
