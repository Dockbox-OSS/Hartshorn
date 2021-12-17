package com.specific.sub;

import org.dockbox.hartshorn.core.annotations.service.ServiceActivator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ServiceActivator(scanPackages = "com.specific")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Demo {
}
