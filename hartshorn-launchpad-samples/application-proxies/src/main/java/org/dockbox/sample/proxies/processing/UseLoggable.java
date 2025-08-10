package org.dockbox.sample.proxies.processing;

import org.dockbox.hartshorn.launchpad.activation.ModuleActivator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ModuleActivator(
        componentPostProcessors = {
                LoggableCallbackPostProcessor.class,
                LoggableInterceptorPostProcessor.class
        }
)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface UseLoggable {
}
