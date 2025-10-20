package org.dockbox.sample.scopes;

import org.dockbox.hartshorn.inject.scope.DirectScopeKey;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.inject.scope.ScopeKey;

public record CustomMessageScope(String target) implements Scope {

    @Override
    public ScopeKey installableScopeType() {
        return DirectScopeKey.of(CustomMessageScope.class);
    }
}
