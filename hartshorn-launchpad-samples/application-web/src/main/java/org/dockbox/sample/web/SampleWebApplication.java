package org.dockbox.sample.web;

import org.dockbox.hartshorn.inject.annotations.CompositeMember;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.launchpad.HartshornApplication;
import org.dockbox.hartshorn.reporting.DiagnosticsReport;
import org.dockbox.hartshorn.reporting.DiagnosticsReportCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.reporting.UseReporting;
import org.dockbox.hartshorn.reporting.serialize.ObjectMapperReportSerializer;
import org.dockbox.hartshorn.util.collections.StandardMultiMap;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.web.UseWebServer;
import org.dockbox.hartshorn.web.message.ResponseWriter;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;
import org.dockbox.hartshorn.web.route.RouterPathConfigurer;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;

@UseWebServer
@UseReporting
public class SampleWebApplication {

    static void main(String[] args) {
        HartshornApplication.create(args);
    }

    @Singleton
    @CompositeMember
    Customizer<RouterPathConfigurer> routeCustomizer(
            ResponseWriter<Object> responseWriter,
            Reportable reportable,
            DiagnosticsReportCollector collector
    ) {
        return routes -> {
            routes.get("/validate", (request, response) -> validationOutput(responseWriter, request, response));
            routes.get("/report", (_, response) -> writeReport(reportable, collector, response));
            routes.get("/error", (_, _) -> {
                throw new ArrayIndexOutOfBoundsException(42);
            });
        };
    }

    private void writeReport(
            Reportable reportable,
            DiagnosticsReportCollector collector,
            WebResponse response
    ) throws Exception {
        DiagnosticsReport diagnosticsReport = collector.report(reportable);
        String serializedReport =
                diagnosticsReport.serialize(new ObjectMapperReportSerializer.JsonReportSerializer());
        response.status(200);
        response.headers().set("Content-Type", "application/json");
        response.write(ByteBuffer.wrap(serializedReport.getBytes()));
    }

    private static void validationOutput(
            ResponseWriter<Object> responseWriter,
            WebRequest request,
            WebResponse response
    ) throws Exception {
        ValidationResponseBody body = new ValidationResponseBody(
                request.method().name(),
                request.path(),
                ((StandardMultiMap)request.query().asMultiMap()).map(),
                request.headers().asMap(),
                request.body().asString()
        );
        response.status(200);
        response.write(responseWriter, body);
    }

    private record ValidationResponseBody(
            String method,
            String path,
            Map<String, Collection<String>> queryParameters,
            Map<String, String> headers,
            String body
    ) {}
}
