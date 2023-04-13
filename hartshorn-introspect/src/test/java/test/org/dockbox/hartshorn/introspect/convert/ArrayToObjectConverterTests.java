package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;
import org.dockbox.hartshorn.util.introspect.convert.StandardConversionService;
import org.dockbox.hartshorn.util.introspect.convert.support.ArrayToObjectConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToNumberConverterFactory;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ArrayToObjectConverterTests {

    @Test
    void testArrayConvertsToObjectIfLengthIsOne() {
        final GenericConverter converter = new ArrayToObjectConverter(null);
        final Object[] array = { "test" };
        final Object result = converter.convert(array, Object[].class, Object.class);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("test", result);
    }

    @Test
    void testArrayDoesNotConvertIfLengthIsZero() {
        final GenericConverter converter = new ArrayToObjectConverter(null);
        final Object[] array = {};
        final Object result = converter.convert(array, Object[].class, Object.class);
        Assertions.assertNull(result);
    }

    @Test
    void testArrayDoesNotConvertIfLengthIsMoreThanOne() {
        final GenericConverter converter = new ArrayToObjectConverter(null);
        final Object[] array = { "test", "test" };
        final Object result = converter.convert(array, Object[].class, Object.class);
        Assertions.assertNull(result);
    }

    @Test
    void testArrayElementIsConvertedIfComponentTypeDoesNotMatch() {
        final TypeView<Integer> integerTypeView = Mockito.mock(TypeView.class);
        Mockito.when(integerTypeView.type()).thenReturn(Integer.class);
        Mockito.when(integerTypeView.cast(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(integerTypeView.defaultOrNull()).thenAnswer(invocation -> 0);

        final Introspector introspector = Mockito.mock(Introspector.class);
        Mockito.when(introspector.introspect(Integer.class)).thenReturn(integerTypeView);

        final StandardConversionService conversionService = new StandardConversionService(introspector);
        conversionService.addConverter(String.class, Integer.class, new StringToNumberConverterFactory().create(Integer.class));

        final GenericConverter converter = new ArrayToObjectConverter(conversionService);
        final Object[] array = { "1" };
        // Use Object[].class as the source type, so we can test that the element type is used, instead of the array component type
        final Object result = converter.convert(array, Object[].class, Integer.class);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result);
    }
}