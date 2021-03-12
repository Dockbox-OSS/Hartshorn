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

package org.dockbox.selene.api.objects.keys;

import org.dockbox.selene.api.objects.Exceptional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DynamicKeyTest {

    @Test
    public void canApplyToKeyHolder() {
        final String value = "KeyTest";
        TestKeyHolder holder = new TestKeyHolder();
        holder.set(TestKeys.HOLDER_KEY, value);
    }

    @Test
    public void canApplyToNonKeyHolder() {
        final String value = "KeyTest";
        TestNonKeyHolder holder = new TestNonKeyHolder();
        TestKeys.NON_HOLDER_KEY.set(holder, value);
    }

    @Test
    public void canObtainFromKeyHolder() {
        final String value = "KeyTest";
        TestKeyHolder holder = new TestKeyHolder();
        holder.set(TestKeys.HOLDER_KEY, value);
        Exceptional<String> result = holder.get(TestKeys.HOLDER_KEY);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(value, result.get());
    }

    @Test
    public void canObtainFromNonKeyHolder() {
        final String value = "KeyTest";
        TestNonKeyHolder holder = new TestNonKeyHolder();
        TestKeys.NON_HOLDER_KEY.set(holder, value);
        Exceptional<String> result = TestKeys.NON_HOLDER_KEY.get(holder);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(value, result.get());
    }
}
