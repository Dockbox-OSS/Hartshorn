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


import org.dockbox.hartshorn.core.annotations.Base;
import org.dockbox.hartshorn.core.annotations.HttpMethod;
import org.dockbox.hartshorn.core.annotations.Intercept;
import org.dockbox.hartshorn.core.annotations.InterceptType;
import org.dockbox.hartshorn.core.annotations.Route;
import org.dockbox.hartshorn.core.annotations.Sub;
import org.dockbox.hartshorn.core.annotations.TestClassWithAfterSuccess;
import org.dockbox.hartshorn.core.annotations.TestClassWithBase;
import org.dockbox.hartshorn.core.annotations.TestClassWithGet;
import org.dockbox.hartshorn.core.annotations.TestClassWithIntercept;
import org.dockbox.hartshorn.core.annotations.TestClassWithJointAnnotation2;
import org.dockbox.hartshorn.core.annotations.TestClassWithMid;
import org.dockbox.hartshorn.core.annotations.TestClassWithPost;
import org.dockbox.hartshorn.core.annotations.TestClassWithPreHandler;
import org.dockbox.hartshorn.core.annotations.TestClassWithRoute;
import org.dockbox.hartshorn.core.annotations.TestClassWithSameBaseType;
import org.dockbox.hartshorn.core.annotations.TestClassWithSocketJS;
import org.dockbox.hartshorn.core.annotations.TestClassWithSub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class AnnotationMagicTest {
    @Test
    public void testBaseAnnotationOnSub() {
        Assertions.assertEquals("Sub", AnnotationHelper.oneOrNull(TestClassWithSub.class, Base.class).value());
    }

    @Test
    public void testBaseAnnotationOnBase() {
        Assertions.assertEquals("Base", AnnotationHelper.oneOrNull(TestClassWithBase.class, Base.class).value());
    }

    @Test
    public void testBaseAnnotationOnMid() {
        Assertions.assertEquals("Mid", AnnotationHelper.oneOrNull(TestClassWithMid.class, Base.class).value());
    }

    @Test
    public void testRouteAnnotationOnRouteClass() {
        Assertions.assertEquals(HttpMethod.POST, AnnotationHelper.oneOrNull(TestClassWithRoute.class, Route.class).method());
        Assertions.assertEquals("test", AnnotationHelper.oneOrNull(TestClassWithRoute.class, Route.class).path());
    }

    @Test
    public void testRouteAnnotationOnGetExtendedClass() {
        Assertions.assertEquals(HttpMethod.GET, AnnotationHelper.oneOrNull(TestClassWithGet.class, Route.class).method());
        Assertions.assertEquals("get", AnnotationHelper.oneOrNull(TestClassWithGet.class, Route.class).path());
    }

    @Test
    public void testRouteAnnotationOnPostExtendedClass() {
        Assertions.assertEquals(HttpMethod.POST, AnnotationHelper.oneOrNull(TestClassWithPost.class, Route.class).method());
        Assertions.assertEquals("post", AnnotationHelper.oneOrNull(TestClassWithPost.class, Route.class).path());
    }

    @Test
    public void testRouteAnnotationOnSocketExtendedClass() {
        Assertions.assertEquals("socketjs", AnnotationHelper.oneOrNull(TestClassWithSocketJS.class, Route.class).path());
    }

    @Test
    public void testInterceptedRouteAnnotations() {
        Assertions.assertEquals("intercept", AnnotationHelper.oneOrNull(TestClassWithIntercept.class, Intercept.class).path());
        Assertions.assertEquals("intercept", AnnotationHelper.oneOrNull(TestClassWithIntercept.class, Route.class).path());
        Assertions.assertEquals(HttpMethod.POST, AnnotationHelper.oneOrNull(TestClassWithIntercept.class, Intercept.class).method());
        Assertions.assertEquals(HttpMethod.POST, AnnotationHelper.oneOrNull(TestClassWithIntercept.class, Route.class).method());
        Assertions.assertEquals(InterceptType.AFTER_SUCCESS, AnnotationHelper.oneOrNull(TestClassWithIntercept.class, Intercept.class).type());
    }

    @Test
    public void testDoubleInterceptedRouteAnnotations() {
        Assertions.assertEquals("prehandler", AnnotationHelper.oneOrNull(TestClassWithPreHandler.class, Intercept.class).path());
        Assertions.assertEquals("prehandler", AnnotationHelper.oneOrNull(TestClassWithPreHandler.class, Route.class).path());
        Assertions.assertEquals(HttpMethod.GET, AnnotationHelper.oneOrNull(TestClassWithPreHandler.class, Intercept.class).method());
        Assertions.assertEquals(HttpMethod.GET, AnnotationHelper.oneOrNull(TestClassWithPreHandler.class, Route.class).method());
        Assertions.assertEquals(InterceptType.PRE_HANDLER, AnnotationHelper.oneOrNull(TestClassWithPreHandler.class, Intercept.class).type());
    }

    @Test
    public void testDoubleInheritedAndDefaultedRouteAnnotation() {
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
    public void jointAnnotationsAreStrictlyOrdered() {
        final List<Route> routes = AnnotationHelper.allOrEmpty(TestClassWithJointAnnotation2.class, Route.class);
        Assertions.assertEquals(Arrays.asList(HttpMethod.POST, HttpMethod.GET), routes.stream().map(Route::method).collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList("abc", ""), routes.stream().map(Route::path).collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList("", "jointRegex"), routes.stream().map(Route::regex).collect(Collectors.toList()));
    }
}
