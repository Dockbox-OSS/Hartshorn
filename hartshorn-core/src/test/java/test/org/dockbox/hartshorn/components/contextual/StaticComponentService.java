/*
 * Copyright 2019-2024 the original author or authors.
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

package test.org.dockbox.hartshorn.components.contextual;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.component.processing.Binds.BindingType;
import org.dockbox.hartshorn.inject.Named;

@Service
public class StaticComponentService {

    public static final String USER = "user";
    public static final String ADMIN = "admin";
    public static final String GUEST = "guest";

    @Named(USER)
    @Binds(type = BindingType.COLLECTION, priority = 12)
    public StaticComponent userComponent() {
        return new StaticComponent(USER);
    }

    @Named(ADMIN)
    @Binds(type = BindingType.COLLECTION)
    public StaticComponent adminComponent() {
        return new StaticComponent(ADMIN);
    }

    @Named(GUEST)
    @Binds(type = BindingType.COLLECTION)
    public StaticComponent guestComponent() {
        return new StaticComponent(GUEST);
    }
}
