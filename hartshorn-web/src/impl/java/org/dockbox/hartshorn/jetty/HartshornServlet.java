package org.dockbox.hartshorn.jetty;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.boot.Hartshorn;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.di.context.element.ParameterContext;
import org.dockbox.hartshorn.persistence.mapping.ObjectMapper;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.annotations.RequestBody;
import org.dockbox.hartshorn.web.annotations.RequestHeader;
import org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HartshornServlet extends HttpServlet {

    private HttpMethod httpMethod;
    private MethodContext<?, ?> methodContext;
    private ApplicationContext context;

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse res) {
        this.handleIf(HttpMethod.GET, req, res);
    }

    protected void handleIf(final HttpMethod method, final HttpServletRequest req, final HttpServletResponse res) {
        if (method.equals(this.httpMethod)) {
            res.addHeader("Hartshorn-Version", Hartshorn.VERSION);

            final Exceptional<?> result = this.methodContext.invoke(this.context, this.parameters(req, res));
            if (result.present()) {
                try {
                    res.setStatus(HttpStatus.OK_200);
                    res.getWriter().print(result.get());
                }
                catch (final IOException e) {
                    Except.handle(e);
                }
            }
        }
    }

    protected List<Object> parameters(final HttpServletRequest req, final HttpServletResponse res) {
        final List<Object> parameters = HartshornUtils.emptyList();
        final Exceptional<String> body = Exceptional.of(() -> req.getReader().lines().collect(Collectors.joining(System.lineSeparator())));

        for (final ParameterContext<?> parameter : this.methodContext.parameters()) {
            if (parameter.annotation(RequestBody.class).present()) {
                if (body.absent()) parameters.add(null);
                else if (parameter.type().is(String.class)) parameters.add(body.orNull());
                else {
                    final ObjectMapper objectMapper = this.context.get(ObjectMapper.class);
                    final Exceptional<?> value = objectMapper.read(body.orNull(), parameter.type());
                    parameters.add(value.orNull());
                }
            }
            else if (parameter.annotation(RequestHeader.class).present()) {
                final RequestHeader requestHeader = parameter.annotation(RequestHeader.class).get();
                if (parameter.type().is(String.class)) parameters.add(req.getHeader(requestHeader.value()));
                else if (parameter.type().is(Integer.class) || parameter.type().is(int.class)) parameters.add(req.getIntHeader(requestHeader.value()));
            }
            else {
                parameters.add(null);
            }
        }

        return parameters;
    }
}
