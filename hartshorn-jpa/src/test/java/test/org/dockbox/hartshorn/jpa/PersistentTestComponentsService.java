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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.jpa.annotations.UsePersistence;
import org.dockbox.hartshorn.jpa.entitymanager.EntityContext;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiresActivator(UsePersistence.class)
public class PersistentTestComponentsService implements LifecycleObserver {

    @Override
    public void onStarted(final ApplicationContext applicationContext) {
        final List<TypeView<?>> entities = Stream.of(
                User.class,
                Address.class,
                JpaUser.class,
                UserWithNamedQuery.class
        ).map(applicationContext.environment()::introspect).collect(Collectors.toList());
        applicationContext.add(new EntityContext(entities));
    }
}
