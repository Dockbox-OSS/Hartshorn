package org.dockbox.sample.kotlin

import org.dockbox.hartshorn.inject.provider.get
import org.dockbox.hartshorn.launchpad.HartshornApplication

fun main(vararg args: String) {
  HartshornApplication.create(*args).apply {
    get<GreetingAction>().greet()
  }
}