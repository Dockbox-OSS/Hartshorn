package org.dockbox.hartshorn.launchpad.configuration;

import org.dockbox.hartshorn.inject.targets.InjectionPoint;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public interface InjectionPointDeclarationResolver {

    TypeView<?> resolve(InjectionPoint injectionPoint);
}
