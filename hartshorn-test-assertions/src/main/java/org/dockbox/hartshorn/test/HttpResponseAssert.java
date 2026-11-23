/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.test;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;

import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.function.Consumer;

public class HttpResponseAssert<T> extends AbstractAssert<HttpResponseAssert<T>, HttpResponse<T>> {

    protected HttpResponseAssert(HttpResponse<T> actual) {
        super(actual, HttpResponseAssert.class);
    }

    public static <T> HttpResponseAssert<T> assertThat(HttpResponse<T> actual) {
        return new HttpResponseAssert<>(actual);
    }

    public HttpResponseAssert<T> hasStatusCode(int expected) {
        isNotNull();
        Assertions.assertThat(actual.statusCode()).as("HTTP response status code").isEqualTo(expected);
        return myself;
    }

    public HttpResponseAssert<T> hasSuccessfulStatus() {
        isNotNull();
        Assertions.assertThat(actual.statusCode()).as("HTTP response status code").isBetween(200, 299);
        return myself;
    }

    public HttpResponseAssert<T> hasClientErrorStatus() {
        isNotNull();
        Assertions.assertThat(actual.statusCode()).as("HTTP response status code").isBetween(400, 499);
        return myself;
    }

    public HttpResponseAssert<T> hasServerErrorStatus() {
        isNotNull();
        Assertions.assertThat(actual.statusCode()).as("HTTP response status code").isBetween(500, 599);

        return myself;
    }

    public HttpResponseAssert<T> hasHeader(String name) {
        isNotNull();
        Assertions.assertThat(actual.headers().allValues(name)).as("HTTP response header '%s'", name).isNotEmpty();
        return myself;
    }

    public HttpResponseAssert<T> hasHeader(String name, String expectedValue) {
        isNotNull();
        Assertions.assertThat(actual.headers().allValues(name)).as("HTTP response header '%s'", name).contains(expectedValue);
        return myself;
    }

    public HttpResponseAssert<T> doesNotHaveHeader(String name) {
        isNotNull();
        Assertions.assertThat(actual.headers().allValues(name)).as("HTTP response header '%s'", name).isEmpty();
        return myself;
    }

    public HttpResponseAssert<T> hasBody(T expected) {
        isNotNull();
        Assertions.assertThat(actual.body()).as("HTTP response body").isEqualTo(expected);
        return myself;
    }

    public HttpResponseAssert<T> hasNoBody() {
        isNotNull();
        Assertions.assertThat(actual.body()).as("HTTP response body").isNull();
        return myself;
    }

    public HttpResponseAssert<T> satisfiesBody(Consumer<? super T> assertion) {
        isNotNull();
        Objects.requireNonNull(assertion, "assertion");
        assertion.accept(actual.body());
        return myself;
    }

    public ObjectAssert<T> body() {
        isNotNull();
        return Assertions.assertThat(actual.body()).as("HTTP response body");
    }
}
