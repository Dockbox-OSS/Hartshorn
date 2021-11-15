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

module Hartshorn.hartshorn.persistence.main {
    requires Hartshorn.hartshorn.core.main;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.toml;
    requires com.fasterxml.jackson.dataformat.javaprop;
    requires com.fasterxml.jackson.dataformat.xml;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires org.jetbrains.annotations;
    requires jakarta.inject;
    requires lombok;
    requires java.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;

    exports org.dockbox.hartshorn.persistence;
    exports org.dockbox.hartshorn.persistence.annotations;
    exports org.dockbox.hartshorn.persistence.jpa;
    exports org.dockbox.hartshorn.persistence.mapping;
    exports org.dockbox.hartshorn.persistence.properties;
}
