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

package test.org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.config.ObjectMappingException;
import org.dockbox.hartshorn.config.annotations.UseConfigurations;
import org.dockbox.hartshorn.config.properties.PropertyHolder;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.dockbox.hartshorn.inject.Inject;

@HartshornTest(includeBasePackages = false)
@UseConfigurations
public abstract class StandardPropertyHolderTests {

    @Inject
    private ApplicationContext applicationContext;

    protected abstract PropertyHolder propertyHolder(ApplicationContext applicationContext);

    @Test
    void testPropertyHolder() throws ObjectMappingException {
        PropertyHolder propertyHolder = this.propertyHolder(this.applicationContext);

        propertyHolder.set("user.name", "John Doe");

        Option<User> user = propertyHolder.get("user", User.class);
        Assertions.assertTrue(user.present());
        Assertions.assertEquals("John Doe", user.get().name());

        propertyHolder.set("user.address", new Address("Darwin City", "Darwin Street", 12));

        Option<Address> address = propertyHolder.get("user.address", Address.class);
        Assertions.assertTrue(address.present());
        Assertions.assertEquals("Darwin City", address.get().city());
        Assertions.assertEquals("Darwin Street", address.get().street());
        Assertions.assertEquals(12, address.get().number());

        propertyHolder.set("user.address.street", "Darwin Lane");

        Option<Address> address2 = propertyHolder.get("user.address", Address.class);
        Assertions.assertTrue(address2.present());
        Assertions.assertEquals("Darwin City", address2.get().city());
        Assertions.assertEquals("Darwin Lane", address2.get().street());
        Assertions.assertEquals(12, address2.get().number());
    }

    @Test
    @TestComponents(components = ComponentWithUserValue.class)
    void testValueComponents() throws ObjectMappingException {
        PropertyHolder propertyHolder = this.propertyHolder(this.applicationContext);
        propertyHolder.set("user.name", "John Doe");
        propertyHolder.set("user.address.city", "Darwin City");
        propertyHolder.set("user.address.street", "Darwin Lane");
        propertyHolder.set("user.address.number", 12);

        ComponentWithUserValue component = this.applicationContext.get(ComponentWithUserValue.class);
        Assertions.assertNotNull(component);
        Assertions.assertNotNull(component.user());

        Assertions.assertEquals("John Doe", component.user().name());
        Assertions.assertEquals("Darwin City", component.user().address().city());
        Assertions.assertEquals("Darwin Lane", component.user().address().street());
        Assertions.assertEquals(12, component.user().address().number());
    }
}
