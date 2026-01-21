package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.launchpad.activation.ModuleActivator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ModuleActivator
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UseWebServer {
}
