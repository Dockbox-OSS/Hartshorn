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

package org.dockbox.hartshorn.api;

import org.dockbox.hartshorn.api.annotations.PostBootstrap;
import org.dockbox.hartshorn.api.annotations.UseBootstrap;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.services.ServiceProcessor;
import org.dockbox.hartshorn.test.ApplicationAwareTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PostBootstrapServiceProcessorTests extends ApplicationAwareTest {

    @Test
    void testProcessorAcceptsValidServices() {
        final ServiceProcessor<UseBootstrap> processor = new PostBootstrapServiceProcessor();
        Assertions.assertTrue(processor.preconditions(this.context(), TypeContext.of(ValidPostBootstrapService.class)));
    }

    @Test
    void testProcessorRejectsNonActivatedServices() {
        final ServiceProcessor<UseBootstrap> processor = new PostBootstrapServiceProcessor();
        Assertions.assertFalse(processor.preconditions(this.context(), TypeContext.of(UnactivatedPostBootstrapService.class)));
    }

    @Test
    void testProcessorRejectsNonPostBootstrapping() {
        final ServiceProcessor<UseBootstrap> processor = new PostBootstrapServiceProcessor();
        Assertions.assertFalse(processor.preconditions(this.context(), TypeContext.of(EmptyPostBootstrapService.class)));
    }

    @Test
    void testProcessorAddsPostBootstrapActivations() {
        final HartshornBootstrap bootstrap = Mockito.mock(HartshornBootstrap.class);
        Mockito.doAnswer(invocation -> {
            final MethodContext<?, ?> method = invocation.getArgument(0);
            Assertions.assertTrue(method.annotation(PostBootstrap.class).present());
            return null;
        }).when(bootstrap).addPostBootstrapActivation(Mockito.any(MethodContext.class));

        Mockito.mockStatic(HartshornBootstrap.class).when(HartshornBootstrap::instance).thenReturn(bootstrap);

        final ServiceProcessor<UseBootstrap> processor = new PostBootstrapServiceProcessor();
        processor.process(null, TypeContext.of(ValidPostBootstrapService.class));
    }
}
