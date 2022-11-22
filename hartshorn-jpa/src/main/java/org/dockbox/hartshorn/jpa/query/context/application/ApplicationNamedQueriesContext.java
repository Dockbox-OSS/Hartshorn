package org.dockbox.hartshorn.jpa.query.context.application;

import org.dockbox.hartshorn.context.AutoCreating;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;

@AutoCreating
public class ApplicationNamedQueriesContext extends DefaultContext {

    private final Map<String, ComponentNamedQueryContext> namedQueries = new ConcurrentHashMap<>();

    public void add(final ComponentNamedQueryContext context) {
        this.namedQueries.put(context.name(), context);
    }

    public Option<ComponentNamedQueryContext> get(final String name) {
        return Option.of(() -> this.namedQueries.get(name));
    }

    public boolean contains(final String name) {
        return this.namedQueries.containsKey(name);
    }

    public Map<String, ComponentNamedQueryContext> namedQueries() {
        return this.namedQueries;
    }

    public void process(final TypeView<?> type) {
        final ElementAnnotationsIntrospector annotations = type.annotations();

        for (final NamedQuery namedQuery : annotations.all(NamedQuery.class)) {
            final ComponentNamedQueryContext queryContext = new ComponentNamedQueryContext(namedQuery.name(), false, namedQuery.query(), type);
            this.add(queryContext);
        }

        for (final NamedNativeQuery namedNativeQuery : annotations.all(NamedNativeQuery.class)) {
            final ComponentNamedQueryContext queryContext = new ComponentNamedQueryContext(namedNativeQuery.name(), true, namedNativeQuery.query(), type);
            this.add(queryContext);
        }
    }
}
