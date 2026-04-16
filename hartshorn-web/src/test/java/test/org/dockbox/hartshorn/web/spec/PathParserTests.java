package test.org.dockbox.hartshorn.web.spec;

import org.assertj.core.api.Assertions;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.web.spec.ParameterPathPartSpec;
import org.dockbox.hartshorn.web.spec.PathSpec;
import org.dockbox.hartshorn.web.spec.StaticPathPartSpec;
import org.dockbox.hartshorn.web.spec.WildcardPathPartSpec;
import org.dockbox.hartshorn.web.spec.parser.SimplePathParser;
import org.junit.jupiter.api.Test;

import java.util.List;

@HartshornIntegrationTest(includeBasePackages = false)
public class PathParserTests {

    @Test
    void testPathParser() {
        SimplePathParser parser = new SimplePathParser('/', List.of(
                WildcardPathPartSpec::parse,
                ParameterPathPartSpec::parse,
                StaticPathPartSpec::parse
        ));
        PathSpec spec = parser.parse(
                "/users/{id}/posts/*/{id:\\w+}/{*wid}"
        );
        Assertions.assertThat(spec.parts())
                .hasSize(6);
    }
}
