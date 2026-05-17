package test.org.dockbox.hartshorn.inject.conditions;

import org.dockbox.hartshorn.inject.condition.support.RequiresClass;
import org.dockbox.hartshorn.util.types.ClassFileUtilities;
import org.junit.jupiter.api.Test;

import java.lang.classfile.AnnotationValue;
import java.util.Map;

public class ClassConditionTests extends AbstractConditionTests {

    @Test
    void conditionMatchesForPresentClass() {
        assertReferenceConditionMatches(
                RequiresClass.class,
                Map.of(
                        "classes", AnnotationValue.ofArray(
                                AnnotationValue.ofClass(ClassFileUtilities.getClassDescriptor(String.class))
                        )
                )
        );
    }

    @Test
    void conditionFailsForAbsentClass() {
        assertReferenceConditionMatches(
                RequiresClass.class,
                Map.of(
                        "classNames", AnnotationValue.ofArray(
                                AnnotationValue.ofString("java.gnal.String")
                        )
                )
        );
    }
}
