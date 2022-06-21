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

package org.dockbox.hartshorn.data.jpa;

import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.util.Result;

import jakarta.persistence.EntityManager;

public class ProxyAttachedEntityManagerLookup implements EntityManagerLookup {

    @Override
    public Result<EntityManager> lookup(final Object target) {
        if (target instanceof EntityManagerJpaRepository jpaRepository) {
            return Result.of(jpaRepository.manager());
        }
        else if (target instanceof Proxy<?> proxy) {
            final Result<JpaRepository> repository = proxy.manager().delegate(JpaRepository.class);
            if (repository.present() && repository.get() instanceof EntityManagerJpaRepository jpaRepository) {
                return Result.of(jpaRepository.manager());
            }
        }
        return Result.empty();
    }
}
