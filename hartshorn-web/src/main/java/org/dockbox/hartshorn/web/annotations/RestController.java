package org.dockbox.hartshorn.web.annotations;

import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.util.annotations.Extends;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Extends(Service.class)
public @interface RestController {
    String value();
}
