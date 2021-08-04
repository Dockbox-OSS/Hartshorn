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
import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.persistence.hibernate.HibernateSqlService;
import org.dockbox.hartshorn.persistence.properties.ConnectionProperty;
import org.dockbox.hartshorn.persistence.properties.Remote;
import org.dockbox.hartshorn.test.HartshornRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@ExtendWith(HartshornRunner.class)
abstract class SqlServiceTest {

    @Test
    public void testJpaSave() throws ApplicationException {
        final SqlService sql = this.sql();
        sql.save(new User("Guus"));
        sql.save(new User("Simon"));
        sql.save(new User("Josh"));

        final Set<User> users = sql.findAll(User.class);
        Assertions.assertFalse(users.isEmpty());

        for (User user : users) {
            Assertions.assertNotEquals(0, user.id());
        }
    }

    @Test
    void testJpaDelete() throws ApplicationException {
        final SqlService sql = this.sql();
        final User guus = new User("Guus");
        sql.save(guus);
        sql.save(new User("Simon"));
        sql.save(new User("Josh"));
        sql.delete(guus);

        final Set<User> users = sql.findAll(User.class);
        Assertions.assertFalse(users.isEmpty());

        for (User user : users) {
            Assertions.assertNotEquals("Guus", user.name());
        }
    }

    @Test
    void testJpaPersists() throws ApplicationException {
        final SqlService sql = this.sql();
        final User user = new User("Guus");
        Assertions.assertEquals(0, user.id());

        sql.save(user);
        Assertions.assertNotEquals(0, user.id());
    }

    @Test
    void testJpaUpdate() throws ApplicationException {
        final SqlService sql = this.sql();
        final User guus = new User("Guus");

        sql.save(guus);
        guus.name("NotGuus");
        sql.update(guus);

        final Exceptional<User> persisted = sql.findById(User.class, guus.id());
        Assertions.assertTrue(persisted.present());
        Assertions.assertEquals(persisted.get().name(), "NotGuus");
    }

    protected static Path directory(String prefix) {
        try {
            return Files.createTempDirectory(prefix);
        }
        catch (Exception e) {
            Assumptions.assumeTrue(false);
            //noinspection ReturnOfNull
            return null;
        }
    }

    protected SqlService sql() throws ApplicationException {
        SqlService man = new HibernateSqlService();
        String connection = this.remote().url(this.target());
        final ConnectionProperty property = ConnectionProperty.of(this.remote(), connection, "", "");
        man.apply(property);
        man.enable();
        return man;
    }

    protected abstract Remote remote();

    protected abstract Object target();
}
