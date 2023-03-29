package org.dockbox.hartshorn.util.introspect.convert.support;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProvider;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProviderFactory;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Collection;

public class CollectionDefaultValueProviderFactory implements DefaultValueProviderFactory<Collection<?>> {

    private final Introspector introspector;

    public CollectionDefaultValueProviderFactory(final Introspector introspector) {
        this.introspector = introspector;
    }

    @Override
    public <O extends Collection<?>> DefaultValueProvider<O> create(final Class<O> targetType) {
        TypeView<O> type = introspector.introspect(targetType);
        Option<ConstructorView<O>> defaultConstructor = type.constructors().defaultConstructor();
        if (defaultConstructor.present()) {
            return () -> defaultConstructor.get().create().orNull();
        }
        // TODO: Default bindings for interfaces (e.g. Set->HashSet, List->ArrayList, etc.)
        return () -> null;
    }
}
