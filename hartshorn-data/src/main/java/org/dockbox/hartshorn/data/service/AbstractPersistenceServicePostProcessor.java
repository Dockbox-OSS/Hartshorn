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

import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.proxy.processing.MethodProxyContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.inject.TypedOwner;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.proxy.processing.ServiceAnnotatedMethodInterceptorPostProcessor;
import org.dockbox.hartshorn.data.FileFormats;
import org.dockbox.hartshorn.data.context.PersistenceAnnotationContext;
import org.dockbox.hartshorn.data.context.SerialisationContext;
import org.dockbox.hartshorn.data.mapping.ObjectMapper;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractPersistenceServicePostProcessor<M extends Annotation, C extends SerializationContext> extends ServiceAnnotatedMethodInterceptorPostProcessor<M> {

    @Override
    public <T, R> MethodInterceptor<T> process(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        final Result<C> serialisationContext = methodContext.first(context, this.contextType());
        if (serialisationContext.absent()) throw new IllegalStateException("Expected additional context to be present");

        final C ctx = serialisationContext.get();
        context.log().debug("Processing persistence path of " + methodContext.method().name() + " with serialisation target " + ctx.target());
        return switch (ctx.target()) {
            case ANNOTATED_PATH -> this.processAnnotatedPath(context, methodContext, ctx);
            case PARAMETER_PATH -> this.processParameterPath(context, methodContext, ctx);
            case STRING -> this.processString(context, methodContext, ctx);
            default -> throw new IllegalArgumentException("Unsupported serialisation target: " + ctx.target());
        };
    }

    protected abstract Class<C> contextType();

    protected abstract <T> MethodInterceptor<T> processAnnotatedPath(ApplicationContext context, MethodProxyContext<T> methodContext, C serializationContext);

    protected abstract <T> MethodInterceptor<T> processParameterPath(ApplicationContext context, MethodProxyContext<T> methodContext, C serializationContext);

    protected abstract <T> MethodInterceptor<T> processString(ApplicationContext context, MethodProxyContext<T> methodContext, C serializationContext);

    protected ObjectMapper mapper(final ApplicationContext context, final C serialisationContext) {
        final ObjectMapper objectMapper = context.get(ObjectMapper.class);
        final FileFormats fileFormat = serialisationContext.fileFormat();
        objectMapper.fileType(fileFormat);
        return objectMapper;
    }

    protected Path determineAnnotationPath(final ApplicationContext context, final MethodProxyContext<?> methodContext, final PersistenceAnnotationContext annotationContext) {
        TypeContext<?> owner = TypeContext.of(annotationContext.file().owner());

        if (owner.isVoid()) {
            final Result<ComponentContainer> container = context.locator().container(methodContext.method().parent());
            if (container.present()) {
                owner = container.get().owner();
            }
        }

        Path root = context.environment().manager().applicationPath();
        if (!owner.isVoid()) {
            final TypedOwner typedOwner = context.meta().lookup(owner);
            root = root.resolve(typedOwner.id());
        }

        if (Files.notExists(root)) {
            try {
                Files.createDirectory(root);
            }
            catch (final IOException e) {
                ExceptionHandler.unchecked(e);
            }
        }

        if (!root.toFile().isDirectory()) ExceptionHandler.unchecked(new ApplicationException("Expected " + root + " to be a directory, but found a file instead"));

        return annotationContext.filetype().asPath(root, annotationContext.file().value());
    }
}
