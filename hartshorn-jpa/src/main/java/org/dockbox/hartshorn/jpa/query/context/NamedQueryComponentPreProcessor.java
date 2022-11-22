package org.dockbox.hartshorn.jpa.query.context;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.jpa.query.context.application.ApplicationNamedQueriesContext;

public class NamedQueryComponentPreProcessor extends ComponentPreProcessor {

    @Override
    public <T> void process(final ApplicationContext context, final ComponentProcessingContext<T> processingContext) {
        final ApplicationNamedQueriesContext queriesContext = context.first(ApplicationNamedQueriesContext.class).get();
        queriesContext.process(processingContext.type());
    }
}
