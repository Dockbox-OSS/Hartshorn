package org.dockbox.hartshorn.util.introspect.annotations;

import java.lang.annotation.Annotation;

import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.ProxyIntrospector;
import org.dockbox.hartshorn.util.option.Option;

public class AnnotationAdapterProxyIntrospector<T extends Annotation> implements ProxyIntrospector<T> {

    private final Annotation annotation;
    private final AnnotationAdapterProxy<T> adapterProxy;

    public AnnotationAdapterProxyIntrospector(Annotation annotation, AnnotationAdapterProxy<T> adapterProxy) {
        this.annotation = annotation;
        this.adapterProxy = adapterProxy;
    }

    @Override
    public Class<T> targetClass() {
        return adapterProxy.targetAnnotationClass();
    }

    @Override
    public Class<T> proxyClass() {
        return TypeUtils.adjustWildcards(annotation.getClass(), Class.class);
    }

    @Override
    public T proxy() {
        return adapterProxy.targetAnnotationClass().cast(annotation);
    }

    @Override
    public Option<T> delegate() {
        return Option.of(adapterProxy.targetAnnotationClass().cast(adapterProxy.actual()));
    }
}
