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

package org.dockbox.hartshorn.persistence;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.persistence.hibernate.HibernateSqlService;
import org.dockbox.hartshorn.persistence.properties.ConnectionAttribute;
import org.dockbox.hartshorn.persistence.properties.Remote;
import org.dockbox.hartshorn.test.ApplicationAwareTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

class SqlServiceTest extends ApplicationAwareTest {

    public static Stream<Arguments> dialects() {
        return Stream.of(
                Arguments.of(Remote.DERBY, directory("derby"))
        );
    }

    protected static Path directory(final String prefix) {
        try {
            return Files.createTempDirectory(prefix);
        }
        catch (final Exception e) {
            Assumptions.assumeTrue(false);
            //noinspection ReturnOfNull
            return null;
        }
    }

    @ParameterizedTest
    @MethodSource("dialects")
    public void testJpaSave(final Remote remote, final Object target) {
        final SqlService sql = this.sql(remote, target);
        sql.save(new User("Guus"));
        sql.save(new User("Simon"));
        sql.save(new User("Josh"));

        final Set<User> users = sql.findAll(User.class);
        Assertions.assertFalse(users.isEmpty());

        for (final User user : users) {
            Assertions.assertNotEquals(0, user.id());
        }
    }

    protected SqlService sql(final Remote remote, final Object target) {
        return this.context().get(HibernateSqlService.class, ConnectionAttribute.of(remote, target, "", ""));
    }

    @ParameterizedTest
    @MethodSource("dialects")
    void testJpaDelete(final Remote remote, final Object target) {
        final SqlService sql = this.sql(remote, target);
        final User guus = new User("Guus");
        sql.save(guus);
        sql.save(new User("Simon"));
        sql.save(new User("Josh"));
        sql.delete(guus);

        final Set<User> users = sql.findAll(User.class);
        Assertions.assertFalse(users.isEmpty());

        for (final User user : users) {
            Assertions.assertNotEquals("Guus", user.name());
        }
    }

    @ParameterizedTest
    @MethodSource("dialects")
    void testJpaPersists(final Remote remote, final Object target) {
        final SqlService sql = this.sql(remote, target);
        final User user = new User("Guus");
        Assertions.assertEquals(0, user.id());

        sql.save(user);
        Assertions.assertNotEquals(0, user.id());
    }

    @ParameterizedTest
    @MethodSource("dialects")
    void testJpaUpdate(final Remote remote, final Object target) {
        final SqlService sql = this.sql(remote, target);
        final User guus = new User("Guus");

        sql.save(guus);
        guus.name("NotGuus");
        sql.update(guus);

        final Exceptional<User> persisted = sql.findById(User.class, guus.id());
        Assertions.assertTrue(persisted.present());
        Assertions.assertEquals(persisted.get().name(), "NotGuus");
    }
}
