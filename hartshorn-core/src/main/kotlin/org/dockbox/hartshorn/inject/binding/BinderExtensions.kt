package org.dockbox.hartshorn.inject.binding

import kotlin.reflect.KClass

fun <T : Any> Binder.bind(type: KClass<T>): BindingFunction<T> = this.bind(type.java)

inline fun <reified T : Any> Binder.bind(): BindingFunction<T> = this.bind(T::class.java)
