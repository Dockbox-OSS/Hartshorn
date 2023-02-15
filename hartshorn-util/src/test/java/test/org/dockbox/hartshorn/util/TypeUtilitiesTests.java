package test.org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.util.TypeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class TypeUtilitiesTests {

    public static Stream<Arguments> primitiveStrings() {
        return Stream.of(
                Arguments.of(Boolean.class, "true", true),
                Arguments.of(Byte.class, "0", (byte) 0),
                Arguments.of(Character.class, "\u0000", '\u0000'),
                Arguments.of(Double.class, "1.0d", 1.0d),
                Arguments.of(Float.class, "1.0f", 1.0f),
                Arguments.of(Integer.class, "1", 1),
                Arguments.of(Long.class, "0", 0L),
                Arguments.of(Short.class, "0", (short) 0)
        );
    }

    @ParameterizedTest
    @MethodSource("primitiveStrings")
    void testPrimitivesFromString(final Class<?> primitive, final String value, final Object real) {
        final Object out = TypeUtils.toPrimitive(primitive, value);
        Assertions.assertEquals(real, out);
    }
}
