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

package test.org.dockbox.hartshorn.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/**
 * A simple JPA compatible user type with an auto-generated {@link #id()}.
 */
@Entity
public class JpaUser {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private int age;

    public JpaUser() {
    }

    public JpaUser(final String name, final int age) {
        this.name = name;
        this.age = age;
    }

    public JpaUser(final long id, final String name, final int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public long id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public int age() {
        return this.age;
    }
}
