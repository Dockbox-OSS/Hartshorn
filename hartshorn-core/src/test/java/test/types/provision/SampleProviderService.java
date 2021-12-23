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

package test.types.provision;

import org.dockbox.hartshorn.core.annotations.inject.Provider;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;

import javax.inject.Named;
import javax.inject.Singleton;

import test.types.SampleField;

@Service
public class SampleProviderService {

    @Provider
    public ProvidedInterface get() {
        return () -> "Provision";
    }

    @Provider("named")
    public ProvidedInterface named() {
        return () -> "NamedProvision";
    }

    @Provider("field")
    public ProvidedInterface withField(final SampleField field) {
        return () -> "FieldProvision";
    }

    @Provider("namedField")
    public ProvidedInterface withNamedField(@Named("named") final SampleField field) {
        return () -> "NamedFieldProvision";
    }

    @Singleton
    @Provider("singleton")
    public ProvidedInterface singleton() {
        return () -> "SingletonProvision";
    }
}
