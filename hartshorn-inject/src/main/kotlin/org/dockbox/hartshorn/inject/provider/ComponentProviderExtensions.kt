package org.dockbox.hartshorn.inject.provider

import kotlin.reflect.KClass

inline fun <reified T : Any> ComponentProvider.get(): T = this.get(T::class.java)