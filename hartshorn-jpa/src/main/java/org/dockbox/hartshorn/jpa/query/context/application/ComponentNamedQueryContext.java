package org.dockbox.hartshorn.jpa.query.context.application;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import jakarta.persistence.Entity;

public class ComponentNamedQueryContext extends DefaultContext {

    private final String name;
    private final boolean nativeQuery;
    private final String query;
    private final TypeView<?> declaredBy;

    private boolean automaticFlush = true;
    private boolean automaticClear = false;

    public ComponentNamedQueryContext(final String name, final boolean nativeQuery, final String query,
                                      final TypeView<?> declaredBy) {
        this.name = name;
        this.nativeQuery = nativeQuery;
        this.query = query;
        this.declaredBy = declaredBy;
    }

    public String name() {
        return this.name;
    }

    public boolean nativeQuery() {
        return this.nativeQuery;
    }

    public String query() {
        return this.query;
    }

    public TypeView<?> declaredBy() {
        return this.declaredBy;
    }

    public boolean automaticFlush() {
        return this.automaticFlush;
    }

    public ComponentNamedQueryContext automaticFlush(final boolean automaticFlush) {
        this.automaticFlush = automaticFlush;
        return this;
    }

    public boolean automaticClear() {
        return this.automaticClear;
    }

    public ComponentNamedQueryContext automaticClear(final boolean automaticClear) {
        this.automaticClear = automaticClear;
        return this;
    }

    public boolean isEntityDeclaration() {
        return this.declaredBy.annotations().has(Entity.class);
    }
}
