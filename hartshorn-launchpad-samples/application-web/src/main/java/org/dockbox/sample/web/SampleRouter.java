package org.dockbox.sample.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dockbox.hartshorn.inject.annotations.Named;
import org.dockbox.hartshorn.reporting.DiagnosticsReport;
import org.dockbox.hartshorn.reporting.DiagnosticsReportCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.reporting.serialize.ObjectMapperReportSerializer;
import org.dockbox.hartshorn.web.HttpStatus;
import org.dockbox.hartshorn.web.message.RequestAttributes;
import org.dockbox.hartshorn.web.message.ResponseWriter;
import org.dockbox.hartshorn.web.rest.GetRoute;
import org.dockbox.hartshorn.web.rest.Header;
import org.dockbox.hartshorn.web.rest.RestRouter;

import java.util.Enumeration;
import java.util.HashMap;
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
            HttpServletResponse response,
            DiagnosticsReportCollector collector
    ) throws Exception {
        DiagnosticsReport diagnosticsReport = collector.report(reportable);
        String serializedReport = diagnosticsReport.serialize(
                new ObjectMapperReportSerializer.JsonReportSerializer()
        );
        response.setStatus(HttpStatus.OK.code());
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().write(serializedReport.getBytes());
    }

    @GetRoute("/validate/{param}")
    public void validationOutput(
            HttpServletRequest request,
            HttpServletResponse response,
            @Named("json") ResponseWriter<Object> responseWriter,
            @Header("Accept-Encoding") String[] acceptEncoding
    ) throws Exception {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, String> headers = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        SampleRouter.ValidationResponseBody body = new SampleRouter.ValidationResponseBody(
                request.getMethod(),
                request.getPathInfo(),
                request.getQueryString(),
                headers,
                request.getReader().readAllAsString(),
                RequestAttributes.pathParameters(request),
                acceptEncoding
        );
        response.setStatus(HttpStatus.OK.code());
        responseWriter.write(response, body);
    }

    private record ValidationResponseBody(
            String method,
            String path,
            String queryParameters,
            Map<String, String> headers,
            String body,
            Map<String, String> pathParameters,
            String[] encodings
    ) {
    }
}
