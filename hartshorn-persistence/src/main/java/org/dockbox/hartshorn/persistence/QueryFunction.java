package org.dockbox.hartshorn.persistence;

import org.dockbox.hartshorn.persistence.context.QueryContext;

public interface QueryFunction {
    Object execute(QueryContext context);
}
