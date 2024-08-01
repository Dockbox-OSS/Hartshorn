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

package test.org.dockbox.hartshorn.conditions;

import org.dockbox.hartshorn.component.Configuration;
import org.dockbox.hartshorn.component.condition.RequiresClass;
import org.dockbox.hartshorn.component.condition.RequiresProperty;
import org.dockbox.hartshorn.component.processing.Singleton;
import org.dockbox.hartshorn.inject.Named;

@Configuration
public class ConditionalConfiguration {

    /**
     * Passes as long as {@code java.lang.String} is on the classpath. As this is
     * part of the standard library, it should always be available.
     */
    @Singleton
    @Named("a")
    @RequiresClass("java.lang.String")
    public String a() {
        return "a";
    }

    /**
     * Fails when {@code java.gnal.String} is not on the classpath. As this is
     * an intentional typo, it should never be available.
     */
    @Singleton
    @Named("b")
    @RequiresClass("java.gnal.String")
    public String b() {
        return "b";
    }

    /**
     * Passes as long as {@code property.c} is present as a property, no matter
     * what its value is.
     */
    @Singleton
    @Named("c")
    @RequiresProperty(name = "property.c")
    public String c() {
        return "c";
    }

    /**
     * Passes as long as {@code property.d} is present as a property, and its
     * value is equal to {@code d}. This is handled by {@link ConditionTests},
     * so the property is <b>present</b>.
     */
    @Singleton
    @Named("d")
    @RequiresProperty(name = "property.d", withValue = "d")
    public String d() {
        return "d";
    }

    /**
     * Passes as long as {@code property.e} is present as a property, and its
     * value is equal to {@code e}. This is handled by {@link ConditionTests},
     * so the property is <b>absent</b>.
     */
    @Singleton
    @Named("e")
    @RequiresProperty(name = "property.e", withValue = "e")
    public String e() {
        return "e";
    }

    /**
     * Passes if there is no property named {@code property.l}. This is handled
     * by {@link ConditionTests}, so the property is <b>absent</b>.
     */
    @Singleton
    @Named("f")
    @RequiresProperty(name = "property.f", matchIfMissing = true)
    public String f() {
        return "f";
    }
}
