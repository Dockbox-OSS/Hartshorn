/*
 * Copyright 2019-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.org.dockbox.hartshorn.util.introspect;

import org.dockbox.hartshorn.util.collections.CollectionUtilities;
import org.dockbox.hartshorn.util.types.GenericType;
import org.dockbox.hartshorn.util.collections.BiMultiMap;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.TypeParameterList;
import org.dockbox.hartshorn.util.introspect.TypeParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for introspecting type parameters.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
@SuppressWarnings({"rawtypes", "InterfaceMayBeAnnotatedFunctional"})
public abstract class TypeParameterIntrospectionTests {

    protected abstract Introspector introspector();

    private interface TypeWithoutTypeBounds<T> {}
    private interface TypeWithSingleTypeBound<T extends Number> {}
    private interface TypeWithMultipleTypeBounds<T extends Number & Function<?,?> & Predicate<?>> {}

    @Test
    public void variableGenericTypeWithSingleUpperbound() {
        TypeView<TypeWithSingleTypeBound> typeView = this.introspector().introspect(TypeWithSingleTypeBound.class);
        assertThat(typeView.type()).isSameAs(TypeWithSingleTypeBound.class);

        this.testVariableWithExpectedBounds(typeView, Number.class);
    }

    @Test
    public void variableGenericTypeWithMultipleUpperbound() {
        TypeView<TypeWithMultipleTypeBounds> typeView = this.introspector().introspect(TypeWithMultipleTypeBounds.class);
        assertThat(typeView.type()).isSameAs(TypeWithMultipleTypeBounds.class);

        this.testVariableWithExpectedBounds(typeView, Number.class, Function.class, Predicate.class);
    }

    @Test
    void variableGenericTypeWithoutExplicitUpperbounds() {
        TypeView<TypeWithoutTypeBounds> typeView = this.introspector().introspect(TypeWithoutTypeBounds.class);
        assertThat(typeView.type()).isSameAs(TypeWithoutTypeBounds.class);

        this.testVariableWithExpectedBounds(typeView, Object.class);
    }

    private void testVariableWithExpectedBounds(TypeView<?> type, Class<?>... expectedBounds) {
        List<TypeParameterView> inputTypes = type.typeParameters().allInput().asList();
        assertThat(inputTypes).hasSize(1);

        TypeParameterView parameterView = inputTypes.get(0);
        assertThat(parameterView.isBounded()).isTrue();
        assertThat(parameterView.isVariable()).isTrue();

        Set<TypeView<?>> upperBounds = parameterView.upperBounds();
        assertThat(upperBounds).hasSameSizeAs(expectedBounds);
        for (Class<?> expectedBound : expectedBounds) {
            assertThat(upperBounds.stream().anyMatch(typeView -> typeView.type().equals(expectedBound))).isTrue();
        }
    }

    private interface NumberPredicate<U extends Number> extends Predicate<U> {}
    private interface IntegerPredicate extends NumberPredicate<Integer> {}

    @Test
    void outputParameterWithConcreteValue() {
        TypeView<IntegerPredicate> typeView = this.introspector().introspect(IntegerPredicate.class);
        assertThat(typeView.type()).isSameAs(IntegerPredicate.class);

        List<TypeParameterView> outputTypes = typeView.typeParameters().allOutput().asList();
        assertThat(outputTypes).hasSize(1);

        TypeParameterView parameterView = outputTypes.get(0);
        assertThat(parameterView.isVariable()).isFalse();

        TypeView<?> resolvedType = parameterView.resolvedType().get();
        assertThat(resolvedType.type()).isSameAs(Integer.class);
    }

    @Test
    void outputParameterWithVariableValue() {
        TypeView<NumberPredicate> typeView = this.introspector().introspect(NumberPredicate.class);
        assertThat(typeView.type()).isSameAs(NumberPredicate.class);

        List<TypeParameterView> outputTypes = typeView.typeParameters().allOutput().asList();
        assertThat(outputTypes).hasSize(1);

        TypeParameterView parameterView = outputTypes.get(0);
        assertThat(parameterView.isVariable()).isTrue();
        assertThat(parameterView.resolvedType().absent()).isTrue();
        assertThat(parameterView.isBounded()).isTrue();

        Set<TypeView<?>> upperBounds = parameterView.upperBounds();
        assertThat(upperBounds).hasSize(1);
        assertThat(CollectionUtilities.first(upperBounds).type()).isSameAs(Number.class);
    }

    @Test
    void inputRepresentingOutputVariable() {
        TypeView<NumberPredicate> typeView = this.introspector().introspect(NumberPredicate.class);
        assertThat(typeView.type()).isSameAs(NumberPredicate.class);

        List<TypeParameterView> inputTypes = typeView.typeParameters().allInput().asList();
        assertThat(inputTypes).hasSize(1);

        TypeParameterView parameterView = inputTypes.get(0);
        assertThat(parameterView.isVariable()).isTrue();
        assertThat(parameterView.resolvedType().absent()).isTrue();
        assertThat(parameterView.isBounded()).isTrue();

        Set<TypeParameterView> represents = parameterView.represents();
        assertThat(represents).hasSize(1);

        TypeParameterView representing = CollectionUtilities.first(represents);
        assertThat(representing.isVariable()).isTrue();
        assertThat(representing.consumedBy().type()).isSameAs(Predicate.class);
    }

    private interface NumberFunctionAndPredicate<U extends Number> extends Function<U, U>, Predicate<U> {}

    @Test
    void inputRepresentingMultipleOutputVariables() {
        TypeView<NumberFunctionAndPredicate> typeView = this.introspector().introspect(NumberFunctionAndPredicate.class);
        assertThat(typeView.type()).isSameAs(NumberFunctionAndPredicate.class);

        List<TypeParameterView> inputTypes = typeView.typeParameters().allInput().asList();
        assertThat(inputTypes).hasSize(1);

        TypeParameterView parameterView = inputTypes.get(0);
        assertThat(parameterView.isVariable()).isTrue();
        assertThat(parameterView.resolvedType().absent()).isTrue();
        assertThat(parameterView.isBounded()).isTrue();

        Set<TypeParameterView> represents = parameterView.represents();
        assertThat(represents).hasSize(3);

        Map<Class<?>, List<TypeParameterView>> representationsByType = represents.stream()
                .collect(Collectors.groupingBy(typeParameterView -> typeParameterView.consumedBy().type()));
        assertThat(representationsByType)
                .containsKey(Function.class)
                .containsKey(Predicate.class);

        List<TypeParameterView> functionRepresentations = representationsByType.get(Function.class);
        assertThat(functionRepresentations).hasSize(2);
        assertThat(functionRepresentations.stream().allMatch(TypeParameterView::isVariable)).isTrue();

        List<TypeParameterView> predicateRepresentations = representationsByType.get(Predicate.class);
        assertThat(predicateRepresentations).hasSize(1);
    }

    @Test
    void outputToInputReferencesCorrectDeclarations() {
        TypeView<NumberPredicate> typeView = this.introspector().introspect(NumberPredicate.class);
        assertThat(typeView.type()).isSameAs(NumberPredicate.class);

        List<TypeParameterView> outputParameters = typeView.typeParameters().allOutput().asList();
        assertThat(outputParameters).hasSize(1);

        TypeParameterView outputParameter = outputParameters.get(0);
        assertThat(outputParameter.isVariable()).isTrue();
        assertThat(outputParameter.declaredBy().type()).isSameAs(NumberPredicate.class);
        assertThat(outputParameter.name()).isEqualTo("U");

        TypeParameterView inputParameter = outputParameter.asInputParameter();
        assertThat(inputParameter.isVariable()).isTrue();
        assertThat(inputParameter.declaredBy().type()).isSameAs(Predicate.class);
        assertThat(inputParameter.name()).isEqualTo("T");
    }

    @Test
    void inputToInputReturnsSelf() {
        TypeView<NumberPredicate> typeView = this.introspector().introspect(NumberPredicate.class);
        assertThat(typeView.type()).isSameAs(NumberPredicate.class);

        List<TypeParameterView> inputParameters = typeView.typeParameters().allInput().asList();
        assertThat(inputParameters).hasSize(1);

        TypeParameterView inputParameter = inputParameters.get(0);
        assertThat(inputParameter.isVariable()).isTrue();
        assertThat(inputParameter.declaredBy().type()).isSameAs(NumberPredicate.class);
        assertThat(inputParameter.name()).isEqualTo("U");

        TypeParameterView asInputParameter = inputParameter.asInputParameter();
        assertThat(asInputParameter).isSameAs(inputParameter);
    }

    private interface XYtoYXFunction<X, Y> extends Function<Y, X> {}

    @Test
    void inputToOutputCorrectlyUpdatesIndex() {
        TypeView<XYtoYXFunction> typeView = this.introspector().introspect(XYtoYXFunction.class);
        assertThat(typeView.type()).isSameAs(XYtoYXFunction.class);

        List<TypeParameterView> inputParameters = typeView.typeParameters().allInput().asList();
        assertThat(inputParameters).hasSize(2);

        assertParameterAtIndexReferencesParameterAtIndex(typeView, 0, 1);
        assertParameterAtIndexReferencesParameterAtIndex(typeView, 1, 0);
    }

    private static void assertParameterAtIndexReferencesParameterAtIndex(TypeView<?> typeView, int inputIndex, int outputIndex) {
        Option<TypeParameterView> parameter = typeView.typeParameters().atIndex(inputIndex);
        assertThat(parameter.present()).isTrue();
        TypeParameterView parameterView = parameter.get();
        assertThat(parameterView.index()).isEqualTo(inputIndex);

        Set<TypeParameterView> parameterRepresents = parameterView.represents();
        assertThat(parameterRepresents).hasSize(1);

        TypeParameterView parameterRepresentsView = CollectionUtilities.first(parameterRepresents);
        assertThat(parameterRepresentsView.index()).isEqualTo(outputIndex);
    }

    @Test
    void inputIsInput() {
        TypeView<NumberPredicate> typeView = this.introspector().introspect(NumberPredicate.class);
        assertThat(typeView.type()).isSameAs(NumberPredicate.class);

        List<TypeParameterView> inputParameters = typeView.typeParameters().allInput().asList();
        assertThat(inputParameters).hasSize(1);

        TypeParameterView inputParameter = inputParameters.get(0);
        assertThat(inputParameter.isInputParameter()).isTrue();
        assertThat(inputParameter.isOutputParameter()).isFalse();
    }

    @Test
    void outputIsOutput() {
        TypeView<NumberPredicate> typeView = this.introspector().introspect(NumberPredicate.class);
        assertThat(typeView.type()).isSameAs(NumberPredicate.class);

        List<TypeParameterView> outputParameters = typeView.typeParameters().allOutput().asList();
        assertThat(outputParameters).hasSize(1);

        TypeParameterView outputParameter = outputParameters.get(0);
        assertThat(outputParameter.isOutputParameter()).isTrue();
        assertThat(outputParameter.isInputParameter()).isFalse();
    }

    @Test
    void resolveForSelfWithExplicitParameters() {
        TypeView<Collection<String>> typeView = this.introspector().introspect(new GenericType<>() {});
        assertThat(typeView.type()).isSameAs(Collection.class);

        TypeParametersIntrospector typeParameters = typeView.typeParameters();
        List<TypeParameterView> collectionParameters = typeParameters.inputFor(Collection.class).asList();
        assertThat(collectionParameters).hasSize(1);

        TypeParameterView collectionParameter = collectionParameters.get(0);
        assertThat(collectionParameter.isVariable()).isFalse(); // Should not be 'E'
        assertThat(collectionParameter.resolvedType().get().type()).isSameAs(String.class);
    }

    @Test
    void resolveForDirectParentWithExplicitParameters() {
        TypeView<Collection<String>> typeView = this.introspector().introspect(new GenericType<>() {});
        assertThat(typeView.type()).isSameAs(Collection.class);

        TypeParametersIntrospector typeParameters = typeView.typeParameters();
        List<TypeParameterView> collectionParameters = typeParameters.inputFor(Iterable.class).asList();
        assertThat(collectionParameters).hasSize(1);

        TypeParameterView collectionParameter = collectionParameters.get(0);
        assertThat(collectionParameter.isVariable()).isFalse(); // Should not be 'E'
        assertThat(collectionParameter.resolvedType().get().type()).isSameAs(String.class);
    }

    @Test
    void resolveForIndirectParentWithExplicitParameters() {
        TypeView<LinkedList<String>> typeView = this.introspector().introspect(new GenericType<>() {});
        assertThat(typeView.type()).isSameAs(LinkedList.class);

        TypeParametersIntrospector typeParameters = typeView.typeParameters();
        List<TypeParameterView> collectionParameters = typeParameters.inputFor(Iterable.class).asList();
        assertThat(collectionParameters).hasSize(1);

        TypeParameterView collectionParameter = collectionParameters.get(0);
        assertThat(collectionParameter.isVariable()).isFalse(); // Should not be 'E'
        assertThat(collectionParameter.resolvedType().get().type()).isSameAs(String.class);
    }

    @Test
    void outputAsMapResolvesAllToParent() {
        TypeView<NumberFunctionAndPredicate> typeView = this.introspector().introspect(NumberFunctionAndPredicate.class);
        assertThat(typeView.type()).isSameAs(NumberFunctionAndPredicate.class);

        TypeParametersIntrospector typeParameters = typeView.typeParameters();
        TypeParameterList outputParameters = typeParameters.allOutput();
        BiMultiMap<TypeParameterView, TypeParameterView> multiMap = outputParameters.asMap();
        // Should be 1:1 mappings, so the size should be the same
        assertThat(multiMap.size()).isEqualTo(outputParameters.count());

        Set<TypeParameterView> keys = multiMap.keySet();
        assertThat(keys).hasSize(outputParameters.count());

        Collection<TypeParameterView> values = multiMap.allValues();
        // Output parameters should only map to the input parameters of their target types
        assertThat(values).hasSize(outputParameters.count());
    }

    @Test
    void inputAsMapResolvesAllToOutput() {
        TypeView<NumberFunctionAndPredicate> typeView = this.introspector().introspect(NumberFunctionAndPredicate.class);
        assertThat(typeView.type()).isSameAs(NumberFunctionAndPredicate.class);

        TypeParametersIntrospector typeParameters = typeView.typeParameters();
        TypeParameterList inputParameters = typeParameters.allInput();

        BiMultiMap<TypeParameterView, TypeParameterView> multiMap = inputParameters.asMap();
        Set<TypeParameterView> keys = multiMap.keySet();
        assertThat(keys)
                // Note: unlike output parameters, I -> O mappings are not 1:1, but are 1:n (where n is the number of
                // output parameters), so the size of the map will be different, but the keys should be the same
                .hasSize(inputParameters.count())
                .hasSize(1);
        Collection<TypeParameterView> values = multiMap.get(CollectionUtilities.first(keys));
        TypeParameterList outputParameters = typeParameters.allOutput();
        assertThat(values).hasSize(outputParameters.count());

        List<TypeParameterView> outputParametersList = outputParameters.asList();
        assertThat(values).containsAll(outputParametersList);
    }
}
