package org.dockbox.hartshorn.proxy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class MethodWrapperList<T> implements MethodWrapper<T> {

    private final Collection<MethodWrapper<T>> wrappers;

    @SafeVarargs
    public MethodWrapperList(final MethodWrapper<T>... wrappers) {
        this.wrappers = Arrays.asList(wrappers);
    }

    public MethodWrapperList(final Collection<MethodWrapper<T>> wrappers) {
        this.wrappers = Collections.unmodifiableCollection(wrappers);
    }

    @Override
    public void acceptBefore(final ProxyCallbackContext<T> context) {
        for (final MethodWrapper<T> wrapper : this.wrappers) {
            wrapper.acceptBefore(context);
        }
    }

    @Override
    public void acceptAfter(final ProxyCallbackContext<T> context) {
        for (final MethodWrapper<T> wrapper : this.wrappers) {
            wrapper.acceptAfter(context);
        }
    }

    @Override
    public void acceptError(final ProxyCallbackContext<T> context) {
        for (final MethodWrapper<T> wrapper : this.wrappers) {
            wrapper.acceptError(context);
        }
    }
}
