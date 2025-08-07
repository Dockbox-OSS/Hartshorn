package org.dockbox.sample.kotlin

import org.dockbox.hartshorn.launchpad.HartshornApplication

fun main(args: Array<String>) {
    val application = HartshornApplication.create(*args)
    application.get<GreetingAction>().greet()
}