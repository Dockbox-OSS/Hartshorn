package org.dockbox.hartshorn.testsuite;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TestBinding {
    Class<?> type();
    Class<?> implementation();
}
