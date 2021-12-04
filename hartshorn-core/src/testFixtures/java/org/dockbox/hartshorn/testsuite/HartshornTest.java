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

package org.dockbox.hartshorn.testsuite;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Inject;

/**
 * Annotation for test classes that should be run with the Hartshorn test suite. This will automatically
 * provide the test class with a {@link HartshornExtension} that will provide an active {@link ApplicationContext}
 * for the test class. The provided {@link ApplicationContext} is refreshed for every test case, to avoid
 * leaking context data between test cases.
 *
 * <p>Additionally, the {@link HartshornExtension} will inject fields annotated with {@link Inject} within
 * the test class. Like the active {@link ApplicationContext}, this will be refreshed for every test case.
 *
 * @see HartshornExtension
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(HartshornExtension.class)
public @interface HartshornTest {
    Class<? extends Annotation>[] activators() default {};
}
