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

package org.dockbox.hartshorn.web;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class HttpMethodEndpointTests extends RestIntegrationTest {

    @Test
    void testGet() throws IOException {
        final CloseableHttpResponse response = this.request("/get", HttpMethod.GET, "");
        RestAssert.assertStatus(HttpStatus.OK, response);
        RestAssert.assertBody("JUnit GET", response);
    }

    @Test
    public void testPostWithBody() throws IOException {
        final CloseableHttpResponse response = this.request("/post", HttpMethod.POST, "Hello world!");
        RestAssert.assertStatus(HttpStatus.OK, response);
        RestAssert.assertBody("Hello world!", response);
    }

    @Test
    void testGetWithHeader() throws IOException {
        final CloseableHttpResponse response = this.request("/header", HttpMethod.GET, "", new BasicHeader("http-demo", "Hello headers!"));
        RestAssert.assertStatus(HttpStatus.OK, response);
        RestAssert.assertBody("Hello headers!", response);
    }

    @Test
    void testGetWithInject() throws IOException {
        final CloseableHttpResponse response = this.request("/inject", HttpMethod.GET, "");
        RestAssert.assertStatus(HttpStatus.OK, response);
        RestAssert.assertBody("true", response);
    }
}
