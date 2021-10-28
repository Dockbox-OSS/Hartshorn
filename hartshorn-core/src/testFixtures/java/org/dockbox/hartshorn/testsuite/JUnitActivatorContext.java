package org.dockbox.hartshorn.testsuite;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.annotations.activate.Activator;
import org.dockbox.hartshorn.core.context.element.TypeContext;

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
