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

import org.dockbox.hartshorn.component.processing.AutomaticActivation;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.proxy.processing.MethodProxyContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.Exceptional;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.data.annotations.Deserialise;
import org.dockbox.hartshorn.data.context.DeserialisationContext;
import org.dockbox.hartshorn.data.context.PersistenceAnnotationContext;
import org.dockbox.hartshorn.data.context.SerialisationTarget;
import org.dockbox.hartshorn.data.mapping.ObjectMapper;

import java.io.File;
import java.nio.file.Path;

@AutomaticActivation
public class DeserialisationServicePostProcessor extends AbstractPersistenceServicePostProcessor<Deserialise, DeserialisationContext> {

    @Override
    protected <T, R> MethodInterceptor<T> processAnnotatedPath(final ApplicationContext context, final MethodProxyContext<T> methodContext, final DeserialisationContext serialisationContext) {
        return interceptorContext -> {
            final Path path = serialisationContext.predeterminedPath();
            final ObjectMapper objectMapper = this.mapper(context, serialisationContext);

            final Exceptional<?> result = objectMapper.read(path, serialisationContext.type());
            return this.wrapResult(result, methodContext);
        };
    }

    @Override
    protected <T, R> MethodInterceptor<T> processParameterPath(final ApplicationContext context, final MethodProxyContext<T> methodContext, final DeserialisationContext serialisationContext) {
        return interceptorContext -> {
            final Path path;
            final Object[] args = interceptorContext.args();
            if (args[0] instanceof Path) path = (Path) args[0];
            else if (args[0] instanceof File) path = ((File) args[0]).toPath();
            else throw new IllegalArgumentException("Expected one argument to be a subtype of File or Path");

            final ObjectMapper objectMapper = this.mapper(context, serialisationContext);

            final Exceptional<?> result = objectMapper.read(path, serialisationContext.type());
            return this.wrapResult(result, methodContext);
        };
    }

    @Override
    protected <T, R> MethodInterceptor<T> processString(final ApplicationContext context, final MethodProxyContext<T> methodContext, final DeserialisationContext serialisationContext) {
        return interceptorContext -> {
            final String raw = (String) interceptorContext.args()[0];
            final ObjectMapper objectMapper = this.mapper(context, serialisationContext);

            final Exceptional<?> result = objectMapper.read(raw, serialisationContext.type());
            return this.wrapResult(result, methodContext);
        };
    }

    @Override
    protected Class<DeserialisationContext> contextType() {
        return DeserialisationContext.class;
    }

    private <R> R wrapResult(final Exceptional<?> result, final MethodProxyContext<?> methodContext) {
        if (methodContext.method().returnType().childOf(Exceptional.class)) return (R) result;
        else return (R) result.orNull();
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext processingContext) {
        if (methodContext.method().parameterCount() > 1) return false;
        if (methodContext.method().returnType().isVoid()) return false;

        final Deserialise annotation = methodContext.annotation(Deserialise.class);

        final TypeContext<?> outputType = this.outputType(methodContext);
        if (outputType == null) return false;

        final DeserialisationContext deserialisationContext = new DeserialisationContext(outputType);
        deserialisationContext.fileFormat(annotation.filetype());
        methodContext.add(deserialisationContext);

        if (methodContext.method().parameterCount() == 0) {
            deserialisationContext.target(SerialisationTarget.ANNOTATED_PATH);
            deserialisationContext.predeterminedPath(this.determineAnnotationPath(
                    context,
                    methodContext,
                    new PersistenceAnnotationContext(annotation))
            );
            return true;
        }
        else if (methodContext.method().parameterCount() == 1) {
            final TypeContext<?> parameterType = methodContext.method().parameterTypes().get(0);

            if (parameterType.childOf(String.class)) {
                deserialisationContext.target(SerialisationTarget.STRING);
                return true;
            }
            else if (parameterType.childOf(Path.class) || parameterType.childOf(File.class)) {
                deserialisationContext.target(SerialisationTarget.PARAMETER_PATH);
                return true;
            }
        }
        return false;
    }

    private TypeContext<?> outputType(final MethodProxyContext<?> context) {
        final TypeContext<?> returnType = context.method().returnType();
        if (returnType.childOf(Exceptional.class)) {
            return returnType.typeParameters().get(0);
        }
        else {
            return returnType;
        }
    }

    @Override
    public Class<Deserialise> annotation() {
        return Deserialise.class;
    }
}
