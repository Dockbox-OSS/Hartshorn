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

package test.org.dockbox.hartshorn.introspect;

import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.ExecutableParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderContext;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderRule;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
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

        final TypeView<String> stringTypeView = Mockito.mock(TypeView.class);
        Mockito.doReturn(true).when(stringTypeView).is(String.class);
        Mockito.doReturn(null).when(stringTypeView).defaultOrNull();
        Mockito.when(stringTypeView.cast(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        final TypeView<Integer> intTypeView = Mockito.mock(TypeView.class);
        Mockito.doReturn(true).when(intTypeView).is(int.class);
        Mockito.doReturn(0).when(intTypeView).defaultOrNull();
        Mockito.when(intTypeView.cast(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        final MethodView<?, ?> methodContext = Mockito.mock(MethodView.class);

        final ParameterView<String> stringParameter = TypeUtils.adjustWildcards(Mockito.mock(ParameterView.class), ParameterView.class);
        Mockito.when(stringParameter.type()).thenReturn(stringTypeView);

        final ParameterView<Integer> intParameter = TypeUtils.adjustWildcards(Mockito.mock(ParameterView.class), ParameterView.class);
        Mockito.when(intParameter.type()).thenReturn(intTypeView);

        final ExecutableParametersIntrospector parametersIntrospector = Mockito.mock(ExecutableParametersIntrospector.class);

        final LinkedList<ParameterView<?>> parameters = new LinkedList<>();
        parameters.add(stringParameter);
        parameters.add(intParameter);

        Mockito.when(parametersIntrospector.all()).thenReturn(parameters);
        Mockito.when(methodContext.parameters()).thenReturn(parametersIntrospector);

        final ParameterLoaderContext loaderContext = new ParameterLoaderContext(methodContext, null);
        final List<Object> objects = parameterLoader.loadArguments(loaderContext);

        Assertions.assertNotNull(objects);
        Assertions.assertEquals(2, objects.size());
        Assertions.assertEquals("JUnit", objects.get(0));
        Assertions.assertEquals(0, objects.get(1)); // Default value for 'int', instead of the value being null
    }
}
