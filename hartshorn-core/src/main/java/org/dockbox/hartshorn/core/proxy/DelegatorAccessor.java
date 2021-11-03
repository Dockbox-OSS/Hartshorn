package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.domain.Exceptional;

public interface DelegatorAccessor<T> {
    <A> Exceptional<A> delegator(Class<A> type);
}
