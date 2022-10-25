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

package test.org.dockbox.hartshorn.data;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.data.annotations.DataSource;
import org.dockbox.hartshorn.data.annotations.EntityModifier;
import org.dockbox.hartshorn.data.annotations.Query;
import org.dockbox.hartshorn.data.annotations.Transactional;
import org.dockbox.hartshorn.data.jpa.JpaRepository;

import java.util.List;

import test.org.dockbox.hartshorn.data.objects.JpaUser;

@Service(lazy = true)
@DataSource("users")
public interface UserQueryRepository extends JpaRepository<JpaUser, Long> {

    @Query("select u from JpaUser u where u.age >= 18")
    List<JpaUser> findAdults();

    @Transactional
    @EntityModifier
    @Query("delete from JpaUser u")
    void deleteAll();

    @EntityModifier
    @Query("update JpaUser u set u.age = :age where u.id = :id")
    int nonTransactionalEntityUpdate(long id, int age);

    @Transactional
    @Query("update JpaUser u set u.age = :age where u.id = :id")
    int nonModifierEntityUpdate(long id, int age);

    @EntityModifier
    @Transactional
    @Query("update JpaUser u set u.age = :age where u.id = :id")
    int entityUpdate(long id, int age);
}
