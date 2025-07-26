package org.dockbox.sample.java.components;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.component.AnnotatedComponentContainer;
import org.dockbox.hartshorn.inject.component.ApplicationMainComponentContainer;
import org.dockbox.hartshorn.inject.component.ComponentContainer;
import org.dockbox.hartshorn.inject.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.inject.processing.ProcessingPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomComponentPreProcessor extends ComponentPreProcessor {

    private final Logger logger = LoggerFactory.getLogger(CustomComponentPreProcessor.class);

    @Override
    public <T> void process(InjectionCapableApplication application, ComponentProcessingContext<T> processingContext) {
        // Processing context can contain additional data. For pre-processing, this always contains the component container
        // which describes the managed component.
        ComponentContainer<?> container = processingContext.get(ComponentContainer.class);
        String containerType = switch(container) {
            // The main component is the class which was passed to the build context. Typically, this is the class from which
            // HartshornApplication.create is called.
            case ApplicationMainComponentContainer<?> mainComponent -> "main";
            // Annotated components are components which are annotated with @Component, @Service, etc.
            case AnnotatedComponentContainer<?> annotatedComponent -> "annotated";
            // Other cases can be added as needed, such as for custom containers or specific types of components. These do not however
            // fall within the default Hartshorn component types.
            default -> "managed";
        };
        this.logger.info("Pre-processing {} component: {}", containerType, processingContext.key().type().getSimpleName());
    }

    @Override
    public int priority() {
        return ProcessingPriority.NORMAL_PRECEDENCE;
    }
}
