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

package org.dockbox.hartshorn.testsuite;

import org.dockbox.hartshorn.core.annotations.Extends;
import org.dockbox.hartshorn.core.context.Context;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Inject;

/**
 * Acts as a composite between {@link Inject} and {@link Test}. This allows test method parameters to be injected
 * into the test method. Note that this will inject on provision-basis and will not inject {@link Context} types.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Test
@Extends(Inject.class)
public @interface InjectTest {
}
