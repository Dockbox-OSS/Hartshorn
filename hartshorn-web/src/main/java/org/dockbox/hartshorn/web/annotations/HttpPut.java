package org.dockbox.hartshorn.web.annotations;

import org.dockbox.hartshorn.util.annotations.Extends;
import org.dockbox.hartshorn.web.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Extends(HttpRequest.class)
@HttpRequest(method = HttpMethod.PUT, value = "")
public @interface HttpPut {
    String value();
}
