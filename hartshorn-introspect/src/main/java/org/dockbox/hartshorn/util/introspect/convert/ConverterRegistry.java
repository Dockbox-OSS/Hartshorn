/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.util.introspect.convert;

public interface ConverterRegistry {

    <I, O> void addConverter(Converter<I, O>  converter);

    <I, O> void addConverter(Class<I> sourceType, Class<O> targetType, Converter<I, O> converter);

    void addConverter(GenericConverter converter);

    <I, O> void addConverterFactory(ConverterFactory<I, O>  converterFactory);

    <I, O> void addConverterFactory(Class<I> sourceType, ConverterFactory<I, O> converterFactory);

    <O> void addDefaultValueProvider(DefaultValueProvider<O> provider);

    <O> void addDefaultValueProvider(Class<O> targetType, DefaultValueProvider<O> provider);

    <O> void addDefaultValueProviderFactory(DefaultValueProviderFactory<O> factory);

}