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

package org.dockbox.hartshorn.core;


import org.dockbox.hartshorn.core.annotations.AliasFor;
import org.dockbox.hartshorn.core.annotations.CompositeOf;
import org.dockbox.hartshorn.core.annotations.Extends;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ExceptionUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.containsString;

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
    String path();
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
        Assertions.assertEquals("Base", AnnotationHelper.oneOrNull(TestClassWithBase.class, Base.class).value());
        Assertions.assertEquals("Mid", AnnotationHelper.oneOrNull(TestClassWithMid.class, Base.class).value());
        Assertions.assertEquals("Sub", AnnotationHelper.oneOrNull(TestClassWithSub.class, Base.class).value());
    }

    @Test
    public void realWorldAnnotationInheritanceTest() {
        Assertions.assertEquals(HttpMethod.POST, AnnotationHelper.oneOrNull(TestClassWithRoute.class, Route.class).method());
        Assertions.assertEquals("test", AnnotationHelper.oneOrNull(TestClassWithRoute.class, Route.class).path());

        Assertions.assertEquals(HttpMethod.GET, AnnotationHelper.oneOrNull(TestClassWithGet.class, Route.class).method());
        Assertions.assertEquals("get", AnnotationHelper.oneOrNull(TestClassWithGet.class, Route.class).path());

        Assertions.assertEquals(HttpMethod.POST, AnnotationHelper.oneOrNull(TestClassWithPost.class, Route.class).method());
        Assertions.assertEquals("post", AnnotationHelper.oneOrNull(TestClassWithPost.class, Route.class).path());

        Assertions.assertEquals("socketjs", AnnotationHelper.oneOrNull(TestClassWithSocketJS.class, Route.class).path());

        Assertions.assertEquals("intercept", AnnotationHelper.oneOrNull(TestClassWithIntercept.class, Intercept.class).path());
        Assertions.assertEquals("intercept", AnnotationHelper.oneOrNull(TestClassWithIntercept.class, Route.class).path());
        Assertions.assertEquals(HttpMethod.POST, AnnotationHelper.oneOrNull(TestClassWithIntercept.class, Intercept.class).method());
        Assertions.assertEquals(HttpMethod.POST, AnnotationHelper.oneOrNull(TestClassWithIntercept.class, Route.class).method());
        Assertions.assertEquals(InterceptType.AFTER_SUCCESS, AnnotationHelper.oneOrNull(TestClassWithIntercept.class, Intercept.class).type());

        Assertions.assertEquals("prehandler", AnnotationHelper.oneOrNull(TestClassWithPreHandler.class, Intercept.class).path());
        Assertions.assertEquals("prehandler", AnnotationHelper.oneOrNull(TestClassWithPreHandler.class, Route.class).path());
        Assertions.assertEquals(HttpMethod.GET, AnnotationHelper.oneOrNull(TestClassWithPreHandler.class, Intercept.class).method());
        Assertions.assertEquals(HttpMethod.GET, AnnotationHelper.oneOrNull(TestClassWithPreHandler.class, Route.class).method());
        Assertions.assertEquals(InterceptType.PRE_HANDLER, AnnotationHelper.oneOrNull(TestClassWithPreHandler.class, Intercept.class).type());

        Assertions.assertEquals("aftersuccess", AnnotationHelper.oneOrNull(TestClassWithAfterSuccess.class, Intercept.class).path());
        Assertions.assertEquals("aftersuccess", AnnotationHelper.oneOrNull(TestClassWithAfterSuccess.class, Route.class).path());
        Assertions.assertEquals(HttpMethod.POST, AnnotationHelper.oneOrNull(TestClassWithAfterSuccess.class, Intercept.class).method());
        Assertions.assertEquals(HttpMethod.POST, AnnotationHelper.oneOrNull(TestClassWithAfterSuccess.class, Route.class).method());
        Assertions.assertEquals(InterceptType.AFTER_SUCCESS, AnnotationHelper.oneOrNull(TestClassWithAfterSuccess.class, Intercept.class).type());
    }

    @Test
    public void reportErrorWhenMultipleAnnotationsWithSameBaseTypeFound() {
        final Exception exception = Assertions.assertThrows(Exception.class, () -> AnnotationHelper.oneOrNull(TestClassWithSameBaseType.class, Base.class));
        Assertions.assertTrue(exception.getMessage().contains("Found more than one annotation on class"));

        AnnotationHelper.oneOrNull(TestClassWithSameBaseType.class, Sub.class);
    }

    @Test
    public void compositeOfTest() {
        Assertions.assertEquals("test", AnnotationHelper.oneOrNull(TestClassWithGetJson.class, Gett.class).value());
        Assertions.assertEquals("test", AnnotationHelper.oneOrNull(TestClassWithGetJson.class, Gett.class).path());
        Assertions.assertEquals("test", AnnotationHelper.oneOrNull(TestClassWithGetJson.class, Route.class).path());
        Assertions.assertEquals("", AnnotationHelper.oneOrNull(TestClassWithGetJson.class, Gett.class).regex());
        Assertions.assertEquals("", AnnotationHelper.oneOrNull(TestClassWithGetJson.class, Route.class).regex());
        Assertions.assertTrue(AnnotationHelper.oneOrNull(TestClassWithGetJson.class, Json.class).pretty());
    }

    @Test
    public void compositeOfDefaultValueTest() {
        Assertions.assertEquals(HttpMethod.GET, AnnotationHelper.oneOrNull(TestClassWithBaseAndRoute.class, Route.class).method());
        Assertions.assertEquals("", AnnotationHelper.oneOrNull(TestClassWithBaseAndRoute.class, Route.class).path());
        Assertions.assertEquals("", AnnotationHelper.oneOrNull(TestClassWithBaseAndRoute.class, Route.class).regex());

        final Exception e = Assertions.assertThrows(RuntimeException.class,
                () -> AnnotationHelper.oneOrNull(TestClassWithBaseAndRoute.class, WithoutDefault.class).value());
        MatcherAssert.assertThat(ExceptionUtils.readStackTrace(e), containsString("Can't invoke org.dockbox.hartshorn.core.WithoutDefault.value() on composite annotation @org.dockbox.hartshorn.core.BaseAndRoute()"));
    }

    @Test
    public void jointAnnotationsAreStrictlyOrdered() {
        final List<Route> routes = AnnotationHelper.allOrEmpty(TestClassWithJointAnnotation2.class, Route.class);
        Assertions.assertEquals(Arrays.asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.GET), routes.stream().map(Route::method).collect(toList()));
        Assertions.assertEquals(Arrays.asList("abc", "abc", ""), routes.stream().map(Route::path).collect(toList()));
        Assertions.assertEquals(Arrays.asList("", "", "jointRegex"), routes.stream().map(Route::regex).collect(toList()));
    }
}

@Route(regex = "jointRegex")
@Joint
class TestClassWithJointAnnotation {
}

@Joint2(path = "abc")
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

@Base
class TestClassWithBase {
}

@Mid
class TestClassWithMid {
}

@Sub
class TestClassWithSub {
}
