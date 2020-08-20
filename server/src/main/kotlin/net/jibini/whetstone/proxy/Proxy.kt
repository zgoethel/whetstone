package net.jibini.whetstone.proxy

import java.lang.reflect.Method
import kotlin.reflect.KClass

object Proxy
{
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> create(baseClass: KClass<*>, baseProvider: () -> T) = java.lang.reflect.Proxy.newProxyInstance(
                baseClass.java.classLoader, arrayOf(baseClass.java)) {
                    _: Any?, method: Method?, args: Array<Any>? ->

                method!!.invoke(baseProvider(), *(args ?: arrayOf()))
            } as T

    inline fun <reified T : Any> create(noinline baseProvider: () -> T): T = create(T::class, baseProvider)

    inline fun <reified T : Any> create(base: T) = create { base }
}