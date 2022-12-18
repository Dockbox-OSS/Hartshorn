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

package test.org.dockbox.hartshorn.jpa;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.jpa.JpaRepository;
import org.dockbox.hartshorn.jpa.annotations.DataSource;
import org.dockbox.hartshorn.jpa.annotations.Query;
import org.dockbox.hartshorn.jpa.query.FlushMode;
import org.dockbox.hartshorn.jpa.query.LockMode;

import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;

@Service(lazy = true)
@DataSource("users")
public interface LockFlushModeUserJpaRepository extends JpaRepository<User, Long> {

    @LockMode(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM users u WHERE u.name = 'Waldo'")
    default void findWaldoWithLockMode() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @FlushMode(FlushModeType.COMMIT)
    @Query("SELECT u FROM users u WHERE u.name = 'Waldo'")
    default void findWaldoWithFlushMode() {
        throw new UnsupportedOperationException("Not implemented");
    }

}
