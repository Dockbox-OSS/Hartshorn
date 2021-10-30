package org.dockbox.hartshorn.persistence.service;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.properties.Attribute;
import org.dockbox.hartshorn.core.properties.UseFactory;
import org.dockbox.hartshorn.core.services.ProxyDelegationModifier;
import org.dockbox.hartshorn.persistence.JpaRepository;
import org.dockbox.hartshorn.persistence.annotations.UsePersistence;

public class JpaRepositoryDelegationModifier extends ProxyDelegationModifier<JpaRepository, UsePersistence> {
    @Override
    public Class<UsePersistence> activator() {
        return UsePersistence.class;
    }

    @Override
    protected Class<JpaRepository> parentTarget() {
        return JpaRepository.class;
    }

    @Override
    protected JpaRepository concreteDelegator(final ApplicationContext context, final TypeContext<? extends JpaRepository> parent, final Attribute<?>... attributes) {
        final Class<?> type = parent.typeParameters(JpaRepository.class).get(0).type();
        final Attribute[] typeAttributes = HartshornUtils.merge(attributes, new Attribute[]{ new UseFactory(type) });
        return context.get(JpaRepository.class, typeAttributes);
    }
}
