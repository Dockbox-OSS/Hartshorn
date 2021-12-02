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

package org.dockbox.hartshorn.web;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
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
