package org.dockbox.sample.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dockbox.hartshorn.reporting.DiagnosticsReport;
import org.dockbox.hartshorn.reporting.DiagnosticsReportCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.reporting.serialize.ObjectMapperReportSerializer;
import org.dockbox.hartshorn.util.collections.CollectionUtilities;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.MultiMapCollector;
import org.dockbox.hartshorn.util.stream.EntryStream;
import org.dockbox.hartshorn.web.GetRoute;
import org.dockbox.hartshorn.web.Header;
import org.dockbox.hartshorn.web.PathParameter;
import org.dockbox.hartshorn.web.QueryParameter;
import org.dockbox.hartshorn.web.Router;
import org.dockbox.hartshorn.web.message.RequestAttributes;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Router("/sample")
public class SampleRouter {

    @GetRoute("/error")
    public void error() {
        throw new ArrayIndexOutOfBoundsException(42);
    }

    @GetRoute("/report")
    public String writeReport(
            Reportable reportable,
            HttpServletResponse response,
            DiagnosticsReportCollector collector
    ) throws Exception {
        DiagnosticsReport diagnosticsReport = collector.report(reportable);
        response.setHeader("Content-Type", "application/json");
        return diagnosticsReport.serialize(
                new ObjectMapperReportSerializer.JsonReportSerializer()
        );
    }

    @GetRoute("/validate/{param}")
    public ValidationResponseBody validationOutput(
            HttpServletRequest request,
            HttpServletResponse response,
            @Header("Accept-Encoding") String[] acceptEncoding,
            @PathParameter("param") String parameter,
            @QueryParameter("test") String testQueryParameter
    ) throws Exception {
        Map<String, String> headers = CollectionUtilities.streamOf(request.getHeaderNames())
                .collect(Collectors.toMap(
                        Function.identity(),
                        request::getHeader
                ));
        MultiMap<String, String> parameters = EntryStream.of(request.getParameterMap())
                .flatMapValues(Arrays::stream)
                .collect(MultiMapCollector.toMultiMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
        return new SampleRouter.ValidationResponseBody(
                request.getMethod(),
                request.getRequestURI(),
                testQueryParameter,
                parameters,
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
            String testQueryParameter,
            MultiMap<String, String> queryParameters,
            Map<String, String> headers,
            String body,
            Map<String, String> pathParameters,
            String providedPathParameter,
            String[] encodings
    ) {
    }
}
