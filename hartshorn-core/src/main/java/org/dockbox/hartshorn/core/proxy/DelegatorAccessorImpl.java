package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.annotations.inject.Bound;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

@Binds(DelegatorAccessor.class)
@RequiredArgsConstructor(onConstructor_ = @Bound)
public class DelegatorAccessorImpl<T> implements DelegatorAccessor<T> {

    @Inject
    private ApplicationContext context;
    private final ProxyHandler<T> handler;

    @Override
    public <A> Exceptional<A> delegator(final Class<A> type) {
        if (!this.handler.type().childOf(type)) return Exceptional.empty();
        final TypeContext<A> typeContext = TypeContext.of(type);
        return this.context.environment().application().delegator(typeContext, (ProxyHandler<A>) this.handler);
    }
}
