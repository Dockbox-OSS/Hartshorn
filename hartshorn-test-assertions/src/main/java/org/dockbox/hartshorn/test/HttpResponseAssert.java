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

/**
 * Custom assertions for {@link HttpResponse} instances.
 *
 * @param <T> the response type
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class HttpResponseAssert<T> extends AbstractAssert<HttpResponseAssert<T>, HttpResponse<T>> {

    protected HttpResponseAssert(HttpResponse<T> actual) {
        super(actual, HttpResponseAssert.class);
    }

    /**
     * Asserts that the response has the given status code.
     *
     * @param expected the expected status code
     *
     * @return the current assertion, for fluent expressions
     */
    public HttpResponseAssert<T> hasStatusCode(int expected) {
        isNotNull();
        Assertions.assertThat(actual.statusCode())
                .as("HTTP response status code")
                .isEqualTo(expected);
        return myself;
    }

    /**
     * Asserts that the response has a successful status code (2xx).
     *
     * @return the current assertion, for fluent expressions
     */
    public HttpResponseAssert<T> hasSuccessfulStatus() {
        isNotNull();
        Assertions.assertThat(actual.statusCode())
                .as("HTTP response status code")
                .isBetween(200, 299);
        return myself;
    }

    /**
     * Asserts that the response has a client error status code (4xx).
     *
     * @return the current assertion, for fluent expressions
     */
    public HttpResponseAssert<T> hasClientErrorStatus() {
        isNotNull();
        Assertions.assertThat(actual.statusCode())
                .as("HTTP response status code")
                .isBetween(400, 499);
        return myself;
    }

    /**
     * Asserts that the response has a server error status code (5xx).
     *
     * @return the current assertion, for fluent expressions
     */
    public HttpResponseAssert<T> hasServerErrorStatus() {
        isNotNull();
        Assertions.assertThat(actual.statusCode())
                .as("HTTP response status code")
                .isBetween(500, 599);

        return myself;
    }

    /**
     * Asserts that the response has the given header.
     *
     * @param name the header name
     *
     * @return the current assertion, for fluent expressions
     */
    public HttpResponseAssert<T> hasHeader(String name) {
        isNotNull();
        Assertions.assertThat(actual.headers().allValues(name))
                .as("HTTP response header '%s'", name)
                .isNotEmpty();
        return myself;
    }

    /**
     * Asserts that the response has the given header with the given value.
     *
     * @param name the header name
     * @param expectedValue the expected header value
     *
     * @return the current assertion, for fluent expressions
     */
    public HttpResponseAssert<T> hasHeader(String name, String expectedValue) {
        isNotNull();
        Assertions.assertThat(actual.headers().allValues(name))
                .as("HTTP response header '%s'", name)
                .contains(expectedValue);
        return myself;
    }

    /**
     * Asserts that the response does not have the given header.
     *
     * @param name the header name
     *
     * @return the current assertion, for fluent expressions
     */
    public HttpResponseAssert<T> doesNotHaveHeader(String name) {
        isNotNull();
        Assertions.assertThat(actual.headers().allValues(name))
                .as("HTTP response header '%s'", name)
                .isEmpty();
        return myself;
    }

    /**
     * Asserts that the response has the given body.
     *
     * @param expected the expected body
     *
     * @return the current assertion, for fluent expressions
     */
    public HttpResponseAssert<T> hasBody(T expected) {
        isNotNull();
        Assertions.assertThat(actual.body())
                .as("HTTP response body")
                .isEqualTo(expected);
        return myself;
    }

    /**
     * Asserts that the response does not have a body.
     *
     * @return the current assertion, for fluent expressions
     */
    public HttpResponseAssert<T> hasNoBody() {
        isNotNull();
        Assertions.assertThat(actual.body())
                .as("HTTP response body")
                .isNull();
        return myself;
    }

    /**
     * Asserts that the response satisfies the given assertion.
     *
     * @param assertion the assertion to apply to the response body
     *
     * @return the current assertion, for fluent expressions
     */
    public HttpResponseAssert<T> satisfiesBody(Consumer<? super T> assertion) {
        isNotNull();
        Objects.requireNonNull(assertion, "assertion");
        assertion.accept(actual.body());
        return myself;
    }

    /**
     * Returns an assertion on the response body.
     *
     * @return an assertion on the response body
     */
    public ObjectAssert<T> body() {
        isNotNull();
        return Assertions.assertThat(actual.body()).as("HTTP response body");
    }
}
