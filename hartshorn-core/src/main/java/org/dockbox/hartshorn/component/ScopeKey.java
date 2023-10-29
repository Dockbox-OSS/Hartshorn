package org.dockbox.hartshorn.component;

import java.util.Objects;

public class ScopeKey<T extends Scope> {

    private final Class<T> scopeType;

    protected ScopeKey(Class<T> scopeType) {
        this.scopeType = scopeType;
    }

    public String name() {
        return this.scopeType.getSimpleName();
    }

    public Class<T> scopeType() {
        return this.scopeType;
    }

    public static <T extends Scope> ScopeKey<T> of(Class<T> scopeType) {
        return new ScopeKey<>(scopeType);
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) {
            return true;
        }
        if(!(object instanceof ScopeKey<?> scopeKey)) {
            return false;
        }
        return Objects.equals(scopeType, scopeKey.scopeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scopeType);
    }
}
