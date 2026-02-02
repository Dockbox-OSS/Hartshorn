package org.dockbox.sample.web;

import org.dockbox.hartshorn.inject.annotations.Named;
import org.dockbox.hartshorn.reporting.DiagnosticsReport;
import org.dockbox.hartshorn.reporting.DiagnosticsReportCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.reporting.serialize.ObjectMapperReportSerializer;
import org.dockbox.hartshorn.util.collections.StandardMultiMap;
import org.dockbox.hartshorn.web.HttpStatus;
import org.dockbox.hartshorn.web.message.ResponseWriter;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;
import org.dockbox.hartshorn.web.rest.GetRoute;
import org.dockbox.hartshorn.web.rest.Header;
import org.dockbox.hartshorn.web.rest.PathParameter;
import org.dockbox.hartshorn.web.rest.QueryParameter;
import org.dockbox.hartshorn.web.rest.RestRouter;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;

@RestRouter
public class SampleRouter {

    @GetRoute("/error")
    public void error() {
        throw new ArrayIndexOutOfBoundsException(42);
    }

    @GetRoute("/report")
    public void writeReport(
            Reportable reportable,
            WebResponse response,
            DiagnosticsReportCollector collector
    ) throws Exception {
        DiagnosticsReport diagnosticsReport = collector.report(reportable);
        String serializedReport = diagnosticsReport.serialize(
                new ObjectMapperReportSerializer.JsonReportSerializer()
        );
        response.status(HttpStatus.OK);
        response.headers().set("Content-Type", "application/json");
        response.write(ByteBuffer.wrap(serializedReport.getBytes()));
    }

    @GetRoute("/validate/{param}")
    public void validationOutput(
            WebRequest request,
            WebResponse response,
            @Named("json") ResponseWriter<Object> responseWriter,
            @Header("Accept-Encoding") String[] acceptEncoding,
            @PathParameter("param") String pathParameter,
            @QueryParameter("query") int queryParameter
    ) throws Exception {
        SampleRouter.ValidationResponseBody body = new SampleRouter.ValidationResponseBody(
                request.method().name(),
                request.path(),
                ((StandardMultiMap) request.query().asMultiMap()).map(),
                request.headers().asMap(),
                request.body().asString(),
                request.pathParameters().asMap(),
                acceptEncoding,
                pathParameter,
                queryParameter
        );
        response.status(HttpStatus.OK);
        response.write(responseWriter, body);
    }

    private record ValidationResponseBody(
            String method,
            String path,
            Map<String, Collection<String>> queryParameters,
            Map<String, String> headers,
            String body,
            Map<String, String> pathParameters,
            String[] encodings,
            String pathParameter,
            int queryParameter
    ) {
    }
}
