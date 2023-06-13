package org.dockbox.hartshorn.inject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO: Document why this exists
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface HandledInjection {
}
