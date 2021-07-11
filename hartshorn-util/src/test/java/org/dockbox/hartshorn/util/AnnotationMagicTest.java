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
import org.dockbox.hartshorn.util.annotations.Extends;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ExceptionUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

enum HttpMethod {
    GET,
    POST
}

enum InterceptType {
    PRE_HANDLER,
    AFTER_SUCCESS
}

@Retention(RetentionPolicy.RUNTIME)
@Extends(Route.class)
@Route(method = HttpMethod.POST, path = "joint")
@CompositeOf({ Base.class, Gett.class, Sub.class })
@interface Joint {
}

@Retention(RetentionPolicy.RUNTIME)
@CompositeOf({ Base.class, Gett.class, Sub.class })
@Extends(Route.class)
@Route(method = HttpMethod.POST, path = "joint")
@interface Joint2 {
    @AliasFor(target = Gett.class, value = "path")
    String getPath();
}

@Retention(RetentionPolicy.RUNTIME)
@interface WithoutDefault {
    String value();
}

@Retention(RetentionPolicy.RUNTIME)
@CompositeOf({ Base.class, Route.class, WithoutDefault.class })
@interface BaseAndRoute {
}


@Retention(RetentionPolicy.RUNTIME)
@interface Route {
    HttpMethod method() default HttpMethod.GET;

    String path() default "";

    String regex() default "";
}

// This is not a typo. On case-insensitive OS, Get and GET in same compiler output directory might cause issues
// NoClassDefFoundError: com/github/blindpirate/annotationmagic/Get (wrong name: com/github/blindpirate/annotationmagic/GET)
@Retention(RetentionPolicy.RUNTIME)
@Extends(Route.class)
@interface Gett {
    @AliasFor("path")
    String value() default "";

    String regex() default "";

    String path() default "";
}


@Retention(RetentionPolicy.RUNTIME)
@interface Json {
    boolean pretty() default false;
}

@Retention(RetentionPolicy.RUNTIME)
@CompositeOf({ Gett.class, Json.class })
@interface GetJson {
    @AliasFor(value = "path", target = Gett.class)
    String path() default "";

    @AliasFor(value = "regex", target = Gett.class)
    String regex() default "";

    @AliasFor(value = "pretty", target = Json.class)
    boolean pretty() default false;
}

@Retention(RetentionPolicy.RUNTIME)
@Extends(Route.class)
@Route(method = HttpMethod.POST)
@interface Post {
    String path() default "";
}

@Retention(RetentionPolicy.RUNTIME)
@Extends(Route.class)
@interface SocketJS {
    String path() default "";
}


@Retention(RetentionPolicy.RUNTIME)
@Extends(Route.class)
@Route
@interface Intercept {
    InterceptType type() default InterceptType.PRE_HANDLER;

    HttpMethod method() default HttpMethod.GET;

    String path() default "";
}

@Retention(RetentionPolicy.RUNTIME)
@Extends(Intercept.class)
@interface PreHandler {
    String path() default "";

    HttpMethod method() default HttpMethod.GET;
}

@Retention(RetentionPolicy.RUNTIME)
@Extends(Intercept.class)
@Intercept(type = InterceptType.AFTER_SUCCESS)
@interface AfterSuccess {
    String path() default "";

    HttpMethod method() default HttpMethod.GET;
}

@Retention(RetentionPolicy.RUNTIME)
@Extends(CircularMid.class)
@interface CircularBase {
}

@Retention(RetentionPolicy.RUNTIME)
@Extends(CircularBase.class)
@interface CircularMid {
}

@Retention(RetentionPolicy.RUNTIME)
@Extends(CircularMid.class)
@interface CircularSub {
}

@Retention(RetentionPolicy.RUNTIME)
@interface Base {
    String value() default "Base";
}

@Retention(RetentionPolicy.RUNTIME)
@Extends(Base.class)
@interface Mid {
    String value() default "Mid";
}

@Retention(RetentionPolicy.RUNTIME)
@Extends(Mid.class)
@interface Sub {
    String value() default "Sub";
}

public class AnnotationMagicTest {
    @Test
    public void canGetAllAnnotationsOfSameBaseAnnotation() {
        assertEquals("Base", AnnotationHelper.getOneOrNull(TestClassWithBase.class, Base.class).value());
        assertEquals("Mid", AnnotationHelper.getOneOrNull(TestClassWithMid.class, Base.class).value());
        assertEquals("Sub", AnnotationHelper.getOneOrNull(TestClassWithSub.class, Base.class).value());
    }

    @Test
    public void realWorldAnnotationInheritanceTest() {
        assertEquals(HttpMethod.POST, AnnotationHelper.getOneOrNull(TestClassWithRoute.class, Route.class).method());
        assertEquals("test", AnnotationHelper.getOneOrNull(TestClassWithRoute.class, Route.class).path());

        assertEquals(HttpMethod.GET, AnnotationHelper.getOneOrNull(TestClassWithGet.class, Route.class).method());
        assertEquals("get", AnnotationHelper.getOneOrNull(TestClassWithGet.class, Route.class).path());

        assertEquals(HttpMethod.POST, AnnotationHelper.getOneOrNull(TestClassWithPost.class, Route.class).method());
        assertEquals("post", AnnotationHelper.getOneOrNull(TestClassWithPost.class, Route.class).path());

        assertEquals("socketjs", AnnotationHelper.getOneOrNull(TestClassWithSocketJS.class, Route.class).path());

        assertEquals("intercept", AnnotationHelper.getOneOrNull(TestClassWithIntercept.class, Intercept.class).path());
        assertEquals("intercept", AnnotationHelper.getOneOrNull(TestClassWithIntercept.class, Route.class).path());
        assertEquals(HttpMethod.POST, AnnotationHelper.getOneOrNull(TestClassWithIntercept.class, Intercept.class).method());
        assertEquals(HttpMethod.POST, AnnotationHelper.getOneOrNull(TestClassWithIntercept.class, Route.class).method());
        assertEquals(InterceptType.AFTER_SUCCESS, AnnotationHelper.getOneOrNull(TestClassWithIntercept.class, Intercept.class).type());

        assertEquals("prehandler", AnnotationHelper.getOneOrNull(TestClassWithPreHandler.class, Intercept.class).path());
        assertEquals("prehandler", AnnotationHelper.getOneOrNull(TestClassWithPreHandler.class, Route.class).path());
        assertEquals(HttpMethod.GET, AnnotationHelper.getOneOrNull(TestClassWithPreHandler.class, Intercept.class).method());
        assertEquals(HttpMethod.GET, AnnotationHelper.getOneOrNull(TestClassWithPreHandler.class, Route.class).method());
        assertEquals(InterceptType.PRE_HANDLER, AnnotationHelper.getOneOrNull(TestClassWithPreHandler.class, Intercept.class).type());

        assertEquals("aftersuccess", AnnotationHelper.getOneOrNull(TestClassWithAfterSuccess.class, Intercept.class).path());
        assertEquals("aftersuccess", AnnotationHelper.getOneOrNull(TestClassWithAfterSuccess.class, Route.class).path());
        assertEquals(HttpMethod.POST, AnnotationHelper.getOneOrNull(TestClassWithAfterSuccess.class, Intercept.class).method());
        assertEquals(HttpMethod.POST, AnnotationHelper.getOneOrNull(TestClassWithAfterSuccess.class, Route.class).method());
        assertEquals(InterceptType.AFTER_SUCCESS, AnnotationHelper.getOneOrNull(TestClassWithAfterSuccess.class, Intercept.class).type());
    }

    @Test
    public void reportErrorsWhenCircularInheritanceDetected() {
        Exception exception = assertThrows(Exception.class, () -> AnnotationHelper.getOneOrNull(TestClassWithCircularAnnotation.class, CircularBase.class));
        assertTrue(exception.getMessage().contains("circular inheritance detected:"));
        assertTrue(exception.getMessage().contains("CircularMid"));
    }

    @Test
    public void reportErrorWhenMultipleAnnotationsWithSameBaseTypeFound() {
        Exception exception = assertThrows(Exception.class, () -> AnnotationHelper.getOneOrNull(TestClassWithSameBaseType.class, Base.class));
        assertTrue(exception.getMessage().contains("Found more than one annotation on class"));

        AnnotationHelper.getOneOrNull(TestClassWithSameBaseType.class, Sub.class);
    }

    @Test
    public void compositeOfTest() {
        assertEquals("test", AnnotationHelper.getOneOrNull(TestClassWithGetJson.class, Gett.class).value());
        assertEquals("test", AnnotationHelper.getOneOrNull(TestClassWithGetJson.class, Gett.class).path());
        assertEquals("test", AnnotationHelper.getOneOrNull(TestClassWithGetJson.class, Route.class).path());
        assertEquals("", AnnotationHelper.getOneOrNull(TestClassWithGetJson.class, Gett.class).regex());
        assertEquals("", AnnotationHelper.getOneOrNull(TestClassWithGetJson.class, Route.class).regex());
        assertTrue(AnnotationHelper.getOneOrNull(TestClassWithGetJson.class, Json.class).pretty());
    }

    @Test
    public void compositeOfDefaultValueTest() {
        assertEquals(HttpMethod.GET, AnnotationHelper.getOneOrNull(TestClassWithBaseAndRoute.class, Route.class).method());
        assertEquals("", AnnotationHelper.getOneOrNull(TestClassWithBaseAndRoute.class, Route.class).path());
        assertEquals("", AnnotationHelper.getOneOrNull(TestClassWithBaseAndRoute.class, Route.class).regex());

        Exception e = assertThrows(RuntimeException.class,
                () -> AnnotationHelper.getOneOrNull(TestClassWithBaseAndRoute.class, WithoutDefault.class).value());
        MatcherAssert.assertThat(ExceptionUtils.readStackTrace(e), containsString("Can't invoke org.dockbox.hartshorn.util.WithoutDefault.value() on composite annotation @org.dockbox.hartshorn.util.BaseAndRoute()"));
    }

    @Test
    public void compositionInheritanceJointTest() {
        List<Route> routes = AnnotationHelper.getAllOrEmpty(TestClassWithJointAnnotation.class, Route.class);
        assertEquals(Arrays.asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.GET), routes.stream().map(Route::method).collect(toList()));
        assertEquals(Arrays.asList("", "joint", ""), routes.stream().map(Route::path).collect(toList()));
        assertEquals(Arrays.asList("jointRegex", "", ""), routes.stream().map(Route::regex).collect(toList()));

        assertTrue(Reflect.instanceOf(routes.get(1), Route.class));
        assertTrue(Reflect.instanceOf(routes.get(1), Joint.class));
    }

    @Test
    public void jointAnnotationsAreStrictlyOrdered() {
        List<Route> routes = AnnotationHelper.getAllOrEmpty(TestClassWithJointAnnotation2.class, Route.class);
        assertEquals(Arrays.asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.GET), routes.stream().map(Route::method).collect(toList()));
        assertEquals(Arrays.asList("abc", "joint", ""), routes.stream().map(Route::path).collect(toList()));
        assertEquals(Arrays.asList("", "", "jointRegex"), routes.stream().map(Route::regex).collect(toList()));
    }
}

@Route(regex = "jointRegex")
@Joint
class TestClassWithJointAnnotation {
}

@Joint2(getPath = "abc")
@Route(regex = "jointRegex")
class TestClassWithJointAnnotation2 {
}

@BaseAndRoute
class TestClassWithBaseAndRoute {
}

@Base
@Sub
class TestClassWithSameBaseType {
}

@Route(method = HttpMethod.POST, path = "test")
class TestClassWithRoute {

}

@GetJson(path = "test", pretty = true)
class TestClassWithGetJson {
}

// +--- Get/Post/Patch/Delete
// +--- StaticResource
// +--- SocketJS
// +--- SocketJSBridge
// +--- Intercept
//   +--- PreHandler
//   +--- AfterSuccess
//   +--- AfterFailure
//   +--- AfterCompletion

@Gett(path = "get")
class TestClassWithGet {
}

@Post(path = "post")
class TestClassWithPost {
}

@SocketJS(path = "socketjs")
class TestClassWithSocketJS {
}

@Intercept(type = InterceptType.AFTER_SUCCESS, method = HttpMethod.POST, path = "intercept")
class TestClassWithIntercept {
}

@PreHandler(path = "prehandler")
class TestClassWithPreHandler {
}

@AfterSuccess(path = "aftersuccess", method = HttpMethod.POST)
class TestClassWithAfterSuccess {
}

@CircularSub
class TestClassWithCircularAnnotation {
}

@Base
class TestClassWithBase {
}

@Mid
class TestClassWithMid {
}

@Sub
class TestClassWithSub {
}
