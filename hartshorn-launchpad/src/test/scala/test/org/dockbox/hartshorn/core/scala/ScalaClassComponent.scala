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
package test.org.dockbox.hartshorn.core.scala

import org.dockbox.hartshorn.inject.annotations.Inject
import org.dockbox.hartshorn.application.context.ApplicationContext
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment
import org.dockbox.hartshorn.component.Component

@Component
class ScalaClassComponent @Inject() (private val environment: ApplicationEnvironment) {

  @Inject
  private var applicationContext: ApplicationContext = _

  def getApplicationContext: ApplicationContext = {
    this.applicationContext
  }

  def getApplicationManager: ApplicationEnvironment = {
    this.environment
  }
}
