/*
 * Copyright 2019-2026 the original author or authors.
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

package test.org.dockbox.hartshorn.inject.conditions;

import org.dockbox.hartshorn.inject.condition.support.PropertyCondition;
import org.dockbox.hartshorn.inject.condition.support.RequiresProperty;
import org.dockbox.hartshorn.test.annotations.TestProperties;
import org.dockbox.hartshorn.util.types.TypeUtils;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class PropertyConditionTests extends AbstractConditionTests {

    @Test
    @TestProperties("property.sample=true")
    void testPropertyConditionMatchesIfValueIsEqual() {
        assertCondition(
                PropertyCondition.class,
                TypeUtils.annotation(RequiresProperty.class, Map.of(
                        "name", "property.sample",
                        "withValue", "true"
                ))
        ).matches();
    }

    @Test
    @TestProperties("property.sample=false")
    void testPropertyConditionMatchesIfValueIsNotEqual() {
        assertCondition(
                PropertyCondition.class,
                TypeUtils.annotation(RequiresProperty.class, Map.of(
                        "name", "property.sample",
                        "withValue", "true"
                ))
        ).doesNotMatchWithMessage("Expected property 'property.sample' to be true but was false");
    }

    @Test
    void testPropertyConditionDoesNotMatchIfPropertyIsAbsent() {
        assertCondition(
                PropertyCondition.class,
                TypeUtils.annotation(RequiresProperty.class, Map.of(
                        "name", "property.sample"
                ))
        ).doesNotMatchWithMessage("Could not find property 'property.sample'");
    }

    @Test
    void testPropertyConditionMatchesIfMatchIfMissingIsTrue() {
        assertCondition(
                PropertyCondition.class,
                TypeUtils.annotation(RequiresProperty.class, Map.of(
                        "name", "property.sample",
                        "matchIfMissing", true
                ))
        ).matches();
    }
}
