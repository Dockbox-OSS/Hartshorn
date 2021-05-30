/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.persistence.service;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.domain.OwnerLookup;
import org.dockbox.selene.api.domain.TypedOwner;
import org.dockbox.selene.di.annotations.Service;
import org.dockbox.selene.di.context.ApplicationContext;
import org.dockbox.selene.persistence.FileManager;
import org.dockbox.selene.persistence.annotations.Serialise;
import org.dockbox.selene.persistence.annotations.UsePersistence;
import org.dockbox.selene.proxy.exception.ProxyMethodBindingException;
import org.dockbox.selene.proxy.handle.ProxyFunction;
import org.dockbox.selene.proxy.service.MethodProxyContext;
import org.dockbox.selene.proxy.service.ServiceAnnotatedMethodModifier;
import org.dockbox.selene.util.Reflect;

import java.io.File;
import java.nio.file.Path;

public class SerialisationServiceModifier extends ServiceAnnotatedMethodModifier<Serialise, UsePersistence> {

    @Override
    public Class<UsePersistence> activator() {
        return UsePersistence.class;
    }

    @Override
    public <T, R> ProxyFunction<T, R> process(ApplicationContext context, MethodProxyContext<T> methodContext) {
        // Single element array for effectively final element (due to functional interface return)
        final Path[] fileTarget = { null };
        if (methodContext.getMethod().getParameterCount() == 1) {
            fileTarget[0] = this.findOwnerPath(context, methodContext);
            if (fileTarget[0] == null) throw new ProxyMethodBindingException(methodContext);
        }

        return ((instance, args, ctx) -> {
            // Will only be null if there are 2 parameters, preconditions will fail if there is
            // no valid target otherwise.
            if (fileTarget[0] == null) {
                fileTarget[0] = this.findParameterPath(args);
            }

            final Object content = this.findContent(args);
            final Exceptional<Boolean> result = context.get(FileManager.class).write(fileTarget[0], content);

            if (Reflect.isNotVoid(methodContext.getReturnType()))
                //noinspection unchecked
                return (R) result;
            else return null;
        });
    }

    private Object findContent(Object[] args) {
        if (args.length == 1) return args[0];
        else {
            for (Object arg : args) {
                if (!(arg instanceof Path || arg instanceof File)) return arg;
            }
        }
        // Should never happen when preconditions have been used.
        return null;
    }

    private Path findParameterPath(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Path) return ((Path) arg);
            else if (arg instanceof File) return ((File) arg).toPath();
        }
        // Should never happen when preconditions have been used.
        return null;
    }

    private Path findOwnerPath(ApplicationContext context, MethodProxyContext<?> methodContext) {
        final Serialise annotation = methodContext.getAnnotation(Serialise.class);
        Class<?> owner = annotation.owner();

        // TODO GuusLieben: Explain
        if (Reflect.isNotVoid(owner)) {
            final Class<?> declaring = methodContext.getMethod().getDeclaringClass();
            final Service service = declaring.getAnnotation(Service.class);
            owner = service.owner();
        }
        final TypedOwner lookup = context.get(OwnerLookup.class).lookup(owner);
        final String file = annotation.file();

        final FileManager fileManager = context.get(FileManager.class);

        Path target;
        if ("".equals(file)) target = fileManager.getDataFile(lookup);
        else target = fileManager.getDataFile(lookup, file);

        return target;
    }

    @Override
    public <T> boolean preconditions(MethodProxyContext<T> context) {
        // One parameter indicates the type instance to serialise, a optional second parameter indicates
        // the path target.
        final int parameterCount = context.getMethod().getParameterCount();
        if (parameterCount < 1 || parameterCount > 2) return false;

        // Serialise methods may return a Exceptional which mirrors the result from FileManager#write
        boolean isVoid = !Reflect.isNotVoid(context.getReturnType());
        boolean validReturn = isVoid || Exceptional.class.equals(context.getReturnType());
        if (!validReturn) return false;

        // If only the type instance is indicated a specific file target needs to be given
        if (parameterCount == 1) {
            final Serialise annotation = context.getAnnotation(Serialise.class);
            boolean hasSpecificOwner = Reflect.isNotVoid(annotation.owner());
            boolean hasSpecificTarget = !"".equals(annotation.file());
            return hasSpecificOwner && hasSpecificTarget;

        } else {
            // If no specific file target is provided by the annotation, it should be present as a method
            // parameter in a (subclass of) Path or File type.
            boolean hasPathArgument = false;
            for (Class<?> parameter : context.getMethod().getParameterTypes()) {
                if (Reflect.assignableFrom(File.class, parameter)
                        || Reflect.assignableFrom(Path.class, parameter)
                ) {
                    hasPathArgument = true;
                    break;
                }
            }
            return hasPathArgument;
        }
    }

    @Override
    public Class<Serialise> annotation() {
        return Serialise.class;
    }
}
