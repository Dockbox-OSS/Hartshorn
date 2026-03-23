package org.dockbox.hartshorn.web.route.support;

import jakarta.servlet.http.HttpServletRequest;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.message.RequestAttributes;
import org.dockbox.hartshorn.web.route.HandlerMapping;
import org.dockbox.hartshorn.web.route.HandlerMappingRegistry;
import org.dockbox.hartshorn.web.route.HandlerMappingResolver;
import org.dockbox.hartshorn.web.route.RequestHandler;
import org.dockbox.hartshorn.web.route.SimpleHandlerMapping;

import java.util.Map;

/**
 * A {@link HandlerMappingResolver} that resolves handler mappings based on path pattern matching,
 * using a given {@link PathPatternMatcher} to match request paths against registered patterns in a
 * {@link HandlerMappingRegistry}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class PatternMatchingHandlerMappingResolver implements HandlerMappingResolver {

    private final PathPatternMatcher patternMatcher;
    private final HandlerMappingRegistry registry;

    public PatternMatchingHandlerMappingResolver(
            PathPatternMatcher patternMatcher,
            HandlerMappingRegistry registry
    ) {
        this.patternMatcher = patternMatcher;
        this.registry = registry;
    }

    @Override
    public Option<HandlerMapping> resolve(
            HttpMethod method,
            String path,
            HttpServletRequest request
    ) {
        Map<String, RequestHandler> mappings = this.registry.mappings(method);
        for (Map.Entry<String, RequestHandler> entry : mappings.entrySet()) {
            String pattern = entry.getKey();
            PathPatternMatcher.PatternMatch match = this.patternMatcher.matches(pattern, path);
            if (match.matches()) {
                RequestAttributes.setPathParameters(request, match.parameters());
                return Option.of(new SimpleHandlerMapping(method, pattern, entry.getValue()));
            }
        }
        return Option.empty();
    }
}
