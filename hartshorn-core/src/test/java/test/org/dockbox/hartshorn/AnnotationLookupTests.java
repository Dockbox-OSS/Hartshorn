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

package test.org.dockbox.hartshorn;


import test.org.dockbox.hartshorn.annotations.Base;
import test.org.dockbox.hartshorn.annotations.HttpMethod;
import test.org.dockbox.hartshorn.annotations.Intercept;
import test.org.dockbox.hartshorn.annotations.InterceptType;
import test.org.dockbox.hartshorn.annotations.Route;
import test.org.dockbox.hartshorn.annotations.Sub;
import test.org.dockbox.hartshorn.components.TestClassWithAfterSuccess;
import test.org.dockbox.hartshorn.components.TestClassWithBase;
import test.org.dockbox.hartshorn.components.TestClassWithGet;
import test.org.dockbox.hartshorn.components.TestClassWithIntercept;
import test.org.dockbox.hartshorn.components.TestClassWithJointAnnotation2;
import test.org.dockbox.hartshorn.components.TestClassWithMid;
import test.org.dockbox.hartshorn.components.TestClassWithPost;
import test.org.dockbox.hartshorn.components.TestClassWithPreHandler;
import test.org.dockbox.hartshorn.components.TestClassWithRoute;
import test.org.dockbox.hartshorn.components.TestClassWithSameBaseType;
import test.org.dockbox.hartshorn.components.TestClassWithSocketJS;
import test.org.dockbox.hartshorn.components.TestClassWithSub;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.inject.Inject;

@HartshornTest(includeBasePackages = false)
public class AnnotationLookupTests {

    @Inject
    private AnnotationLookup annotationLookup;
    
    @Test
    public void testBaseAnnotationOnSub() {
        Assertions.assertEquals("Sub", this.annotationLookup.find(TestClassWithSub.class, Base.class).value());
    }

    @Test
    public void testBaseAnnotationOnBase() {
        Assertions.assertEquals("Base", this.annotationLookup.find(TestClassWithBase.class, Base.class).value());
    }

    @Test
    public void testBaseAnnotationOnMid() {
        Assertions.assertEquals("Mid", this.annotationLookup.find(TestClassWithMid.class, Base.class).value());
    }

    @Test
    public void testRouteAnnotationOnRouteClass() {
        Assertions.assertEquals(HttpMethod.POST, this.annotationLookup.find(TestClassWithRoute.class, Route.class).method());
        Assertions.assertEquals("test", this.annotationLookup.find(TestClassWithRoute.class, Route.class).path());
    }

    @Test
    public void testRouteAnnotationOnGetExtendedClass() {
        Assertions.assertEquals(HttpMethod.GET, this.annotationLookup.find(TestClassWithGet.class, Route.class).method());
        Assertions.assertEquals("get", this.annotationLookup.find(TestClassWithGet.class, Route.class).path());
    }

    @Test
    public void testRouteAnnotationOnPostExtendedClass() {
        Assertions.assertEquals(HttpMethod.POST, this.annotationLookup.find(TestClassWithPost.class, Route.class).method());
        Assertions.assertEquals("post", this.annotationLookup.find(TestClassWithPost.class, Route.class).path());
    }

    @Test
    public void testRouteAnnotationOnSocketExtendedClass() {
        Assertions.assertEquals("socketjs", this.annotationLookup.find(TestClassWithSocketJS.class, Route.class).path());
    }

    @Test
    public void testInterceptedRouteAnnotations() {
        Assertions.assertEquals("intercept", this.annotationLookup.find(TestClassWithIntercept.class, Intercept.class).path());
        Assertions.assertEquals("intercept", this.annotationLookup.find(TestClassWithIntercept.class, Route.class).path());
        Assertions.assertEquals(HttpMethod.POST, this.annotationLookup.find(TestClassWithIntercept.class, Intercept.class).method());
        Assertions.assertEquals(HttpMethod.POST, this.annotationLookup.find(TestClassWithIntercept.class, Route.class).method());
        Assertions.assertEquals(InterceptType.AFTER_SUCCESS, this.annotationLookup.find(TestClassWithIntercept.class, Intercept.class).type());
    }

    @Test
    public void testDoubleInterceptedRouteAnnotations() {
        Assertions.assertEquals("prehandler", this.annotationLookup.find(TestClassWithPreHandler.class, Intercept.class).path());
        Assertions.assertEquals("prehandler", this.annotationLookup.find(TestClassWithPreHandler.class, Route.class).path());
        Assertions.assertEquals(HttpMethod.GET, this.annotationLookup.find(TestClassWithPreHandler.class, Intercept.class).method());
        Assertions.assertEquals(HttpMethod.GET, this.annotationLookup.find(TestClassWithPreHandler.class, Route.class).method());
        Assertions.assertEquals(InterceptType.PRE_HANDLER, this.annotationLookup.find(TestClassWithPreHandler.class, Intercept.class).type());
    }

    @Test
    public void testDoubleInheritedAndDefaultedRouteAnnotation() {
        Assertions.assertEquals("aftersuccess", this.annotationLookup.find(TestClassWithAfterSuccess.class, Intercept.class).path());
        Assertions.assertEquals("aftersuccess", this.annotationLookup.find(TestClassWithAfterSuccess.class, Route.class).path());
        Assertions.assertEquals(HttpMethod.POST, this.annotationLookup.find(TestClassWithAfterSuccess.class, Intercept.class).method());
        Assertions.assertEquals(HttpMethod.POST, this.annotationLookup.find(TestClassWithAfterSuccess.class, Route.class).method());
        Assertions.assertEquals(InterceptType.AFTER_SUCCESS, this.annotationLookup.find(TestClassWithAfterSuccess.class, Intercept.class).type());
    }

    @Test
    public void reportErrorWhenMultipleAnnotationsWithSameBaseTypeFound() {
        final Exception exception = Assertions.assertThrows(Exception.class, () -> this.annotationLookup.find(TestClassWithSameBaseType.class, Base.class));
        Assertions.assertTrue(exception.getMessage().contains("Found more than one annotation on class"));

        this.annotationLookup.find(TestClassWithSameBaseType.class, Sub.class);
    }

    @Test
    public void jointAnnotationsAreStrictlyOrdered() {
        final List<Route> routes = this.annotationLookup.findAll(TestClassWithJointAnnotation2.class, Route.class);
        Assertions.assertEquals(Arrays.asList(HttpMethod.POST, HttpMethod.GET), routes.stream().map(Route::method).collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList("abc", ""), routes.stream().map(Route::path).collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList("", "jointRegex"), routes.stream().map(Route::regex).collect(Collectors.toList()));
    }
}
