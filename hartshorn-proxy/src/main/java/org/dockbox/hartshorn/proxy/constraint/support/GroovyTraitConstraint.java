package org.dockbox.hartshorn.proxy.constraint.support;

import org.dockbox.hartshorn.proxy.constraint.ProxyConstraint;
import org.dockbox.hartshorn.proxy.constraint.ProxyConstraintViolation;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.annotation.Annotation;
import java.util.Set;

public class GroovyTraitConstraint implements ProxyConstraint {

    private static final String GROOVY_TRAIT = "groovy.transform.Trait";

    @Override
    public Set<ProxyConstraintViolation> validate(final TypeView<?> typeView) {
        if (this.isGroovyTrait(typeView.type())) {
            return Set.of(new ProxyConstraintViolation("Cannot create proxy for Groovy trait " + typeView.qualifiedName()));
        }
        return Set.of();
    }

    protected boolean isGroovyTrait(final Class<?> type) {
        try {
            final Class<?> groovyTrait = Class.forName(GROOVY_TRAIT);
            return groovyTrait.isAnnotation() && type.isAnnotationPresent((Class<? extends Annotation>) groovyTrait);
        }
        catch (final ClassNotFoundException e) {
            return false;
        }
    }
}
