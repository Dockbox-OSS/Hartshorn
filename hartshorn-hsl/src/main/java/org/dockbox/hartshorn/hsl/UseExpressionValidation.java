package org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.component.factory.UseFactoryServices;
import org.dockbox.hartshorn.component.processing.ServiceActivator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ServiceActivator
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@UseFactoryServices
public @interface UseExpressionValidation {
}
