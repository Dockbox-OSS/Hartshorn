package org.dockbox.hartshorn.application

import org.dockbox.hartshorn.application.HartshornApplication.ApplicationBootstrap
import org.dockbox.hartshorn.application.context.ApplicationContext
import kotlin.reflect.KClass

fun createApplication(mailClass: KClass<*>, vararg arguments: String): ApplicationBootstrapKt {
  return createApplication { HartshornApplication.createApplication(mailClass.java, *arguments) }
}

inline fun <reified T : Any> createApplication(vararg arguments: String): ApplicationBootstrapKt {
  return createApplication(T::class, *arguments)
}

private fun createApplication(bootstrapProvider: () -> ApplicationBootstrap): ApplicationBootstrapKt {
  return ApplicationBootstrapKt { customizer ->
    bootstrapProvider().initialize { configurer: HartshornApplicationConfigurer -> customizer(configurer) }
  }
}

fun interface ApplicationBootstrapKt {
  fun initialize(): ApplicationContext = this.initialize {}

  fun initialize(customizer: HartshornApplicationConfigurer.() -> Unit): ApplicationContext
}

