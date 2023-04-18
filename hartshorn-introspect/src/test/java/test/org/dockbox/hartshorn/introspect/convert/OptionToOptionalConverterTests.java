package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.convert.support.OptionToOptionalConverter;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class OptionToOptionalConverterTests {
    @Test
    void testPresentOptionConvertsToPresentOptional() {
        final OptionToOptionalConverter converter = new OptionToOptionalConverter();
        final Option<String> option = Option.of("test");
        final Optional<?> optional = converter.convert(option);
        Assertions.assertNotNull(optional);
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals("test", optional.get());
    }

    @Test
    void testEmptyOptionConvertsToEmptyOptional() {
        final OptionToOptionalConverter converter = new OptionToOptionalConverter();
        final Option<String> option = Option.empty();
        final Optional<?> optional = converter.convert(option);
        Assertions.assertNotNull(optional);
        Assertions.assertFalse(optional.isPresent());
    }

    @Test
    void testSuccessPresentAttemptConvertsToPresentOptional() {
        final OptionToOptionalConverter converter = new OptionToOptionalConverter();
        final Option<String> option = Attempt.of("test");
        final Optional<?> optional = converter.convert(option);
        Assertions.assertNotNull(optional);
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals("test", optional.get());
    }

    @Test
    void testFailurePresentAttemptConvertsToPresentOptional() {
        final OptionToOptionalConverter converter = new OptionToOptionalConverter();
        final Option<String> option = Attempt.of("test", new Exception());
        final Optional<?> optional = converter.convert(option);
        Assertions.assertNotNull(optional);
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals("test", optional.get());
    }

    @Test
    void testFailureAbsentAttemptConvertsToEmptyOptional() {
        final OptionToOptionalConverter converter = new OptionToOptionalConverter();
        final Option<String> option = Attempt.of(new Exception());
        final Optional<?> optional = converter.convert(option);
        Assertions.assertNotNull(optional);
        Assertions.assertFalse(optional.isPresent());
    }

    @Test
    void testSuccessAbsentAttemptConvertsToEmptyOptional() {
        final OptionToOptionalConverter converter = new OptionToOptionalConverter();
        final Option<String> option = Attempt.of((String) null);
        final Optional<?> optional = converter.convert(option);
        Assertions.assertNotNull(optional);
        Assertions.assertFalse(optional.isPresent());
    }
}
