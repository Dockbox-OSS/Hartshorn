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

package test.org.dockbox.hartshorn.web;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.dockbox.hartshorn.web.HttpStatus;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

public final class RestAssert {

    private RestAssert() {}

    public static void assertStatus(final HttpStatus expected, final HttpResponse response) {
        Assertions.assertEquals(expected.value(), response.getStatusLine().getStatusCode());
    }

    public static void assertBody(final String body, final HttpResponse response) {
        try {
            final String entity = EntityUtils.toString(response.getEntity());
            Assertions.assertEquals(body, entity);
        }
        catch (final IOException e) {
            Assertions.fail(e);
        }
    }

}
