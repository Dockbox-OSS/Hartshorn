package org.dockbox.hartshorn.inject.processing;

import java.util.Collection;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ParameterizableType;
import org.dockbox.hartshorn.component.contextual.StaticComponentContext;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class StaticComponentPostProcessor extends ComponentPostProcessor {

    @Override
    public <T> T initializeComponent(ApplicationContext context, @Nullable T instance,
            ComponentProcessingContext<T> processingContext) {
        ComponentKey<T> componentKey = processingContext.key();

        if (Collection.class.isAssignableFrom(componentKey.type())) {
            TypeView<T> componentType = context.environment().introspect(componentKey.type());
            List<ParameterizableType<?>> parameters = componentKey.parameterizedType().parameters();
            ParameterizableType<?> elementType;
            if(parameters.size() == 1) {
                elementType = parameters.get(0);
            }
            else {
                List<TypeView<?>> collectionParameters = componentType.typeParameters().from(Collection.class);
                if (collectionParameters.size() != 1) {
                    throw new IllegalArgumentException(String.format("Cannot determine collection type for %s", componentKey.type()));
                }
                elementType = new ParameterizableType<>(collectionParameters.get(0));
            }

            ComponentKey<?> elementKey = ComponentKey.builder(elementType)
                    .name(componentKey.name())
                    .build();
            final StaticComponentContext staticComponentContext = context.first(StaticComponentContext.CONTEXT_KEY).get();
            final List<?> beans = staticComponentContext.provider().all(elementKey);

            return context.get(ConversionService.class).convert(beans, componentType.type());
        }
        return instance;
    }
}
