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
import org.junit.jupiter.api.assertThrows

class JakartaCompatibilityTests {

  private fun createApplicationContext(withJakarta: Boolean): ApplicationContext =
    createApplication<JakartaCompatibilityTests>().initialize {
      if (withJakarta) withJakartaAnnotations()
      includeBasePackages(false)
    }

  @Test
  fun testJakartaFieldInjectSupportedIfEnabled() {
    val context = this.createApplicationContext(withJakarta = true)
    context.bind<String>().singleton("Hello, World!")

    val service = context.get(FieldJakartaService::class.java)
    assertNotNull(service)

    assertEquals("Hello, World!", service.messageAsInject)
    assertEquals("Hello, World!", service.messageAsResource)
  }

  @Test
  fun testJakartaFieldInjectFailsIfDisabled() {
    val context = this.createApplicationContext(withJakarta = false)
    context.bind<String>().singleton("Hello, World!")

    val service = context.get(FieldJakartaService::class.java)
    assertNotNull(service)

    assertNull(service.messageAsInject)
    assertNull(service.messageAsResource)
  }

  @Test
  fun testJakartaConstructorInjectSupportedIfEnabled() {
    val context = this.createApplicationContext(withJakarta = true)
    context.bind<String>().singleton("Hello, World!")

    val service = context.get(InjectConstructorJakartaService::class.java)
    assertNotNull(service)

    assertEquals("Hello, World!", service.messageAsInject)
  }

  @Test
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
  fun testJakartaMethodInjectSupportedIfEnabled() {
    val context = this.createApplicationContext(withJakarta = true)
    context.bind<String>().singleton("Hello, World!")

    val service = context.get(MethodJakartaService::class.java)
    assertNotNull(service)

    assertEquals("Hello, World!", service.messageAsInject)
    assertEquals("Hello, World!", service.messageAsResource)
  }

  @Test
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
