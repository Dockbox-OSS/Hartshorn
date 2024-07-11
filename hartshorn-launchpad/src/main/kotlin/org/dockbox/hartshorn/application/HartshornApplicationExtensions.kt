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
package org.dockbox.hartshorn.application

import org.dockbox.hartshorn.launchpad.HartshornApplication.ApplicationBootstrap
import org.dockbox.hartshorn.launchpad.ApplicationContext
import org.dockbox.hartshorn.launchpad.HartshornApplication
import org.dockbox.hartshorn.launchpad.HartshornApplicationConfigurer
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

