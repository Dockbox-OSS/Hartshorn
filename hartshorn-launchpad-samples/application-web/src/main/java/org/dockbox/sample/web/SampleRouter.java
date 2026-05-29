package org.dockbox.sample.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dockbox.hartshorn.reporting.DiagnosticsReport;
import org.dockbox.hartshorn.reporting.DiagnosticsReportCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.reporting.serialize.ObjectMapperReportSerializer;
import org.dockbox.hartshorn.web.GetRoute;
import org.dockbox.hartshorn.web.Header;
import org.dockbox.hartshorn.web.HttpStatus;
import org.dockbox.hartshorn.web.PathParameter;
import org.dockbox.hartshorn.web.QueryParameter;
import org.dockbox.hartshorn.web.Router;
import org.dockbox.hartshorn.web.message.RequestAttributes;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Router("/api")
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
    public ValidationResponseBody validationOutput(
            HttpServletRequest request,
            HttpServletResponse response,
            @Header("Accept-Encoding") String[] acceptEncoding,
            @PathParameter("param") String parameter,
            @QueryParameter("test") String testQueryParameter
    ) throws Exception {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, String> headers = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return new SampleRouter.ValidationResponseBody(
                request.getMethod(),
                request.getRequestURI(),
                request.getParameterMap(),
                testQueryParameter,
                headers,
                request.getReader().readAllAsString(),
                RequestAttributes.pathParameters(request),
                parameter,
                acceptEncoding
        );
    }

    public record ValidationResponseBody(
            String method,
            String path,
            Map<String, String[]> queryParameters,
            String testQueryParameter,
            Map<String, String> headers,
            String body,
            Map<String, String> pathParameters,
            String providedPathParameter,
            String[] encodings
    ) {
    }
}
