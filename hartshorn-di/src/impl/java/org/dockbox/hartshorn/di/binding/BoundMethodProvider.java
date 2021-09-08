package org.dockbox.hartshorn.di.binding;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.di.properties.UseFactory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BoundMethodProvider<C> implements Provider<C> {

    @Getter
    private MethodContext<C, Object> method;

    @Override
    public Exceptional<C> provide(final ApplicationContext context, final Attribute<?>... attributes) {
        final Exceptional<Object[]> factoryArgs = Bindings.lookup(UseFactory.class, attributes);
        if (factoryArgs.absent()) return Exceptional.empty();
        final Object[] arguments = factoryArgs.get();

        if (arguments.length != this.method().parameterCount()) return Exceptional.empty();
        final Object instance = context.get(this.method().parent());
        return this.method().invoke(instance, arguments);
    }
}
