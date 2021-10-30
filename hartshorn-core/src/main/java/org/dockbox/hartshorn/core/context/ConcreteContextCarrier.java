package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.annotations.inject.Binds;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.Getter;

@Binds(ContextCarrier.class)
@Singleton
public class ConcreteContextCarrier implements ContextCarrier {
    @Inject
    @Getter
    private ApplicationContext applicationContext;
}
