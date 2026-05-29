/*
 * Copyright 2019-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
