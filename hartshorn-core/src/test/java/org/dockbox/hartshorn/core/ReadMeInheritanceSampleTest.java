/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.annotations.AliasFor;
import org.dockbox.hartshorn.core.annotations.Extends;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@interface Animal {
    boolean fluffy() default false;

    String name() default "";
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Extends(Animal.class)
@Animal(fluffy = true)
@interface Pet {
    String name();
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Extends(Pet.class)
@interface Cat {
    @AliasFor("name")
    String value();
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Extends(Pet.class)
@interface Dog {
    String name();
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
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
        final Pet petAnnotation = AnnotationHelper.oneOrNull(MyClass.class, Pet.class);
        Assertions.assertEquals("Tom", petAnnotation.name());
        Assertions.assertTrue(AnnotationHelper.instanceOf(petAnnotation, Animal.class));

        final Animal animalAnnotation = AnnotationHelper.oneOrNull(MyClass.class, Animal.class);
        Assertions.assertTrue(animalAnnotation.fluffy());

        final Method fooMethod = MyClass.class.getMethod("foo");
        final List<Animal> animalAnnotations = AnnotationHelper.allOrEmpty(fooMethod, Animal.class);
        Assertions.assertEquals(Arrays.asList("Spike", "Jerry"), animalAnnotations.stream().map(Animal::name).collect(Collectors.toList()));
    }
}
