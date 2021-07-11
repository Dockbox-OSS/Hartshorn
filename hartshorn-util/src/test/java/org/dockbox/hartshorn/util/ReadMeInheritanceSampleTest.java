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
import org.dockbox.hartshorn.util.annotations.Extends;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@interface Animal {
    boolean fluffy() default false;

    String name() default "";
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Extends(Animal.class)
@Animal(fluffy = true)
@interface Pet {
    String name();
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Extends(Pet.class)
@interface Cat {
    @AliasFor("name")
    String value();
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Extends(Pet.class)
@interface Dog {
    String name();
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Extends(Animal.class)
@interface Rat {
    @AliasFor(target = Animal.class, value = "name")
    String value();
}

@Cat("Tom")
class MyClass {
    @Dog(name = "Spike")
    @Rat("Jerry")
    public void foo() {
    }
}


public class ReadMeInheritanceSampleTest {
    @Test
    public void test() throws NoSuchMethodException {
        Pet petAnnotation = AnnotationHelper.getOneOrNull(MyClass.class, Pet.class);
        assertEquals("Tom", petAnnotation.name());
        assertTrue(AnnotationHelper.instanceOf(petAnnotation, Animal.class));

        Animal animalAnnotation = AnnotationHelper.getOneOrNull(MyClass.class, Animal.class);
        assertTrue(animalAnnotation.fluffy());

        Method fooMethod = MyClass.class.getMethod("foo");
        List<Animal> animalAnnotations = AnnotationHelper.getAllOrEmpty(fooMethod, Animal.class);
        assertEquals(Arrays.asList("Spike", "Jerry"), animalAnnotations.stream().map(Animal::name).collect(toList()));
    }
}
