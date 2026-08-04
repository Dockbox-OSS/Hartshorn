package org.dockbox.hartshorn.web.support.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dockbox.hartshorn.launchpad.resources.ResourceLookup;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.option.Option;

import java.io.IOException;
import java.net.URI;

public class StaticResourceServlet extends HttpServlet implements Reportable {

    private final ResourceLookup resourceLookup;
    private final String staticLocation;

    public StaticResourceServlet(ResourceLookup resourceLookup, String staticLocation) {
        this.resourceLookup = resourceLookup;
        this.staticLocation = StringUtilities.trimWith('/', staticLocation);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String resourcePath = req.getPathInfo();
        if (resourcePath == null || resourcePath.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Option<URI> resourceUri = resourceLookup.lookup(this.staticLocation + resourcePath).stream()
                .collect(Option.collector());

        if (resourceUri.absent()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        else {
            resp.setContentType(getServletContext().getMimeType(resourcePath));
            resourceUri.get().toURL().openStream().transferTo(resp.getOutputStream());
        }
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("staticLocation").writeString(this.staticLocation);
    }
}
