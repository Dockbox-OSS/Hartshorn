package org.dockbox.hartshorn.core.context.element;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

public interface ObtainableElement<R> {
    Exceptional<R> obtain(ApplicationContext applicationContext);
}
