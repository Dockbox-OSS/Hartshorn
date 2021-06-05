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

import org.dockbox.hartshorn.api.domain.OwnerLookup;
import org.dockbox.hartshorn.api.domain.TypedOwner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServiceLookupTests {

    @Test
    void testEntityTypeUsesEntityAlias() {
        final OwnerLookup lookup = new ServiceLookup();
        final TypedOwner owner = lookup.lookup(EmptyEntity.class);
        Assertions.assertNotNull(owner);
        Assertions.assertEquals("entity", owner.id());
    }

    @Test
    void testUsesProjectId() {
        final OwnerLookup lookup = new ServiceLookup();
        final TypedOwner owner = lookup.lookup(Hartshorn.class);
        Assertions.assertNotNull(owner);
        Assertions.assertEquals(HartshornInformation.PROJECT_ID, owner.id());

    }

    @Test
    void testServiceUsesServiceId() {
        final OwnerLookup lookup = new ServiceLookup();
        final TypedOwner owner = lookup.lookup(EmptyService.class);
        Assertions.assertNotNull(owner);
        Assertions.assertEquals("empty", owner.id());
    }

    @Test
    void testUnknownIsNull() {
        final OwnerLookup lookup = new ServiceLookup();
        final TypedOwner owner = lookup.lookup(Void.class);
        Assertions.assertNull(owner);
    }
}
