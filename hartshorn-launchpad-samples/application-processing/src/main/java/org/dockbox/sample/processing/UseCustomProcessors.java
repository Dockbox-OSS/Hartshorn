package org.dockbox.sample.processing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.dockbox.hartshorn.launchpad.activation.ModuleActivator;
import org.dockbox.sample.processing.bindings.CustomBindingPostProcessor;
import org.dockbox.sample.processing.components.CustomComponentPostProcessor;
import org.dockbox.sample.processing.components.CustomComponentPreProcessor;

@ModuleActivator(
    binderPostProcessors = CustomBindingPostProcessor.class,
    componentPostProcessors = CustomComponentPostProcessor.class,
    componentPreProcessors = CustomComponentPreProcessor.class
)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
public @interface UseCustomProcessors {
}
