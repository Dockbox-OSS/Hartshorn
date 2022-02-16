/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.data.hibernate;

import org.dockbox.hartshorn.core.annotations.inject.Provider;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.data.QueryFunction;
import org.dockbox.hartshorn.data.TransactionManager;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.data.jpa.JpaRepository;

@Service(activators = UsePersistence.class, requires = "org.hibernate.Hibernate")
public class HibernateProviders {

    @Provider
    public Class<? extends JpaRepository> jpaRepository() {
        return HibernateJpaRepository.class;
    }

    @Provider
    public Class<? extends TransactionManager> transactionManager() {
        return HibernateTransactionManager.class;
    }

    @Provider
    public QueryFunction queryFunction() {
        return new HibernateQueryFunction();
    }

    @Provider
    public Class<? extends HibernateRemote> remote() {
        return HibernateRemoteImpl.class;
    }
}
