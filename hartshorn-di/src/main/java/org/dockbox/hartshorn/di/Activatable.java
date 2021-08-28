package org.dockbox.hartshorn.di;

import java.lang.annotation.Annotation;

public interface Activatable<A extends Annotation> {
    Class<A> activator();
}
