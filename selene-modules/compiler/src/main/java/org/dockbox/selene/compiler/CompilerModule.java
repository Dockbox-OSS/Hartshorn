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

package org.dockbox.selene.compiler;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.module.annotations.Module;

import java.util.Map;

@Module
public class CompilerModule {

    public Exceptional<Class<?>> compile(String source) {
        return Exceptional.of(() -> {
            String className = this.extractClassName(source);
            String packageName = this.extractPackage(source);

            Compiler compiler = new Compiler();
            Map<String, byte[]> result = compiler.compile(className + ".java", source);
            return compiler.loadClass(packageName + '.' + className, result);
        });
    }

    private String extractClassName(String source) {
        int class_ = source.indexOf("class ");
        if (class_ == -1) throw new IllegalArgumentException("Missing class definition");
        String sub = source.substring(class_ + 6);
        return sub.substring(0, sub.indexOf(' '));
    }

    private String extractPackage(String source) {
        int package_ = source.indexOf("package ");
        if (package_ == -1) throw new IllegalArgumentException("Missing package definition");
        String sub = source.substring(package_ + 8);
        return sub.substring(0, sub.indexOf(';'));
    }

}
