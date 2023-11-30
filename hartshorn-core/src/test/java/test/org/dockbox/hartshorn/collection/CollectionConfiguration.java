/*
 * Copyright 2019-2023 the original author or authors.
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

package test.org.dockbox.hartshorn.collection;

import org.dockbox.hartshorn.component.Configuration;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.component.processing.Binds.BindingType;
import org.dockbox.hartshorn.inject.Named;

@Configuration
public class CollectionConfiguration {

    @Binds(type = BindingType.COLLECTION)
    String hello() {
        return "Hello";
    }

    @Binds(type = BindingType.COLLECTION)
    String thing(@Named("name") String name) {
        return name;
    }

    @Binds
    @Named("name")
    String name() {
        return "World";
    }
}
