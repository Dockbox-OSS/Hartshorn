package com.specific.sub;

import org.dockbox.hartshorn.core.annotations.service.AutomaticActivation;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.services.ServicePreProcessor;

import javax.inject.Singleton;

import lombok.Getter;

@AutomaticActivation
@Singleton
public class DemoServicePreProcessor implements ServicePreProcessor<Demo> {

    @Getter
    private int processed = 0;

    @Override
    public boolean preconditions(final ApplicationContext context, final TypeContext<?> type) {
        return type.is(DemoService.class);
    }

    @Override
    public <T> void process(final ApplicationContext context, final TypeContext<T> type) {
        context.log().debug("Processing %s".formatted(type.qualifiedName()));
        this.processed++;
    }

    @Override
    public Class<Demo> activator() {
        return Demo.class;
    }
}
