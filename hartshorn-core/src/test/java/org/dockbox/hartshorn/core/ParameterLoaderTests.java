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

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.parameterloaders.RuleBasedParameterLoaderImpl;
import org.dockbox.hartshorn.core.parameterloaders.StringParameterRule;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoaderRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.LinkedList;
import java.util.List;

public class ParameterLoaderTests {

    @Test
    void testRuleBasedParameterLoadersCannotHaveDuplicateRules() {
        final RuleBasedParameterLoaderImpl parameterLoader = new RuleBasedParameterLoaderImpl();
        final ParameterLoaderRule<ParameterLoaderContext> stringRule = new StringParameterRule();
        parameterLoader.add(stringRule);
        parameterLoader.add(stringRule);
        Assertions.assertEquals(1, parameterLoader.rules().size());
    }

    @Test
    void testRuleBasedParameterLoaderReturnsCorrectObjectsOrDefault() {
        final RuleBasedParameterLoaderImpl parameterLoader = new RuleBasedParameterLoaderImpl();
        parameterLoader.add(new StringParameterRule());
        final MethodContext methodContext = Mockito.mock(MethodContext.class);
        final ParameterContext stringParameter = Mockito.mock(ParameterContext.class);
        Mockito.when(stringParameter.type()).thenReturn(TypeContext.of(String.class));

        final ParameterContext intParameter = Mockito.mock(ParameterContext.class);
        Mockito.when(intParameter.type()).thenReturn(TypeContext.of(int.class));

        final LinkedList parameters = new LinkedList();
        parameters.add(stringParameter);
        parameters.add(intParameter);

        Mockito.when(methodContext.parameters()).thenReturn(parameters);

        final ParameterLoaderContext loaderContext = new ParameterLoaderContext(methodContext, null, null, null);
        final List<Object> objects = parameterLoader.loadArguments(loaderContext);

        Assertions.assertNotNull(objects);
        Assertions.assertEquals(2, objects.size());
        Assertions.assertEquals("JUnit", objects.get(0));
        Assertions.assertEquals(0, objects.get(1)); // Default value for 'int', instead of the value being null
    }
}
