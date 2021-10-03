package org.dockbox.hartshorn.demo.commands.services;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.commands.arguments.ArgumentConverterImpl;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.di.annotations.service.Service;

@Service
public class ArgumentConverterService {

    public static final ArgumentConverter<String> GREETER = ArgumentConverterImpl.builder(String.class, "greeting")
            .withConverter(input -> Exceptional.of("Hello %s".formatted(input)))
            .build();

}
