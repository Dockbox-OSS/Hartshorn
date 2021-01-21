package org.dockbox.selene.core.delegate;

public interface DelegationFunction<T, R> {

    R delegate(T instance, Object[] args, DelegationHolder holder);
}
