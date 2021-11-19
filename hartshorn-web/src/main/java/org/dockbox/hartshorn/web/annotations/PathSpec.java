package org.dockbox.hartshorn.web.annotations;

import org.dockbox.hartshorn.core.annotations.Extends;
import org.dockbox.hartshorn.core.annotations.service.Service;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Extends(Service.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface PathSpec {
    String pathSpec() default "";
}
