package org.dockbox.hartshorn.web;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

public class RestAssert {

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
