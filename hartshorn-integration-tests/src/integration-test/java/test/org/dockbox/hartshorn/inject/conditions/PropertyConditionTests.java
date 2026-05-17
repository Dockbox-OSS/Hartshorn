package test.org.dockbox.hartshorn.inject.conditions;

import org.dockbox.hartshorn.inject.condition.support.PropertyCondition;
import org.dockbox.hartshorn.inject.condition.support.RequiresProperty;
import org.dockbox.hartshorn.test.annotations.TestProperties;
import org.dockbox.hartshorn.util.types.TypeUtils;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class PropertyConditionTests extends AbstractConditionTests {

    @Test
    @TestProperties("property.sample=true")
    void testPropertyConditionMatchesIfValueIsEqual() {
        assertCondition(
                PropertyCondition.class,
                TypeUtils.annotation(RequiresProperty.class, Map.of(
                        "name", "property.sample",
                        "withValue", "true"
                ))
        ).matches();
    }

    @Test
    @TestProperties("property.sample=false")
    void testPropertyConditionMatchesIfValueIsNotEqual() {
        assertCondition(
                PropertyCondition.class,
                TypeUtils.annotation(RequiresProperty.class, Map.of(
                        "name", "property.sample",
                        "withValue", "true"
                ))
        ).doesNotMatchWithMessage("Expected property 'property.sample' to be true but was false");
    }

    @Test
    void testPropertyConditionDoesNotMatchIfPropertyIsAbsent() {
        assertCondition(
                PropertyCondition.class,
                TypeUtils.annotation(RequiresProperty.class, Map.of(
                        "name", "property.sample"
                ))
        ).doesNotMatchWithMessage("Could not find property 'property.sample'");
    }

    @Test
    void testPropertyConditionMatchesIfMatchIfMissingIsTrue() {
        assertCondition(
                PropertyCondition.class,
                TypeUtils.annotation(RequiresProperty.class, Map.of(
                        "name", "property.sample",
                        "matchIfMissing", true
                ))
        ).matches();
    }
}
