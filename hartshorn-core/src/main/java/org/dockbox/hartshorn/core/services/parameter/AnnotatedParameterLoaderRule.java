package org.dockbox.hartshorn.core.services.parameter;

import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;

import java.lang.annotation.Annotation;

public abstract class AnnotatedParameterLoaderRule<A extends Annotation, C extends ParameterLoaderContext> implements ParameterLoaderRule<C>{

    protected abstract Class<A> annotation();

    @Override
    public boolean accepts(final ParameterContext<?> parameter, final C context, final Object... args) {
        return parameter.annotation(this.annotation()).present();
    }
}
