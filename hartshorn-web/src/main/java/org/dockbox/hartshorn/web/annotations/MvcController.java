package org.dockbox.hartshorn.web.annotations;

import org.dockbox.hartshorn.core.annotations.AliasFor;
import org.dockbox.hartshorn.core.annotations.Extends;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Extends(PathSpec.class)
public @interface MvcController {
    @AliasFor("pathSpec")
    String value() default "";
}
