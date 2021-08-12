package org.dockbox.hartshorn.di.binding;

import org.dockbox.hartshorn.api.domain.Exceptional;

public interface Provider<T> {
    Exceptional<T> provide();
}
