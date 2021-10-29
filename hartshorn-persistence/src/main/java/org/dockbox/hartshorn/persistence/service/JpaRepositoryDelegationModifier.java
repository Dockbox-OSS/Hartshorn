package org.dockbox.hartshorn.persistence.service;

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
}
