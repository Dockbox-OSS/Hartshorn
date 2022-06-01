/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.data.service;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.data.annotations.Deserialize;
import org.dockbox.hartshorn.data.context.DeserializationContext;
import org.dockbox.hartshorn.data.context.PersistenceAnnotationContext;
import org.dockbox.hartshorn.data.context.SerializationTarget;
import org.dockbox.hartshorn.data.mapping.ObjectMapper;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.processing.MethodProxyContext;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public class DeserializationServicePostProcessor extends AbstractPersistenceServicePostProcessor<Deserialize, DeserializationContext> {

    @Override
    protected <T> MethodInterceptor<T> processAnnotatedPath(final ApplicationContext context, final MethodProxyContext<T> methodContext, final DeserializationContext deserializationContext) {
        return interceptorContext -> {
            final Path path = deserializationContext.predeterminedPath();
            final ObjectMapper objectMapper = this.mapper(context, deserializationContext);

            final Result<?> result = objectMapper.read(path, deserializationContext.type());
            return this.wrapResult(result, methodContext);
        };
    }

    @Override
    protected <T> MethodInterceptor<T> processParameterPath(final ApplicationContext context, final MethodProxyContext<T> methodContext, final DeserializationContext deserializationContext) {
        return interceptorContext -> {
            final Path path;
            final Object[] args = interceptorContext.args();
            if (args[0] instanceof Path) path = (Path) args[0];
            else if (args[0] instanceof File) path = ((File) args[0]).toPath();
            else throw new IllegalArgumentException("Expected one argument to be a subtype of File or Path");

            final ObjectMapper objectMapper = this.mapper(context, deserializationContext);

            final Result<?> result = objectMapper.read(path, deserializationContext.type());
            return this.wrapResult(result, methodContext);
        };
    }

    @Override
    protected <T> MethodInterceptor<T> processString(final ApplicationContext context, final MethodProxyContext<T> methodContext, final DeserializationContext deserializationContext) {
        return interceptorContext -> {
            final String raw = (String) interceptorContext.args()[0];
            final ObjectMapper objectMapper = this.mapper(context, deserializationContext);

            final Result<?> result = objectMapper.read(raw, deserializationContext.type());
            return this.wrapResult(result, methodContext);
        };
    }

    @Override
    protected Class<DeserializationContext> contextType() {
        return DeserializationContext.class;
    }

    private <R> R wrapResult(final Result<?> result, final MethodProxyContext<?> methodContext) {
        if (methodContext.method().returnType().childOf(Result.class)) return (R) result;
        else return (R) result.orNull();
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        if (methodContext.method().parameterCount() > 1) return false;
        if (methodContext.method().returnType().isVoid()) return false;

        final Deserialize annotation = methodContext.annotation(Deserialize.class);

        final TypeContext<?> outputType = this.outputType(methodContext);
        if (outputType == null) return false;

        final DeserializationContext deserializationContext = new DeserializationContext(outputType);
        deserializationContext.fileFormat(annotation.filetype());
        methodContext.add(deserializationContext);

        if (methodContext.method().parameterCount() == 0) {
            deserializationContext.target(SerializationTarget.ANNOTATED_PATH);
            deserializationContext.predeterminedPath(this.determineAnnotationPath(
                    context,
                    methodContext,
                    new PersistenceAnnotationContext(annotation))
            );
            return true;
        }
        else if (methodContext.method().parameterCount() == 1) {
            final TypeContext<?> parameterType = methodContext.method().parameterTypes().get(0);

            if (parameterType.childOf(String.class)) {
                deserializationContext.target(SerializationTarget.STRING);
                return true;
            }
            else if (parameterType.childOf(Path.class) || parameterType.childOf(File.class)) {
                deserializationContext.target(SerializationTarget.PARAMETER_PATH);
                return true;
            }
        }
        return false;
    }

    private TypeContext<?> outputType(final MethodProxyContext<?> context) {
        final TypeContext<?> returnType = context.method().returnType();
        if (returnType.childOf(Result.class) || returnType.childOf(Optional.class)) {
            return returnType.typeParameters().get(0);
        }
        else {
            return returnType;
        }
    }

    @Override
    public Class<Deserialize> annotation() {
        return Deserialize.class;
    }
}
