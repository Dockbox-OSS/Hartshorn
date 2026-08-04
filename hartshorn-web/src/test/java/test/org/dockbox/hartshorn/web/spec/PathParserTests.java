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

import org.dockbox.hartshorn.web.spec.ParameterPathPartSpec;
import org.dockbox.hartshorn.web.spec.PathPartSpec;
import org.dockbox.hartshorn.web.spec.PathSpec;
import org.dockbox.hartshorn.web.spec.StaticPathPartSpec;
import org.dockbox.hartshorn.web.spec.WildcardPathPartSpec;
import org.dockbox.hartshorn.web.spec.parser.PathParser;
import org.dockbox.hartshorn.web.spec.parser.SimplePathParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dockbox.hartshorn.test.HartshornAssertions.ofType;

public class PathParserTests {

    @Test
    void testPathParser() {
        PathParser parser = new SimplePathParser('/', List.of(
                WildcardPathPartSpec::parse,
                ParameterPathPartSpec::parse,
                StaticPathPartSpec::parse
        ));
        String unparsedPattern = "users/{id}/posts/*/{id:\\w+}/{*wid}";
        PathSpec spec = parser.parse(unparsedPattern);
        assertThat(spec.pattern()).isEqualTo(unparsedPattern);
        List<PathPartSpec> parts = spec.parts();
        assertThat(parts)
                .hasSize(6)
                .satisfiesExactly(
                        part -> assertThat(part)
                                .isInstanceOf(StaticPathPartSpec.class)
                                .extracting(PathPartSpec::stringValue)
                                .isEqualTo("users"),

                        part -> assertThat(part)
                                .asInstanceOf(ofType(ParameterPathPartSpec.class))
                                .extracting(ParameterPathPartSpec::parameterName)
                                .isEqualTo("id"),

                        part -> assertThat(part)
                                .isInstanceOf(StaticPathPartSpec.class)
                                .extracting(PathPartSpec::stringValue)
                                .isEqualTo("posts"),

                        part -> assertThat(part)
                                .isInstanceOf(WildcardPathPartSpec.class)
                                .extracting(PathPartSpec::stringValue)
                                .isEqualTo("*"),

                        part -> {
                            var parameterWithPattern = assertThat(part)
                                    .asInstanceOf(ofType(ParameterPathPartSpec.class));
                            parameterWithPattern
                                    .extracting(ParameterPathPartSpec::parameterName)
                                    .isEqualTo("id");
                            parameterWithPattern
                                    .extracting(ParameterPathPartSpec::pattern)
                                    .matches(p -> p.pattern().equals("\\w+"));
                        },

                        part -> {
                            var wildcardCapturePart = assertThat(part)
                                    .asInstanceOf(ofType(WildcardPathPartSpec.class));
                            wildcardCapturePart
                                    .extracting(WildcardPathPartSpec::captureName)
                                    .isEqualTo("wid");
                            wildcardCapturePart
                                    .extracting(PathPartSpec::stringValue)
                                    .isEqualTo("{*wid}");
                        }
                );
    }
}
