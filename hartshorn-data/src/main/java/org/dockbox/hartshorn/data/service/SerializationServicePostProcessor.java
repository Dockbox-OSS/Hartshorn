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
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.data.DataStorageType;
import org.dockbox.hartshorn.data.annotations.Serialize;
import org.dockbox.hartshorn.data.context.PersistenceAnnotationContext;
import org.dockbox.hartshorn.data.context.SerializationContext;
import org.dockbox.hartshorn.data.context.SerializationTarget;
import org.dockbox.hartshorn.data.mapping.ObjectMapper;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.processing.MethodProxyContext;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.io.File;
import java.nio.file.Path;

public class SerializationServicePostProcessor extends AbstractPersistenceServicePostProcessor<Serialize, SerializationContext> {

    @Override
    protected <T> MethodInterceptor<T> processAnnotatedPath(final ApplicationContext context, final MethodProxyContext<T> methodContext, final SerializationContext serializationContext) {
        return interceptorContext -> {
            final Path target = serializationContext.predeterminedPath();
            final Object content = interceptorContext.args()[0];
            final ObjectMapper objectMapper = this.mapper(context, serializationContext);
            final Result<Boolean> result = objectMapper.write(target, content);
            return this.wrapBooleanResult(result, methodContext);
        };
    }

    @Override
    protected <T> MethodInterceptor<T> processParameterPath(final ApplicationContext context, final MethodProxyContext<T> methodContext, final SerializationContext serializationContext) {
        return interceptorContext -> {
            Path target = null;
            Object content = null;
            for (final Object arg : interceptorContext.args()) {
                if (arg instanceof Path) target = (Path) arg;
                else if (arg instanceof File) target = ((File) arg).toPath();
                else content = arg;
            }

            if (target == null || content == null) throw new IllegalArgumentException("Expected one argument to be a subtype of File or Path, expected one argument to be a content type");

            final ObjectMapper objectMapper = this.mapper(context, serializationContext);
            final Result<Boolean> result = objectMapper.write(target, content);
            return this.wrapBooleanResult(result, methodContext);
        };
    }

    @Override
    protected <T> MethodInterceptor<T> processString(final ApplicationContext context, final MethodProxyContext<T> methodContext, final SerializationContext serializationContext) {
        return interceptorContext -> {
            final Object content = interceptorContext.args()[0];
            final ObjectMapper objectMapper = this.mapper(context, serializationContext);

            final Result<String> result = objectMapper.write(content);
            if (methodContext.method().returnType().childOf(String.class)) {
                return result.orNull();
            }
            else {
                return result;
            }
        };
    }

    @Override
    protected Class<SerializationContext> contextType() {
        return SerializationContext.class;
    }

    private Object wrapBooleanResult(final Result<Boolean> result, final MethodProxyContext<?> methodContext) {
        if (methodContext.method().returnType().childOf(Boolean.class))
            return result.or(false);
        else return result;
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        if (methodContext.method().parameterCount() < 1) return false;

        final Serialize annotation = methodContext.annotation(Serialize.class);
        if (annotation.filetype().type() != DataStorageType.RAW) return false;

        boolean hasPath = false;
        for (final TypeContext<?> parameter : methodContext.method().parameterTypes()) {
            if (parameter.childOf(Path.class) || parameter.childOf(File.class)) {
                hasPath = true;
                break;
            }
        }

        final SerializationContext serializationContext = new SerializationContext();
        serializationContext.fileFormat(annotation.filetype());
        methodContext.add(serializationContext);

        if (hasPath) {
            serializationContext.target(SerializationTarget.PARAMETER_PATH);
            return methodContext.method().parameterCount() == 2;
        }
        else {
            final TypeContext<?> owner = this.owner(context, TypeContext.of(annotation.path().owner()), methodContext);
            if (!owner.isVoid()) {
                serializationContext.target(SerializationTarget.ANNOTATED_PATH);
                serializationContext.predeterminedPath(this.determineAnnotationPath(
                        context,
                        methodContext,
                        new PersistenceAnnotationContext(annotation))
                );
                return methodContext.method().parameterCount() == 1;
            }
            else {
                serializationContext.target(SerializationTarget.STRING);
                return this.stringTargetPreconditions(methodContext);
            }
        }
    }

    private TypeContext<?> owner(final ApplicationContext context, final TypeContext<?> annotationOwner, final MethodProxyContext<?> methodContext) {
        if (!annotationOwner.isVoid()) return annotationOwner;
        return context.get(ComponentLocator.class).container(methodContext.method().parent()).map(ComponentContainer::owner).orNull();
    }

    private boolean stringTargetPreconditions(final MethodProxyContext<?> context) {
        final TypeContext<?> returnType = context.method().returnType();

        if (returnType.childOf(String.class)) return true;

        else if (returnType.childOf(Result.class)) {
            final TypeContext<?> type = returnType.typeParameters().get(0);

            if (!type.isVoid()) {
                return type.childOf(String.class);
            }
        }

        return false;
    }

    @Override
    public Class<Serialize> annotation() {
        return Serialize.class;
    }
}
