package org.dockbox.hartshorn.web.jetty;

import org.dockbox.hartshorn.web.spec.ParameterPathPartSpec;
import org.dockbox.hartshorn.web.spec.PathPartSpec;
import org.dockbox.hartshorn.web.spec.PathSpec;
import org.dockbox.hartshorn.web.spec.WildcardPathPartSpec;

public class JettyPathSpecTransformer {

    public String toJettyPathSpec(PathSpec pathSpec) {
        StringBuilder sb = new StringBuilder();
        for (PathPartSpec part : pathSpec.parts()) {
            sb.append("/");
            switch (part) {
                case WildcardPathPartSpec _, ParameterPathPartSpec _ -> sb.append("*");
                default -> sb.append(part.stringValue());
            }
        }
        return sb.toString();
    }
}
