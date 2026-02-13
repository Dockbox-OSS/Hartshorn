package test.org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.web.MimeType;
import org.dockbox.hartshorn.web.MimeTypes;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class MimeTypeTests {

    @Test
    void concreteTypeIsNotWildcard() {
        MimeType mimeType = new MimeType("text", "html");
        assertThat(mimeType.isWildcardType()).isFalse();
        assertThat(mimeType.isWildcardSubtype()).isFalse();
        assertThat(mimeType.isConcrete()).isTrue();
        assertThat(mimeType.toString()).isEqualTo("text/html");
    }

    @Test
    void wildcardTypeWithConcreteSubtypeIsWildcard() {
        MimeType mimeType = new MimeType("*", "html");
        assertThat(mimeType.isWildcardType()).isTrue();
        assertThat(mimeType.isWildcardSubtype()).isFalse();
        assertThat(mimeType.isConcrete()).isFalse();
        assertThat(mimeType.toString()).isEqualTo("*/html");
    }

    @Test
    void concreteTypeWithWildcardSubtypeIsWildcard() {
        MimeType mimeType = new MimeType("text", "*");
        assertThat(mimeType.isWildcardType()).isFalse();
        assertThat(mimeType.isWildcardSubtype()).isTrue();
        assertThat(mimeType.isConcrete()).isFalse();
        assertThat(mimeType.toString()).isEqualTo("text/*");
    }

    @Test
    void wildcardTypeWithWildcardSubtypeIsWildcard() {
        MimeType mimeType = new MimeType("*", "*");
        assertThat(mimeType.isWildcardType()).isTrue();
        assertThat(mimeType.isWildcardSubtype()).isTrue();
        assertThat(mimeType.isConcrete()).isFalse();
        assertThat(mimeType.toString()).isEqualTo("*/*");
    }

    @Test
    void mimeTypeParametersDoNotAffectWildcardPredicateForConcreteType() {
        MimeType mimeType = new MimeType("text", "html", Map.of(
                "charset", "utf-8"
        ));
        assertThat(mimeType.isWildcardType()).isFalse();
        assertThat(mimeType.isWildcardSubtype()).isFalse();
        assertThat(mimeType.isConcrete()).isTrue();
        assertThat(mimeType.toString()).isEqualTo("text/html;charset=utf-8");
    }

    @Test
    void mimeTypeParametersDoNotAffectWildcardPredicateForWildcardType() {
        MimeType mimeType = new MimeType("*", "html", Map.of(
                "charset", "utf-8"
        ));
        assertThat(mimeType.isWildcardType()).isTrue();
        assertThat(mimeType.isWildcardSubtype()).isFalse();
        assertThat(mimeType.isConcrete()).isFalse();
        assertThat(mimeType.toString()).isEqualTo("*/html;charset=utf-8");
    }

    @Test
    void mimeTypeParametersDoNotAffectWildcardPredicateForWildcardSubtype() {
        MimeType mimeType = new MimeType("text", "*", Map.of(
                "charset", "utf-8"
        ));
        assertThat(mimeType.isWildcardType()).isFalse();
        assertThat(mimeType.isWildcardSubtype()).isTrue();
        assertThat(mimeType.isConcrete()).isFalse();
        assertThat(mimeType.toString()).isEqualTo("text/*;charset=utf-8");
    }

    @Test
    void mimeTypeInstancesAreReused() {
        MimeType instanceOne = MimeTypes.parse("application/json");
        MimeType instanceTwo = MimeTypes.parse("application/json");
        // Not just equal, but the same instance, as it should be cached
        assertThat(instanceOne).isSameAs(instanceTwo);
        // Predefined constant should be pre-populated in the cache, so it should
        // be the same instance as well
        assertThat(instanceOne).isSameAs(MimeTypes.APPLICATION_JSON);
    }
}
