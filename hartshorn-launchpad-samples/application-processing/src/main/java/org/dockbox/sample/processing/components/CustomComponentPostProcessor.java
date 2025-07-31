package org.dockbox.sample.processing.components;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.inject.processing.ProcessingPriority;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.sample.processing.SimpleHelloWorldSupplier;
import org.slf4j.Logger;

public class CustomComponentPostProcessor extends ComponentPostProcessor {

    @Inject
    private Logger logger;

    @Override
    public <T> boolean isCompatible(ComponentProcessingContext<T> processingContext) {
        // Check whether this component post-processor is compatible with the given processing context.
        T instance = processingContext.instance();
        this.logger.info("Checking compatibility for key: {}, with instance {}",
            processingContext.key().type().getSimpleName(),
            instance
        );
        return super.isCompatible(processingContext);
    }

    @Override
    public <T> void preConfigureComponent(InjectionCapableApplication application, @Nullable T instance,
        ComponentProcessingContext<T> processingContext) throws ApplicationException {
        // Early configuration, does not typically involve the instance directly
        super.preConfigureComponent(application, instance, processingContext);
    }

    @Override
    public <T> T initializeComponent(InjectionCapableApplication application, @Nullable T instance,
        ComponentProcessingContext<T> processingContext) throws ApplicationException {
        // This is where the component is initialized, if possible
        return super.initializeComponent(application, instance, processingContext);
    }

    @Override
    public <T> void postConfigureComponent(InjectionCapableApplication application, @Nullable T instance,
        ComponentProcessingContext<T> processingContext) throws ApplicationException {
        // Post-configuration, typically involves the instance
        if (instance instanceof SimpleHelloWorldSupplier simpleHelloWorldSupplier) {
            this.logger.info("Post-configuring a hello world supplier instance: {}", simpleHelloWorldSupplier);
            simpleHelloWorldSupplier.message("Hello from CustomComponentPostProcessor!");
        }
        super.postConfigureComponent(application, instance, processingContext);
    }
}
