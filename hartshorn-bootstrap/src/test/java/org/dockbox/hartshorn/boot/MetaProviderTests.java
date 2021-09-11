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

import org.dockbox.hartshorn.api.domain.TypedOwner;
import org.dockbox.hartshorn.di.MetaProvider;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.test.ApplicationAwareTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MetaProviderTests extends ApplicationAwareTest {

    @Test
    void testComponentTypeUsesComponentAlias() {
        final MetaProvider lookup = new MetaProviderImpl(this.context());
        final TypedOwner owner = lookup.lookup(TypeContext.of(EmptyComponent.class));
        Assertions.assertNotNull(owner);
        Assertions.assertEquals("component", owner.id());
    }

    @Test
    void testUsesProjectId() {
        final MetaProvider lookup = new MetaProviderImpl(this.context());
        final TypedOwner owner = lookup.lookup(TypeContext.of(Hartshorn.class));
        Assertions.assertNotNull(owner);
        Assertions.assertEquals(Hartshorn.PROJECT_ID, owner.id());

    }

    @Test
    void testServiceUsesServiceId() {
        final MetaProvider lookup = new MetaProviderImpl(this.context());
        final TypedOwner owner = lookup.lookup(TypeContext.of(EmptyService.class));
        Assertions.assertNotNull(owner);
        Assertions.assertEquals("empty", owner.id());
    }

    @Test
    void testUnknownIsSelf() {
        final MetaProvider lookup = new MetaProviderImpl(this.context());
        final TypedOwner owner = lookup.lookup(TypeContext.VOID);
        Assertions.assertNotNull(owner);
        Assertions.assertEquals("void", owner.id());
    }
}
