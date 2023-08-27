package org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.context.Context;

/**
 * A marker interface for classes that can be configured. This interface is used to allow for
 * context-attached configurers to be used in {@link Customizer}s.
 */
public interface Configurer extends Context {
}
