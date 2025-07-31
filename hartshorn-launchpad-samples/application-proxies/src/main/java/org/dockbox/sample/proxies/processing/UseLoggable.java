package org.dockbox.sample.proxies.processing;

import org.dockbox.hartshorn.launchpad.activation.ServiceActivator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ServiceActivator(
        componentPostProcessors = LoggableProxyPostProcessor.class
)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface UseLoggable {
}
