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

package org.dockbox.selene.api;

import org.dockbox.selene.api.annotations.PostBootstrap;
import org.dockbox.selene.api.annotations.UseBootstrap;
import org.dockbox.selene.di.services.ServiceProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;

public class PostBootstrapServiceProcessorTests {

    @Test
    void testProcessorAcceptsValidServices() {
        final ServiceProcessor<UseBootstrap> processor = new PostBootstrapServiceProcessor();
        Assertions.assertTrue(processor.preconditions(ValidPostBootstrapService.class));
    }

    @Test
    void testProcessorRejectsNonActivatedServices() {
        final ServiceProcessor<UseBootstrap> processor = new PostBootstrapServiceProcessor();
        Assertions.assertFalse(processor.preconditions(UnactivatedPostBootstrapService.class));
    }

    @Test
    void testProcessorRejectsNonPostBootstrapping() {
        final ServiceProcessor<UseBootstrap> processor = new PostBootstrapServiceProcessor();
        Assertions.assertFalse(processor.preconditions(EmptyPostBootstrapService.class));
    }

    @Test
    void testProcessorAddsPostBootstrapActivations() throws NoSuchFieldException, IllegalAccessException {
        final SeleneBootstrap bootstrap = Mockito.mock(SeleneBootstrap.class);
        Mockito.doAnswer(invocation -> {
            final Method method = invocation.getArgument(0);
            Assertions.assertTrue(method.isAnnotationPresent(PostBootstrap.class));
            return null;
        }).when(bootstrap).addPostBootstrapActivation(Mockito.any(Method.class), Mockito.any(Class.class));

        Mockito.mockStatic(SeleneBootstrap.class).when(SeleneBootstrap::instance).thenReturn(bootstrap);

        final ServiceProcessor<UseBootstrap> processor = new PostBootstrapServiceProcessor();
        processor.process(null, ValidPostBootstrapService.class);
    }
}
