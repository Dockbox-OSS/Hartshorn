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

import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.services.ComponentContainer;
import org.dockbox.hartshorn.data.DataStorageType;
import org.dockbox.hartshorn.data.annotations.Serialise;
import org.dockbox.hartshorn.data.context.PersistenceAnnotationContext;
import org.dockbox.hartshorn.data.context.SerialisationContext;
import org.dockbox.hartshorn.data.context.SerialisationTarget;
import org.dockbox.hartshorn.data.mapping.ObjectMapper;
import org.dockbox.hartshorn.core.proxy.ProxyFunction;
import org.dockbox.hartshorn.core.context.MethodProxyContext;

import java.io.File;
import java.nio.file.Path;

@AutomaticActivation
public class SerialisationServicePostProcessor extends AbstractPersistenceServicePostProcessor<Serialise, SerialisationContext> {

    @Override
    protected <T, R> ProxyFunction<T, R> processAnnotatedPath(final ApplicationContext context, final MethodProxyContext<T> methodContext, final SerialisationContext serialisationContext) {
        return (instance, args, proxyContext) -> {
            final Path target = serialisationContext.predeterminedPath();
            final Object content = args[0];
            final ObjectMapper objectMapper = this.mapper(context, serialisationContext);
            final Exceptional<Boolean> result = objectMapper.write(target, content);
            return this.wrapBooleanResult(result, methodContext);
        };
    }

    @Override
    protected <T, R> ProxyFunction<T, R> processParameterPath(final ApplicationContext context, final MethodProxyContext<T> methodContext, final SerialisationContext serialisationContext) {
        return (instance, args, proxyContext) -> {
            Path target = null;
            Object content = null;
            for (final Object arg : args) {
                if (arg instanceof Path) target = (Path) arg;
                else if (arg instanceof File) target = ((File) arg).toPath();
                else content = arg;
            }

            if (target == null || content == null) throw new IllegalArgumentException("Expected one argument to be a subtype of File or Path, expected one argument to be a content type");

            final ObjectMapper objectMapper = this.mapper(context, serialisationContext);
            final Exceptional<Boolean> result = objectMapper.write(target, content);
            return this.wrapBooleanResult(result, methodContext);
        };
    }

    @Override
    protected <T, R> ProxyFunction<T, R> processString(final ApplicationContext context, final MethodProxyContext<T> methodContext, final SerialisationContext serialisationContext) {
        return (instance, args, proxyContext) -> {
            final Object content = args[0];
            final ObjectMapper objectMapper = this.mapper(context, serialisationContext);

            final Exceptional<String> result = objectMapper.write(content);
            if (methodContext.method().returnType().childOf(String.class)) {
                return (R) result.orNull();
            }
            else {
                return (R) result;
            }
        };
    }

    @Override
    protected Class<SerialisationContext> contextType() {
        return SerialisationContext.class;
    }

    private <R> R wrapBooleanResult(final Exceptional<Boolean> result, final MethodProxyContext<?> methodContext) {
        if (methodContext.method().returnType().childOf(Boolean.class))
            return (R) result.or(false);
        else return (R) result;
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        if (methodContext.method().parameterCount() < 1) return false;

        final Serialise annotation = methodContext.annotation(Serialise.class);
        if (annotation.filetype().type() != DataStorageType.RAW) return false;

        boolean hasPath = false;
        for (final TypeContext<?> parameter : methodContext.method().parameterTypes()) {
            if (parameter.childOf(Path.class) || parameter.childOf(File.class)) {
                hasPath = true;
                break;
            }
        }

        final SerialisationContext serialisationContext = new SerialisationContext();
        serialisationContext.fileFormat(annotation.filetype());
        methodContext.add(serialisationContext);

        if (hasPath) {
            serialisationContext.target(SerialisationTarget.PARAMETER_PATH);
            return methodContext.method().parameterCount() == 2;
        }
        else {
            final TypeContext<?> owner = this.owner(context, TypeContext.of(annotation.path().owner()), methodContext);
            if (!owner.isVoid()) {
                serialisationContext.target(SerialisationTarget.ANNOTATED_PATH);
                serialisationContext.predeterminedPath(this.determineAnnotationPath(
                        context,
                        methodContext,
                        new PersistenceAnnotationContext(annotation))
                );
                return methodContext.method().parameterCount() == 1;
            }
            else {
                serialisationContext.target(SerialisationTarget.STRING);
                return this.stringTargetPreconditions(methodContext);
            }
        }
    }

    private TypeContext<?> owner(final ApplicationContext context, final TypeContext<?> annotationOwner, final MethodProxyContext<?> methodContext) {
        if (!annotationOwner.isVoid()) return annotationOwner;
        return context.locator().container(methodContext.method().parent()).map(ComponentContainer::owner).orNull();
    }

    private boolean stringTargetPreconditions(final MethodProxyContext<?> context) {
        final TypeContext<?> returnType = context.method().returnType();

        if (returnType.childOf(String.class)) return true;

        else if (returnType.childOf(Exceptional.class)) {
            final TypeContext<?> type = returnType.typeParameters().get(0);

            if (!type.isVoid()) {
                return type.childOf(String.class);
            }
        }

        return false;
    }

    @Override
    public Class<Serialise> annotation() {
        return Serialise.class;
    }
}
