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

package test.org.dockbox.hartshorn.architecture;

import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class ArchitectureUtilities {

    public static <T> ArchCondition<T> complyWith(String description, BiConsumer<T, ConditionEvents> consumer) {
        return new ArchCondition<>(description) {
            @Override
            public void check(T item, ConditionEvents events) {
                consumer.accept(item, events);
            }
        };
    }
}
