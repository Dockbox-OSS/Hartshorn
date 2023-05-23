package org.dockbox.hartshorn.proxy.constraint.support;

import org.dockbox.hartshorn.proxy.constraint.ProxyConstraint;
import org.dockbox.hartshorn.proxy.constraint.ProxyConstraintViolation;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Set;

public class FinalClassConstraint implements ProxyConstraint {

    @Override
    public Set<ProxyConstraintViolation> validate(final TypeView<?> typeView) {
        String classType = null;
        if (typeView.isSealed()) classType = "sealed class";
        else if (typeView.isRecord()) classType = "record";
        else if (typeView.modifiers().isFinal()) classType = "final class";

        if (classType != null) {
            return Set.of(new ProxyConstraintViolation("Cannot create proxy for " + classType + " " + typeView.qualifiedName()));
        }
        return Set.of();
    }
}
