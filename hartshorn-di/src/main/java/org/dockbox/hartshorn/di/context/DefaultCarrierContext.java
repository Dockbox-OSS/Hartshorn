package org.dockbox.hartshorn.di.context;

import org.dockbox.hartshorn.api.domain.Exceptional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultCarrierContext extends DefaultContext implements CarrierContext {
    @Getter private final ApplicationContext applicationContext;

    @Override
    public <C extends Context> Exceptional<C> first(final Class<C> context) {
        return super.first(this.applicationContext(), context);
    }
}
