package org.dockbox.hartshorn.test;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.annotations.activate.Activator;
import org.dockbox.hartshorn.di.context.element.TypeContext;

import java.lang.annotation.Annotation;

public class JUnitActivatorContext<T> extends TypeContext<T> {

    protected JUnitActivatorContext(final Class<T> type) {
        super(type);
    }

    @Override
    public <A extends Annotation> Exceptional<A> annotation(final Class<A> annotation) {
        if (Activator.class.equals(annotation)) return Exceptional.of((A) HartshornRunner.activator());
        else return super.annotation(annotation);
    }
}
