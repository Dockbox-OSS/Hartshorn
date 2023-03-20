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

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;

@Entity(name = "queryUsers")
@NamedQuery(name = "UserWithNamedQuery.findWaldo", query = "SELECT u FROM queryUsers u WHERE u.name = 'Waldo'")
public class UserWithNamedQuery {

    @Id
    @GeneratedValue
    private long id;
    private String name;

    public UserWithNamedQuery() {
    }

    public UserWithNamedQuery(final String name) {
        this.name = name;
    }

    public long id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public UserWithNamedQuery name(final String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final UserWithNamedQuery that)) return false;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }
}
