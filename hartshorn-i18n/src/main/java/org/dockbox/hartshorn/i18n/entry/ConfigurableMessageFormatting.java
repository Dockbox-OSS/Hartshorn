package org.dockbox.hartshorn.i18n.entry;

import org.dockbox.hartshorn.config.annotations.Value;
import org.dockbox.hartshorn.di.annotations.service.Service;

import lombok.Getter;

@Service
public class ConfigurableMessageFormatting implements MessageFormatting {

    @Getter @Value("hartshorn.i18n.primary")
    private final String primary = "b";

    @Getter @Value("hartshorn.i18n.primary")
    private final String secondary = "b";

    @Getter @Value("hartshorn.i18n.primary")
    private final String minor = "b";

    @Getter @Value("hartshorn.i18n.primary")
    private final String error = "b";
}
