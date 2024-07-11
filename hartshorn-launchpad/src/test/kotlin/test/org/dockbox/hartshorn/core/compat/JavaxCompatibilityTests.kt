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

import org.dockbox.hartshorn.launchpad.ApplicationContext
import org.dockbox.hartshorn.application.createApplication
import org.dockbox.hartshorn.inject.graph.support.ComponentInitializationException
import org.dockbox.hartshorn.inject.provider.MissingInjectConstructorException
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

class JavaxCompatibilityTests {

  private fun createApplicationContext(withJavax: Boolean): ApplicationContext =
    createApplication<JavaxCompatibilityTests>().initialize {
      if (withJavax) withJavaxAnnotations()
      includeBasePackages(false)
    }

  @Test
  @DisplayName("Javax annotations are supported for field injection, if enabled")
  fun testJavaxFieldInjectSupportedIfEnabled() {
    val context = this.createApplicationContext(withJavax = true)
    context.bind<String>().singleton("Hello, World!")

    val service = context.get(FieldJavaxService::class.java)
    assertNotNull(service)

    assertEquals("Hello, World!", service.messageAsInject)
    assertEquals("Hello, World!", service.messageAsResource)
  }

  @Test
  @DisplayName("Javax annotations are not supported for field injection, if disabled")
  fun testJavaxFieldInjectFailsIfDisabled() {
    val context = this.createApplicationContext(withJavax = false)
    context.bind<String>().singleton("Hello, World!")

    val service = context.get(FieldJavaxService::class.java)
    assertNotNull(service)

    assertNull(service.messageAsInject)
    assertNull(service.messageAsResource)
  }

  @Test
  @DisplayName("Javax annotations are supported for constructor injection, if enabled")
  fun testJavaxConstructorInjectSupportedIfEnabled() {
    val context = this.createApplicationContext(withJavax = true)
    context.bind<String>().singleton("Hello, World!")

    val service = context.get(InjectConstructorJavaxService::class.java)
    assertNotNull(service)

    assertEquals("Hello, World!", service.messageAsInject)
  }

  @Test
  @DisplayName("Javax annotations are not supported for constructor injection, if disabled")
  fun testJavaxConstructorInjectFailsIfDisabled() {
    val context = this.createApplicationContext(withJavax = false)
    context.bind<String>().singleton("Hello, World!")

    val initializationException = assertThrows<ComponentInitializationException> {
      context.get(InjectConstructorJavaxService::class.java)
    }
    val rootCause = TypeUtils.getRootCause(initializationException)
    assertInstanceOf(MissingInjectConstructorException::class.java, rootCause)
  }

  @Test
  @DisplayName("Javax annotations are supported for method injection, if enabled")
  fun testJavaxMethodInjectSupportedIfEnabled() {
    val context = this.createApplicationContext(withJavax = true)
    context.bind<String>().singleton("Hello, World!")

    val service = context.get(MethodJavaxService::class.java)
    assertNotNull(service)

    assertEquals("Hello, World!", service.messageAsInject)
    assertEquals("Hello, World!", service.messageAsResource)
  }

  @Test
  @DisplayName("Javax annotations are not supported for method injection, if disabled")
  fun testJavaxMethodInjectFailsIfDisabled() {
    val context = this.createApplicationContext(withJavax = false)
    context.bind<String>().singleton("Hello, World!")

    val service = context.get(MethodJavaxService::class.java)
    assertNotNull(service)

    assertNull(service.messageAsInject)
    assertNull(service.messageAsResource)
  }

  class FieldJavaxService {

    @javax.inject.Inject
    internal var messageAsInject: String? = null

    @javax.annotation.Resource
    internal var messageAsResource: String? = null
  }

  class InjectConstructorJavaxService {
    val messageAsInject: String

    @javax.inject.Inject
    constructor(messageAsInject: String) {
      this.messageAsInject = messageAsInject
    }

    constructor(markerToVerify: Any) {
      this.messageAsInject = ""
      fail<Void>("This constructor should never be called")
    }
  }

  class MethodJavaxService {

    internal var messageAsInject: String? = null
    internal var messageAsResource: String? = null

    @javax.inject.Inject
    fun setMessageAsInject(messageAsInject: String) {
      this.messageAsInject = messageAsInject
    }

    @javax.annotation.Resource
    fun setMessageAsResource(messageAsResource: String) {
      this.messageAsResource = messageAsResource
    }
  }
}
