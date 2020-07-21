package net.jibini.whetstone.document.persistent

import net.jibini.whetstone.document.DocumentRepository
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class Cache(
        val documentCache: KClass<out DocumentRepository<*>>
)