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

package org.dockbox.hartshorn.component.condition;

/**
 * A condition that matches when a class is present on the classpath. Due to the nature of this condition, it is
 * required to provide the class name as a string.
 *
 * @see RequiresClass
 * @see Class#forName(String)
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class ClassCondition implements Condition {

    @Override
    public ConditionResult matches(ConditionContext context) {
        return context.annotatedElement().annotations().get(RequiresClass.class).map(condition -> {
            for (String name : condition.value()) {
                try {
                    Class.forName(name);
                }
                catch (ClassNotFoundException e) {
                    return ConditionResult.notFound("class", name);
                }
            }
            return ConditionResult.matched();
        }).orElse(ConditionResult.invalidCondition("class"));
    }
}
