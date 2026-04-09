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

package test.org.dockbox.hartshorn.web;

import org.assertj.core.api.Assertions;
import org.dockbox.hartshorn.web.MediaType;
import org.dockbox.hartshorn.web.MediaTypes;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class MediaTypeTests {

    @Test
    void concreteTypeIsNotWildcard() {
        MediaType mediaType = new MediaType("text", "html");
        assertThat(mediaType.isWildcardType()).isFalse();
        assertThat(mediaType.isWildcardSubtype()).isFalse();
        assertThat(mediaType.isConcrete()).isTrue();
        assertThat(mediaType.toString()).isEqualTo("text/html");
    }

    @Test
    void wildcardTypeWithConcreteSubtypeIsWildcard() {
        MediaType mediaType = new MediaType("*", "html");
        assertThat(mediaType.isWildcardType()).isTrue();
        assertThat(mediaType.isWildcardSubtype()).isFalse();
        assertThat(mediaType.isConcrete()).isFalse();
        assertThat(mediaType.toString()).isEqualTo("*/html");
    }

    @Test
    void concreteTypeWithWildcardSubtypeIsWildcard() {
        MediaType mediaType = new MediaType("text", "*");
        assertThat(mediaType.isWildcardType()).isFalse();
        assertThat(mediaType.isWildcardSubtype()).isTrue();
        assertThat(mediaType.isConcrete()).isFalse();
        assertThat(mediaType.toString()).isEqualTo("text/*");
    }

    @Test
    void wildcardTypeWithWildcardSubtypeIsWildcard() {
        MediaType mediaType = new MediaType("*", "*");
        assertThat(mediaType.isWildcardType()).isTrue();
        assertThat(mediaType.isWildcardSubtype()).isTrue();
        assertThat(mediaType.isConcrete()).isFalse();
        assertThat(mediaType.toString()).isEqualTo("*/*");
    }

    @Test
    void mediaTypeParametersDoNotAffectWildcardPredicateForConcreteType() {
        MediaType mediaType = new MediaType("text", "html", Map.of(
                "charset", "utf-8"
        ));
        assertThat(mediaType.isWildcardType()).isFalse();
        assertThat(mediaType.isWildcardSubtype()).isFalse();
        assertThat(mediaType.isConcrete()).isTrue();
        assertThat(mediaType.toString()).isEqualTo("text/html;charset=utf-8");
    }

    @Test
    void mediaTypeParametersDoNotAffectWildcardPredicateForWildcardType() {
        MediaType mediaType = new MediaType("*", "html", Map.of(
                "charset", "utf-8"
        ));
        assertThat(mediaType.isWildcardType()).isTrue();
        assertThat(mediaType.isWildcardSubtype()).isFalse();
        assertThat(mediaType.isConcrete()).isFalse();
        assertThat(mediaType.toString()).isEqualTo("*/html;charset=utf-8");
    }

    @Test
    void mediaTypeParametersDoNotAffectWildcardPredicateForWildcardSubtype() {
        MediaType mediaType = new MediaType("text", "*", Map.of(
                "charset", "utf-8"
        ));
        assertThat(mediaType.isWildcardType()).isFalse();
        assertThat(mediaType.isWildcardSubtype()).isTrue();
        assertThat(mediaType.isConcrete()).isFalse();
        assertThat(mediaType.toString()).isEqualTo("text/*;charset=utf-8");
    }

    @Test
    void mediaTypeInstancesOfKnownTypesAreReused() {
        MediaType instanceOne = MediaTypes.parse("application/json");
        MediaType instanceTwo = MediaTypes.parse("application/json");
        // Not just equal, but the same instance, as it should be cached
        assertThat(instanceOne).isSameAs(instanceTwo);
        // Predefined constant should be pre-populated in the cache, so it should
        // be the same instance as well
        assertThat(instanceOne).isSameAs(MediaTypes.APPLICATION_JSON);
    }

    @Test
    void mediaTypeInstancesOfUnknownTypesAreReused() {
        MediaType instanceOne = MediaTypes.parse("junit/test");
        MediaType instanceTwo = MediaTypes.parse("junit/test");
        // Not just equal, but the same instance, as it should be cached
        assertThat(instanceOne).isSameAs(instanceTwo);
    }

    @Test
    void invalidMediaTypeCannotBeParsed() {
        Assertions.assertThatCode(() -> {
            MediaTypes.parse("junit/test/wrong");
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
