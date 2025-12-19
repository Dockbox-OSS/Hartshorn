package org.dockbox.sample.kotlin

import org.dockbox.hartshorn.inject.annotations.PropertyValue
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton
import org.dockbox.hartshorn.launchpad.annotations.LoggerMeta
import org.slf4j.Logger

@Configuration
class GreetingConfiguration {

  @Singleton(lazy = true)
  fun greetingAction(
    @PropertyValue(
      name = "greetings.hello",
      defaultValue = "Hello there, {}"
    ) helloGreetingTemplate: String?,
    @LoggerMeta(name = "Greeting implementation") logger: Logger
  ): GreetingAction {
    return GreetingAction { logger.info(helloGreetingTemplate, "World") }
  }
}
