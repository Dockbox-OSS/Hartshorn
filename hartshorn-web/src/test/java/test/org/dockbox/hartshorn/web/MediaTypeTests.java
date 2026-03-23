package test.org.dockbox.hartshorn.web;

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
    void mediaTypeInstancesAreReused() {
        MediaType instanceOne = MediaTypes.parse("application/json");
        MediaType instanceTwo = MediaTypes.parse("application/json");
        // Not just equal, but the same instance, as it should be cached
        assertThat(instanceOne).isSameAs(instanceTwo);
        // Predefined constant should be pre-populated in the cache, so it should
        // be the same instance as well
        assertThat(instanceOne).isSameAs(MediaTypes.APPLICATION_JSON);
    }
}
