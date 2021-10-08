package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.di.annotations.context.AutoCreating;
import org.dockbox.hartshorn.di.context.DefaultContext;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Set;

import lombok.Getter;

@AutoCreating
public class ControllerContext extends DefaultContext {

    @Getter
    private final Set<RequestHandlerContext> contexts = HartshornUtils.emptySet();

    public void add(final RequestHandlerContext context) {
        this.contexts.add(context);
    }
}
