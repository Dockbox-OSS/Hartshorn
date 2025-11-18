/*
 * Copyright 2019-2025 the original author or authors.
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

package test.org.dockbox.hartshorn.introspect;


import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;
import org.dockbox.hartshorn.util.introspect.annotations.VirtualHierarchyAnnotationLookup;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import test.org.dockbox.hartshorn.introspect.annotations.Base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import test.org.dockbox.hartshorn.introspect.annotations.HttpMethod;
import test.org.dockbox.hartshorn.introspect.annotations.Intercept;
import test.org.dockbox.hartshorn.introspect.annotations.InterceptType;
import test.org.dockbox.hartshorn.introspect.annotations.Route;
import test.org.dockbox.hartshorn.introspect.annotations.Sub;
import test.org.dockbox.hartshorn.introspect.components.TestClassWithAfterSuccess;
import test.org.dockbox.hartshorn.introspect.components.TestClassWithBase;
import test.org.dockbox.hartshorn.introspect.components.TestClassWithGet;
import test.org.dockbox.hartshorn.introspect.components.TestClassWithIntercept;
import test.org.dockbox.hartshorn.introspect.components.TestClassWithJointAnnotation2;
import test.org.dockbox.hartshorn.introspect.components.TestClassWithMid;
import test.org.dockbox.hartshorn.introspect.components.TestClassWithPost;
import test.org.dockbox.hartshorn.introspect.components.TestClassWithPreHandler;
import test.org.dockbox.hartshorn.introspect.components.TestClassWithRoute;
import test.org.dockbox.hartshorn.introspect.components.TestClassWithSameBaseType;
import test.org.dockbox.hartshorn.introspect.components.TestClassWithSocketJS;
import test.org.dockbox.hartshorn.introspect.components.TestClassWithSub;

class AnnotationLookupTests {

    /**
     * Default implementation of {@link AnnotationLookup}, provided as a method
     * to allow overriding in subclasses of this test class.
     *
     * @return the annotation lookup instance to use
     */
    protected AnnotationLookup annotationLookup() {
        return new VirtualHierarchyAnnotationLookup();
    }

    @Test
    void baseAnnotationOnSub() {
        assertThat(this.annotationLookup().find(TestClassWithSub.class, Base.class).value()).isEqualTo("Sub");
    }

    @Test
    void baseAnnotationOnBase() {
        assertThat(this.annotationLookup().find(TestClassWithBase.class, Base.class).value()).isEqualTo("Base");
    }

    @Test
    void baseAnnotationOnMid() {
        assertThat(this.annotationLookup().find(TestClassWithMid.class, Base.class).value()).isEqualTo("Mid");
    }

    @Test
    void routeAnnotationOnRouteClass() {
        assertThat(this.annotationLookup().find(TestClassWithRoute.class, Route.class).method()).isEqualTo(HttpMethod.POST);
        assertThat(this.annotationLookup().find(TestClassWithRoute.class, Route.class).path()).isEqualTo("test");
    }

    @Test
    void routeAnnotationOnGetExtendedClass() {
        assertThat(this.annotationLookup().find(TestClassWithGet.class, Route.class).method()).isEqualTo(HttpMethod.GET);
        assertThat(this.annotationLookup().find(TestClassWithGet.class, Route.class).path()).isEqualTo("get");
    }

    @Test
    void routeAnnotationOnPostExtendedClass() {
        assertThat(this.annotationLookup().find(TestClassWithPost.class, Route.class).method()).isEqualTo(HttpMethod.POST);
        assertThat(this.annotationLookup().find(TestClassWithPost.class, Route.class).path()).isEqualTo("post");
    }

    @Test
    void routeAnnotationOnSocketExtendedClass() {
        assertThat(this.annotationLookup().find(TestClassWithSocketJS.class, Route.class).path()).isEqualTo("socketjs");
    }

    @Test
    void interceptedRouteAnnotations() {
        assertThat(this.annotationLookup().find(TestClassWithIntercept.class, Intercept.class).path()).isEqualTo("intercept");
        assertThat(this.annotationLookup().find(TestClassWithIntercept.class, Route.class).path()).isEqualTo("intercept");
        assertThat(this.annotationLookup().find(TestClassWithIntercept.class, Intercept.class).method()).isEqualTo(HttpMethod.POST);
        assertThat(this.annotationLookup().find(TestClassWithIntercept.class, Route.class).method()).isEqualTo(HttpMethod.POST);
        assertThat(this.annotationLookup().find(TestClassWithIntercept.class, Intercept.class).type()).isEqualTo(InterceptType.AFTER_SUCCESS);
    }

    @Test
    void doubleInterceptedRouteAnnotations() {
        assertThat(this.annotationLookup().find(TestClassWithPreHandler.class, Intercept.class).path()).isEqualTo("prehandler");
        assertThat(this.annotationLookup().find(TestClassWithPreHandler.class, Route.class).path()).isEqualTo("prehandler");
        assertThat(this.annotationLookup().find(TestClassWithPreHandler.class, Intercept.class).method()).isEqualTo(HttpMethod.GET);
        assertThat(this.annotationLookup().find(TestClassWithPreHandler.class, Route.class).method()).isEqualTo(HttpMethod.GET);
        assertThat(this.annotationLookup().find(TestClassWithPreHandler.class, Intercept.class).type()).isEqualTo(InterceptType.PRE_HANDLER);
    }

    @Test
    void doubleInheritedAndDefaultedRouteAnnotation() {
        assertThat(this.annotationLookup().find(TestClassWithAfterSuccess.class, Intercept.class).path()).isEqualTo("aftersuccess");
        assertThat(this.annotationLookup().find(TestClassWithAfterSuccess.class, Route.class).path()).isEqualTo("aftersuccess");
        assertThat(this.annotationLookup().find(TestClassWithAfterSuccess.class, Intercept.class).method()).isEqualTo(HttpMethod.POST);
        assertThat(this.annotationLookup().find(TestClassWithAfterSuccess.class, Route.class).method()).isEqualTo(HttpMethod.POST);
        assertThat(this.annotationLookup().find(TestClassWithAfterSuccess.class, Intercept.class).type()).isEqualTo(InterceptType.AFTER_SUCCESS);
    }

    @Test
    void reportErrorWhenMultipleAnnotationsWithSameBaseTypeFound() {
        Exception exception = assertThatExceptionOfType(Exception.class).isThrownBy(() -> this.annotationLookup().find(TestClassWithSameBaseType.class, Base.class)).actual();
        assertThat(exception.getMessage()).contains("Found more than one annotation on class");

        this.annotationLookup().find(TestClassWithSameBaseType.class, Sub.class);
    }

    @Test
    void jointAnnotationsAreStrictlyOrdered() {
        List<Route> routes = this.annotationLookup().findAll(TestClassWithJointAnnotation2.class, Route.class);
        assertThat(routes.stream().map(Route::method).collect(Collectors.toList())).isEqualTo(Arrays.asList(HttpMethod.POST, HttpMethod.GET));
        assertThat(routes.stream().map(Route::path)).containsExactlyElementsOf(Arrays.asList("abc", ""));
        assertThat(routes.stream().map(Route::regex)).containsExactlyElementsOf(Arrays.asList("", "jointRegex"));
    }
}
