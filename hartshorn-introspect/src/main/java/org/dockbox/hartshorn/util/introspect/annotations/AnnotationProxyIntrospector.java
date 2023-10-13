package org.dockbox.hartshorn.util.introspect.annotations;

import java.lang.annotation.Annotation;

import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.ProxyIntrospector;
import org.dockbox.hartshorn.util.option.Option;

public class AnnotationProxyIntrospector<T extends Annotation> implements ProxyIntrospector<T> {

    private final T annotation;

    public AnnotationProxyIntrospector(T annotation) {
        this.annotation = annotation;
    }

    @Override
    public Class<T> targetClass() {
        return TypeUtils.adjustWildcards(annotation.annotationType(), Class.class);
    }

    @Override
    public Class<T> proxyClass() {
        return TypeUtils.adjustWildcards(annotation.getClass(), Class.class);
    }

    @Override
    public T proxy() {
        return annotation;
    }

    @Override
    public Option<T> delegate() {
        return Option.empty();
    }
}
