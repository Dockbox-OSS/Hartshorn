package org.dockbox.hartshorn.inject;

import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ProcessingPriority;
import org.dockbox.hartshorn.profiles.ProfileProperty;
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.parse.support.ConversionServiceProfilePropertyParser;
import org.dockbox.hartshorn.profiles.parse.ProfilePropertyParser;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.option.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationValueComponentPostProcessor extends ComponentPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationValueComponentPostProcessor.class);

    @Override
    public <T> void postConfigureComponent(ApplicationContext context, @Nullable T instance, ComponentProcessingContext<T> processingContext) throws ApplicationException {
        List<FieldView<T, ?>> annotatedFields = processingContext.type().fields().annotatedWith(Value.class);
        if (annotatedFields.isEmpty()) {
            return;
        }

        ProfilePropertyRegistry propertyRegistry = context.environment().profiles().registry();
        ConversionService conversionService = context.get(ConversionService.class);

        for (FieldView<T, ?> annotatedField : annotatedFields) {
            Value value = annotatedField.annotations().get(Value.class).get();
            String key = value.value();

            Option<ProfileProperty> property = propertyRegistry.property(key);
            if (property.absent()) {
                LOG.debug("No value present for property field {}", annotatedField.qualifiedName());
                continue;
            }

            ProfilePropertyParser<?> parser = new ConversionServiceProfilePropertyParser<>(annotatedField.type().type(), conversionService);
            ProfileProperty profileProperty = property.get();
            Option<?> parsedValue = profileProperty.parseValue(parser);
            if (parsedValue.absent()) {
                LOG.debug("Could not parse value for property field {}", annotatedField.qualifiedName());
                continue;
            }

            try {
                annotatedField.set(instance, parsedValue.get());
            }
            catch (Throwable e) {
                throw new ApplicationException(e);
            }
        }
    }

    @Override
    public int priority() {
        return ProcessingPriority.NORMAL_PRECEDENCE;
    }
}
