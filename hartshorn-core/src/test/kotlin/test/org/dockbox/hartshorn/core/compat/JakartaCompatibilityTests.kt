/*
 * Copyright 2019-2024 the original author or authors.
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
package test.org.dockbox.hartshorn.core.compat

import org.dockbox.hartshorn.application.context.ApplicationContext
import org.dockbox.hartshorn.application.createApplication
import org.dockbox.hartshorn.inject.ComponentInitializationException
import org.dockbox.hartshorn.inject.MissingInjectConstructorException
import org.dockbox.hartshorn.inject.binding.bind
import org.dockbox.hartshorn.util.TypeUtils
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows

class JakartaCompatibilityTests {

  private fun createApplicationContext(withJakarta: Boolean): ApplicationContext =
    createApplication<JakartaCompatibilityTests>().initialize {
      if (withJakarta) withJakartaAnnotations()
      includeBasePackages(false)
    }

  @Test
  @DisplayName("Jakarta annotations are supported for field injection, if enabled")
  fun testJakartaFieldInjectSupportedIfEnabled() {
    val context = this.createApplicationContext(withJakarta = true)
    context.bind<String>().singleton("Hello, World!")

    val service = context.get(FieldJakartaService::class.java)
    assertNotNull(service)

    assertEquals("Hello, World!", service.messageAsInject)
    assertEquals("Hello, World!", service.messageAsResource)
  }

  @Test
  @DisplayName("Jakarta annotations are not supported for field injection, if disabled")
  fun testJakartaFieldInjectFailsIfDisabled() {
    val context = this.createApplicationContext(withJakarta = false)
    context.bind<String>().singleton("Hello, World!")

    val service = context.get(FieldJakartaService::class.java)
    assertNotNull(service)

    assertNull(service.messageAsInject)
    assertNull(service.messageAsResource)
  }

  @Test
  @DisplayName("Jakarta annotations are supported for constructor injection, if enabled")
  fun testJakartaConstructorInjectSupportedIfEnabled() {
    val context = this.createApplicationContext(withJakarta = true)
    context.bind<String>().singleton("Hello, World!")

    val service = context.get(InjectConstructorJakartaService::class.java)
    assertNotNull(service)

    assertEquals("Hello, World!", service.messageAsInject)
  }

  @Test
  @DisplayName("Jakarta annotations are not supported for constructor injection, if disabled")
  fun testJakartaConstructorInjectFailsIfDisabled() {
    val context = this.createApplicationContext(withJakarta = false)
    context.bind<String>().singleton("Hello, World!")

    val initializationException = assertThrows<ComponentInitializationException> {
      context.get(InjectConstructorJakartaService::class.java)
    }
    val rootCause = TypeUtils.getRootCause(initializationException)
    assertInstanceOf(MissingInjectConstructorException::class.java, rootCause)
  }

  @Test
  @DisplayName("Jakarta annotations are supported for method injection, if enabled")
  fun testJakartaMethodInjectSupportedIfEnabled() {
    val context = this.createApplicationContext(withJakarta = true)
    context.bind<String>().singleton("Hello, World!")

    val service = context.get(MethodJakartaService::class.java)
    assertNotNull(service)

    assertEquals("Hello, World!", service.messageAsInject)
    assertEquals("Hello, World!", service.messageAsResource)
  }

  @Test
  @DisplayName("Jakarta annotations are not supported for method injection, if disabled")
  fun testJakartaMethodInjectFailsIfDisabled() {
    val context = this.createApplicationContext(withJakarta = false)
    context.bind<String>().singleton("Hello, World!")

    val service = context.get(MethodJakartaService::class.java)
    assertNotNull(service)

    assertNull(service.messageAsInject)
    assertNull(service.messageAsResource)
  }

  class FieldJakartaService {

    @jakarta.inject.Inject
    internal var messageAsInject: String? = null

    @jakarta.annotation.Resource
    internal var messageAsResource: String? = null
  }

  class InjectConstructorJakartaService {
    val messageAsInject: String

    @jakarta.inject.Inject
    constructor(messageAsInject: String) {
      this.messageAsInject = messageAsInject
    }

    constructor(markerToVerify: Any) {
      this.messageAsInject = ""
      fail<Void>("This constructor should never be called")
    }
  }

  class MethodJakartaService {

    internal var messageAsInject: String? = null
    internal var messageAsResource: String? = null

    @jakarta.inject.Inject
    fun setMessageAsInject(messageAsInject: String) {
      this.messageAsInject = messageAsInject
    }

    @jakarta.annotation.Resource
    fun setMessageAsResource(messageAsResource: String) {
      this.messageAsResource = messageAsResource
    }
  }
}
