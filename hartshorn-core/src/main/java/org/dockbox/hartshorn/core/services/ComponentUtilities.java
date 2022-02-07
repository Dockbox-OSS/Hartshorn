package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.StringUtilities;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.Locale;
import java.util.function.Function;

public class ComponentUtilities {

    public static String id(final ApplicationContext context, final TypeContext<?> type) {
        return id(context, type, false);
    }

    public static String id(final ApplicationContext context, final TypeContext<?> type, final boolean ignoreExisting) {
        return format(context, type, ignoreExisting, '-', ComponentContainer::id).toLowerCase(Locale.ROOT);
    }

    public static String name(final ApplicationContext context, final TypeContext<?> type, final boolean ignoreExisting) {
        return format(context, type, ignoreExisting, ' ', ComponentContainer::name);
    }

    protected static String format(final ApplicationContext context, final TypeContext<?> type, final boolean ignoreExisting, final char delimiter, final Function<ComponentContainer, String> attribute) {
        final Exceptional<ComponentContainer> container = context.locator().container(type);
        if (!ignoreExisting && container.present()) {
            final String name = attribute.apply(container.get());
            if (!"".equals(name)) return name;
        }

        String raw = type.name();
        if (raw.endsWith("Service")) {
            raw = raw.substring(0, raw.length() - 7);
        }
        final String[] parts = StringUtilities.splitCapitals(raw);
        return StringUtilities.capitalize(String.join(delimiter + "", parts));
    }
}
