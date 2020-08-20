package net.jibini.whetstone

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class Adjacent(
    val adjacentPersistence: KClass<out AdjacentPersistence>
)