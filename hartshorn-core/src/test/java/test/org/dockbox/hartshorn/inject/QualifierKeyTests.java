package test.org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.CompositeQualifier;
import org.dockbox.hartshorn.component.QualifierKey;
import org.dockbox.hartshorn.inject.Named;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class QualifierKeyTests {

    @Test
    void testQualifierKeysWithoutMetaEqual() {
        QualifierKey<SampleQualifier> expected = QualifierKey.of(SampleQualifier.class);
        QualifierKey<SampleQualifier> actual = QualifierKey.of(SampleQualifier.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testQualifierKeysWithMetaEqual() {
        QualifierKey<Named> expected = QualifierKey.of(Named.class, Map.of("value", "sample"));
        QualifierKey<Named> actual = QualifierKey.of(Named.class, Map.of("value", "sample"));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testQualifierWithoutMetaFailsIfTypeRequiresMeta() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> QualifierKey.of(Named.class));
    }

    @Test
    void testQualifierWithMetaFailsIfTypeDoesNotRequireMeta() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> QualifierKey.of(SampleQualifier.class, Map.of("value", "sample")));
    }

    @Test
    void testCompositeKeyEqualsIfKeysEqual() {
        QualifierKey<Named> namedQualifierOneA = QualifierKey.of(Named.class, Map.of("value", "sampleA"));
        QualifierKey<Named> namedQualifierOneB = QualifierKey.of(Named.class, Map.of("value", "sampleB"));
        CompositeQualifier compositeQualifierOne = new CompositeQualifier();
        compositeQualifierOne.add(namedQualifierOneA);
        compositeQualifierOne.add(namedQualifierOneB);

        QualifierKey<Named> namedQualifierTwoA = QualifierKey.of(Named.class, Map.of("value", "sampleA"));
        QualifierKey<Named> namedQualifierTwoB = QualifierKey.of(Named.class, Map.of("value", "sampleB"));
        CompositeQualifier compositeQualifierTwo = new CompositeQualifier();
        compositeQualifierTwo.add(namedQualifierTwoA);
        compositeQualifierTwo.add(namedQualifierTwoB);

        Assertions.assertEquals(compositeQualifierOne, compositeQualifierTwo);
    }

    @Test
    void testCompositeKeyDoesNotEqualIfOneKeyDoesNotEqual() {
        QualifierKey<Named> namedQualifierOneA = QualifierKey.of(Named.class, Map.of("value", "sampleA"));
        CompositeQualifier compositeQualifierOne = new CompositeQualifier();
        compositeQualifierOne.add(namedQualifierOneA);

        QualifierKey<Named> namedQualifierTwoB = QualifierKey.of(Named.class, Map.of("value", "sampleB"));
        CompositeQualifier compositeQualifierTwo = new CompositeQualifier();
        compositeQualifierTwo.add(namedQualifierTwoB);

        Assertions.assertNotEquals(compositeQualifierOne, compositeQualifierTwo);
    }

    public @interface SampleQualifier {}
}
