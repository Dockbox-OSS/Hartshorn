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

package org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.util.annotations.CompositeOf;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ExceptionUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.hamcrest.core.StringContains.containsString;

@Retention(RetentionPolicy.RUNTIME)
@interface Component {
}

@Retention(RetentionPolicy.RUNTIME)
@interface Router {
    String value() default "";
}

@Retention(RetentionPolicy.RUNTIME)
@CompositeOf({ Component.class, Router.class })
@interface Application {
}

@Router("/test")
@Application
public class CompositeToStringTest {
    @Test
    public void throwExceptionsWhenTwoAnnotationsAreFound() {
        Exception e = Assertions.assertThrows(Exception.class, () -> AnnotationHelper.oneOrNull(CompositeToStringTest.class, Router.class));
        MatcherAssert.assertThat(ExceptionUtils.readStackTrace(e), containsString("Found more than one annotation on class org.dockbox.hartshorn.util.CompositeToStringTest"));
    }

    @Test
    public void canGetTwoRouters() {
        Assertions.assertEquals(2, AnnotationHelper.allOrEmpty(CompositeToStringTest.class, Router.class).size());
    }
}
