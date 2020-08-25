package net.jibini.whetstone.impl

import net.jibini.whetstone.Adjacent
import net.jibini.whetstone.AdjacentPersistence
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaGetter
import kotlin.reflect.jvm.javaSetter

object AdjacentProxyFactory
{
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> create(baseClass: KClass<*>, baseProvider: () -> T) = java.lang.reflect.Proxy.newProxyInstance(
        baseClass.java.classLoader, arrayOf(baseClass.java)) {
            _: Any?, method: Method?, args: Array<Any>? ->

        method!!

        val property = when
        {
            method.name.startsWith("get") ->
                baseClass.memberProperties.find { it.javaGetter == method }

            method.name.startsWith("set") ->
                baseClass.memberProperties.find { it is KMutableProperty<*> && it.javaSetter == method }

            else -> null
        }

        val adjacent = property?.findAnnotation<Adjacent>()?.adjacentPersistence?.objectInstance

        if (adjacent != null)
        {
            //TODO
            println("Adjacent is not null $adjacent")
            method.invoke(baseProvider(), *(args ?: arrayOf()))
        } else
            method.invoke(baseProvider(), *(args ?: arrayOf()))
    } as T

    inline fun <reified T : Any> create(noinline baseProvider: () -> T): T = create(T::class, baseProvider)

    inline fun <reified T : Any> create(base: T) = create { base }
}