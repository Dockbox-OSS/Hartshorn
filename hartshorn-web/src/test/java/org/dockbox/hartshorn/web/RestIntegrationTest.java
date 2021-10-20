package org.dockbox.hartshorn.web;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.dockbox.hartshorn.config.annotations.UseConfigurations;
import org.dockbox.hartshorn.di.annotations.activate.UseServiceProvision;
import org.dockbox.hartshorn.events.annotations.UseEvents;
import org.dockbox.hartshorn.test.ApplicationAwareTest;
import org.dockbox.hartshorn.web.annotations.UseHttpServer;

import java.io.IOException;
import java.util.function.Function;

@UseEvents
@UseHttpServer
@UseConfigurations
@UseServiceProvision
public abstract class RestIntegrationTest extends ApplicationAwareTest {

    protected static final String ADDRESS = "http://localhost:" + ServerBootstrap.DEFAULT_PORT;

    protected CloseableHttpResponse request(final String uri, final HttpMethod method, final String body) throws IOException, InterruptedException {
        final CloseableHttpClient client = HttpClients.createDefault();
        final Function<String, HttpUriRequest> requestProvider = switch (method) {
            case GET -> HttpGet::new;
            case HEAD -> HttpHead::new;
            case POST -> HttpPost::new;
            case PUT -> HttpPut::new;
            case DELETE -> HttpDelete::new;
            case OPTIONS -> HttpOptions::new;
            case TRACE -> HttpTrace::new;
            case PATCH -> HttpPatch::new;
            default -> throw new IllegalArgumentException("Method %s is unsupported".formatted(method));
        };
        final HttpUriRequest request = requestProvider.apply(ADDRESS + uri);
        if (body != null && request instanceof HttpEntityEnclosingRequest entityEnclosingRequest)
            entityEnclosingRequest.setEntity(new StringEntity(body));

        return client.execute(request);
    }

}
