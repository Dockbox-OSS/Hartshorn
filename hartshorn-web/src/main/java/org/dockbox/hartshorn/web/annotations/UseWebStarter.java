package org.dockbox.hartshorn.web.annotations;

import org.dockbox.hartshorn.di.annotations.service.ServiceActivator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ServiceActivator
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UseWebStarter {
}
