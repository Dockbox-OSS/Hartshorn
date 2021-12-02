/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
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
