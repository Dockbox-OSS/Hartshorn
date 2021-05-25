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

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

class MemoryOutputJavaFileObject extends SimpleJavaFileObject {

    private final String name;
    private final MemoryJavaFileManager memoryJavaFileManager;

    public MemoryOutputJavaFileObject(MemoryJavaFileManager memoryJavaFileManager, String name) {
        super(URI.create("string:///" + name), Kind.CLASS);
        this.memoryJavaFileManager = memoryJavaFileManager;
        this.name = name;
    }

    @Override
    public OutputStream openOutputStream() {
        return new FilterOutputStream(new ByteArrayOutputStream()) {
            @Override
            public void close() throws IOException {
                this.out.close();
                ByteArrayOutputStream bos = (ByteArrayOutputStream) this.out;
                MemoryOutputJavaFileObject.this.memoryJavaFileManager.classBytes.put(MemoryOutputJavaFileObject.this.name, bos.toByteArray());
            }
        };
    }

}
