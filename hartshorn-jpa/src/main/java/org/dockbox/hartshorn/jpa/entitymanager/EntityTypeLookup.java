package org.dockbox.hartshorn.jpa.entitymanager;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public interface EntityTypeLookup {

    TypeView<?> entityType(final ApplicationContext applicationContext, final MethodView<?, ?> context, final Class<?> guessedType);

}
