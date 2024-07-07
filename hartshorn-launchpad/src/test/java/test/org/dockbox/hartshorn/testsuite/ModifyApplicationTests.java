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

package test.org.dockbox.hartshorn.testsuite;

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.testsuite.ModifyApplication;
import org.dockbox.hartshorn.testsuite.TestCustomizer;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;

@HartshornTest(includeBasePackages = false)
public class ModifyApplicationTests {

    @ModifyApplication
    public static void customize() {
        TestCustomizer.BUILDER.compose(builder -> {
            builder.arguments(args -> args.add("--hartshorn.modified=true"));
        });
    }

    @InjectTest
    void testFactoryWasModified(ApplicationContext applicationContext) {
        Option<String> property = applicationContext.property("hartshorn.modified");
        Assertions.assertTrue(property.present());
        Assertions.assertEquals("true", property.get());
    }
}
