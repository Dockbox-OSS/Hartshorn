package com.nonregistered;

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.services.ComponentLocatorImpl;
import org.mockito.Mockito;
import org.slf4j.Logger;

public class ThrowingComponentLocatorImpl extends ComponentLocatorImpl {

    private final ApplicationContext proxy;
    private boolean mock = false;

    public ThrowingComponentLocatorImpl(final ApplicationContext applicationContext) {
        super(applicationContext);
        this.proxy = Mockito.spy(applicationContext);
    }

    @Override
    public ApplicationContext applicationContext() {
        if (this.mock) return this.proxy;
        return super.applicationContext();
    }

    @Override
    public <T> void validate(final Key<T> key) {
        this.mock = true;
        final Logger logger = Mockito.spy(this.proxy.log());
        Mockito.doAnswer(invocation -> {
            final String message = invocation.getArgument(0, String.class);
            throw new ApplicationException(message);
        }).when(logger).warn(Mockito.anyString());
        Mockito.when(this.proxy.log()).thenReturn(logger);
        super.validate(key);
        this.mock = false;
    }
}
