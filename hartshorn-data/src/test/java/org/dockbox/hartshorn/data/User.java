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

package org.dockbox.hartshorn.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue
    private long id;
    private String name;
    @SuppressWarnings("JpaAttributeTypeInspection") private Address address;

    public User() {
    }

    public User(final String name) {
        this.name = name;
    }

    public long id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public User name(final String name) {
        this.name = name;
        return this;
    }
}
