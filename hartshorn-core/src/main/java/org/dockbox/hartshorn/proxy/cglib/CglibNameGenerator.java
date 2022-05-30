package org.dockbox.hartshorn.proxy.cglib;

import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;

public class CglibNameGenerator implements NamingPolicy {

    @Override
    public String getClassName(final String prefix, final String source, final Object key, final Predicate names) {
        return null;
    }
}
