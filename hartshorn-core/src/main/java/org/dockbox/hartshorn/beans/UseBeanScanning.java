package org.dockbox.hartshorn.beans;

import org.dockbox.hartshorn.component.processing.ServiceActivator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ServiceActivator(processors = {
        BeanServicePreProcessor.class,
})
public @interface UseBeanScanning {
}
