package org.dockbox.hartshorn.web.message;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Map;

public class ParameterizedServletRequest extends HttpServletRequestWrapper {

    private final Map<String, String> pathParameters;

    public ParameterizedServletRequest(HttpServletRequest request, Map<String, String> pathParameters) {
        super(request);
        this.pathParameters = pathParameters;
    }

    public Map<String, String> pathParameters() {
        return pathParameters;
    }
}
