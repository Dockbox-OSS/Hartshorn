/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.util.introspect;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.dockbox.hartshorn.util.collections.ArrayListHashBiMultiMap;
import org.dockbox.hartshorn.util.collections.BiMultiMap;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A simple implementation of {@link TypeParameterList}.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class SimpleTypeParameterList implements TypeParameterList {

    private final List<? extends TypeParameterView> typeParameters;
    private BiMultiMap<TypeParameterView, TypeParameterView> representationMap;

    public SimpleTypeParameterList(List<? extends TypeParameterView> typeParameters) {
        this.typeParameters = typeParameters;
    }

    @Override
    public Option<TypeParameterView> atIndex(int index) {
        if (this.typeParameters.size() <= index) {
            return Option.empty();
        }
        return Option.of(this.typeParameters.get(index));
    }

    @Override
    public int count() {
        return this.typeParameters.size();
    }

    @Override
    public boolean isEmpty() {
        return this.typeParameters.isEmpty();
    }

    @Override
    public List<TypeParameterView> asList() {
        return List.copyOf(this.typeParameters);
    }

    @Override
    public BiMultiMap<TypeParameterView, TypeParameterView> asMap() {
        if (this.representationMap == null) {
            BiMultiMap<TypeParameterView, TypeParameterView> map = new ArrayListHashBiMultiMap<>();
            for (TypeParameterView typeParameter : this.typeParameters) {
                if (typeParameter.isInputParameter()) {
                    map.putAll(typeParameter, typeParameter.represents());
                }
                else {
                    map.put(typeParameter, typeParameter.asInputParameter());
                }
            }
            this.representationMap = map;
        }
        return this.representationMap;
    }

    @Override
    public Stream<TypeParameterView> stream() {
        return this.asList().stream();
    }

    @Override
    public Iterator<TypeParameterView> iterator() {
        return this.asList().iterator();
    }
}
