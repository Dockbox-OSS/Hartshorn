package org.dockbox.hartshorn.util.problem;

import org.dockbox.hartshorn.util.TypeUtils;

import java.util.Map;

public class Problem {

    private final Map<ProblemAttribute<?>, Object> attributes;

    public Problem(final Map<ProblemAttribute<?>, Object> attributes) {
        this.attributes = attributes;
    }

    public <T> T attribute(final ProblemAttribute<T> attribute) {
        return (T) this.attributes.get(attribute);
    }

    public boolean hasAttribute(final ProblemAttribute<?> attribute) {
        return this.attributes.containsKey(attribute);
    }

    public String[] formattedAttribute(final ProblemAttribute<?> attribute) {
        final Object value = this.attribute(attribute);
        return attribute.formatValue(TypeUtils.adjustWildcards(value, Object.class));
    }

    public Map<ProblemAttribute<?>, Object> attributes() {
        return Map.copyOf(this.attributes);
    }
}
