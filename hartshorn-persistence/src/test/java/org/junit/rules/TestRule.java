/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.junit.rules;

/**
 * TestContainers has a hard dependency on this class and {@link org.junit.runners.model.Statement}, both of which
 * are only present in JUnit 4. However, these types are not used if the active environment is JUnit 5. This is
 * purely a placeholder to avoid runtime classloader issues.
 * @see <a href="https://github.com/testcontainers/testcontainers-java/issues/970">testcontainers/testcontainers-java #970</a>
 */
@SuppressWarnings("unused")
public interface TestRule {
}
