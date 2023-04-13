package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProvider;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProviderFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.CollectionDefaultValueProviderFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.beans.beancontext.BeanContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Supplier;

public class CollectionDefaultValueProviderFactoryTests {
    @Test
    void testConcreteListCanBeProvided() {
        final DefaultValueProvider<ArrayList> provider = createProvider(ArrayList.class, ArrayList::new);

        final List<?> list = provider.defaultValue();
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list instanceof ArrayList<?>);
        Assertions.assertEquals(0, list.size());
    }

    @Test
    void testConcreteSetCanBeProvided() {
        final DefaultValueProvider<HashSet> provider = createProvider(HashSet.class, HashSet::new);

        final Set<?> set = provider.defaultValue();
        Assertions.assertNotNull(set);
        Assertions.assertEquals(0, set.spliterator().getExactSizeIfKnown());
    }

    @Test
    void testInterfaceListCanBeProvided() {
        final DefaultValueProvider<List> provider = createProvider(List.class);

        final List<?> list = provider.defaultValue();
        Assertions.assertNotNull(list);
        Assertions.assertEquals(0, list.size());
    }

    @Test
    void testInterfaceCollectionCanBeProvided() {
        final DefaultValueProvider<Collection> provider = createProvider(Collection.class);

        final Collection<?> collection = provider.defaultValue();
        Assertions.assertNotNull(collection);
        Assertions.assertEquals(0, collection.size());
    }

    @Test
    void testInterfaceSetCanBeProvided() {
        final DefaultValueProvider<Set> provider = createProvider(Set.class);

        final Set<?> set = provider.defaultValue();
        Assertions.assertNotNull(set);
        Assertions.assertEquals(0, set.spliterator().getExactSizeIfKnown());
    }

    @Test
    void testInterfaceQueueCanBeProvided() {
        final DefaultValueProvider<Queue> provider = createProvider(Queue.class);

        final Queue<?> queue = provider.defaultValue();
        Assertions.assertNotNull(queue);
        Assertions.assertEquals(0, queue.size());
    }

    @Test
    void testInterfaceDequeCanBeProvided() {
        final DefaultValueProvider<Deque> provider = createProvider(Deque.class);

        final Deque<?> deque = provider.defaultValue();
        Assertions.assertNotNull(deque);
        Assertions.assertEquals(0, deque.size());
    }

    @Test
    void testUnsupportedCollectionTypeCannotBeProvided() {
        final DefaultValueProvider<BeanContext> provider = createProvider(BeanContext.class);

        final Collection<?> collection = provider.defaultValue();
        Assertions.assertNull(collection);
    }

    private static <T extends Collection<?>> DefaultValueProvider<T> createProvider(final Class<T> type) {
        return createProvider(type, () -> null);
    }

    private static <T extends Collection<?>> DefaultValueProvider<T> createProvider(final Class<T> type, final Supplier<T> supplier) {
        final Introspector introspector = ConverterIntrospectionHelper.createIntrospectorForCollection(type, supplier);
        final DefaultValueProviderFactory<Collection<?>> factory = new CollectionDefaultValueProviderFactory(introspector).withDefaults();

        final DefaultValueProvider<T> provider = factory.create(type);
        Assertions.assertNotNull(provider);

        return provider;
    }
}
