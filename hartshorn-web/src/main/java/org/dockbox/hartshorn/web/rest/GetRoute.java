package org.dockbox.hartshorn.web.rest;

import org.dockbox.hartshorn.util.introspect.annotations.AttributeAlias;
import org.dockbox.hartshorn.util.introspect.annotations.Extends;
import org.dockbox.hartshorn.web.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Extends(HttpRoute.class)
@HttpRoute(method = HttpMethod.GET, path = "")
public @interface GetRoute {
    @AttributeAlias(value = "path", target = HttpRoute.class)
    String value();
}
