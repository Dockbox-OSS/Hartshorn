package org.dockbox.hartshorn.proxy.constraint;

import org.dockbox.hartshorn.proxy.constraint.support.FinalClassConstraint;
import org.dockbox.hartshorn.proxy.constraint.support.GroovyTraitConstraint;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CollectorProxyValidator implements ProxyValidator {

    private final Set<ProxyConstraint> constraints = ConcurrentHashMap.newKeySet();

    public void add(final ProxyConstraint constraint) {
        this.constraints.add(constraint);
    }

    @Override
    public Set<ProxyConstraint> constraints() {
        return Set.copyOf(this.constraints);
    }

    @Override
    public Set<ProxyConstraintViolation> validate(final TypeView<?> type) {
        return constraints.stream()
                .flatMap(constraint -> constraint.validate(type).stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    public CollectorProxyValidator withDefaults() {
        this.add(new FinalClassConstraint());
        this.add(new GroovyTraitConstraint());
        return this;
    }
}
