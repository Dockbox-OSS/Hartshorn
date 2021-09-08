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

package org.dockbox.hartshorn.boot;

import org.dockbox.hartshorn.boot.annotations.PostBootstrap;
import org.dockbox.hartshorn.boot.annotations.UseBootstrap;
import org.dockbox.hartshorn.di.ApplicationContextAware;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.ApplicationEnvironment;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.services.ServiceProcessor;
import org.dockbox.hartshorn.test.ApplicationAwareTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.atomic.AtomicBoolean;

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
        final ApplicationContextAware application = Mockito.spy(this.context().environment().application());
        final ApplicationEnvironment environment = Mockito.spy(this.context().environment());
        final ApplicationContext context = Mockito.spy(this.context());

        final AtomicBoolean passed = new AtomicBoolean(false);
        Mockito.doAnswer(invocation -> {
            final MethodContext<?, ?> method = invocation.getArgument(0);
            passed.set(method.annotation(PostBootstrap.class).present());
            return null;
        }).when(application).addActivation(Mockito.any());

        Mockito.when(environment.application()).thenReturn(application);
        Mockito.when(context.environment()).thenReturn(environment);

        final ServiceProcessor<UseBootstrap> processor = new PostBootstrapServiceProcessor();
        processor.process(context, TypeContext.of(ValidPostBootstrapService.class));
        Assertions.assertTrue(passed.get());
    }
}
