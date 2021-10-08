package org.dockbox.hartshorn.jetty;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.boot.Hartshorn;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.web.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;

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
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        this.handleIf(HttpMethod.GET, req, res);
    }

    protected void handleIf(HttpMethod method, HttpServletRequest req, HttpServletResponse res) {
        if (method.equals(this.httpMethod)) {
            res.addHeader("Hartshorn-Version", Hartshorn.VERSION);
            Exceptional<?> result = this.methodContext.invoke(this.context);
            if (result.present()) {
                try {
                    res.setStatus(HttpStatus.OK_200);
                    res.getWriter().print(result.get());
                }
                catch (IOException e) {
                    Except.handle(e);
                }
            }
        }
    }
}
