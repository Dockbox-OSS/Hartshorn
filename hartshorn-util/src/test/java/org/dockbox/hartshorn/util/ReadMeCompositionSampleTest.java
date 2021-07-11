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


import org.dockbox.hartshorn.util.annotations.AliasFor;
import org.dockbox.hartshorn.util.annotations.CompositeOf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@interface GET {
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@interface Path {
    String value();
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@interface Produces {
    String value();
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@CompositeOf({GET.class, Path.class, Produces.class})
@interface GetResource {
    @AliasFor(target = Path.class, value = "value")
    String path();

    @AliasFor(target = Produces.class, value = "value")
    String produces();
}

class MyResource {
    @GET
    @Path("/{id}")
    @Produces("application/json")
    public String foo() {
        return "";
    }

    // Equivalent as above
    @GetResource(path = "/{id}", produces = "application/json")
    public String bar() {
        return "";
    }
}

public class ReadMeCompositionSampleTest {
    @Test
    public void test() throws NoSuchMethodException {
        Assertions.assertTrue(AnnotationHelper.isAnnotationPresent(MyResource.class.getMethod("bar"), GET.class));

        Path pathAnnotation = AnnotationHelper.getOneOrNull(MyResource.class.getMethod("bar"), Path.class);
        assertEquals("/{id}", pathAnnotation.value());

        Produces producesAnnotation = AnnotationHelper.getOneOrNull(MyResource.class.getMethod("bar"), Produces.class);
        assertEquals("application/json", producesAnnotation.value());
    }
}
