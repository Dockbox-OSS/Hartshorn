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

package test.org.dockbox.hartshorn.config;

public class Address {

    private String street;
    private String city;
    private int number;

    public Address(String city, String street, int number) {
        this.street = street;
        this.city = city;
        this.number = number;
    }

    public Address() {
    }

    public String street() {
        return this.street;
    }

    public String city() {
        return this.city;
    }

    public int number() {
        return this.number;
    }
}
