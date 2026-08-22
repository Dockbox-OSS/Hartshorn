package test.org.dockbox.hartshorn.architecture;

import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class ArchitectureUtilities {

    public static <T> ArchCondition<T> complyWith(String description, BiConsumer<T, ConditionEvents> consumer) {
        return new ArchCondition<>(description) {
            @Override
            public void check(T item, ConditionEvents events) {
                consumer.accept(item, events);
            }
        };
    }
}
