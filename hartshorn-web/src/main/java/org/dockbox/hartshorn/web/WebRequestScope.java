package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.inject.scope.DirectScopeKey;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.inject.scope.ScopeKey;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;

public record WebRequestScope(
        WebRequest request,
        WebResponse response
) implements Scope {

    @Override
    public ScopeKey installableScopeType() {
        return DirectScopeKey.of(WebRequestScope.class);
    }
}
