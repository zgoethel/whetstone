package net.jibini.whetstone.logging.impl

import net.jibini.whetstone.logging.Logger
import kotlin.reflect.KClass

class LoggerImpl(private val owner: KClass<*>) : Logger
{
    override fun info(message: String)
    {
        println("${owner.simpleName} INFO - $message")
    }

    override fun error(thrown: Throwable)
    {
        kotlin.error(thrown)
    }

    override fun error(message: String)
    {
        kotlin.error("${owner.simpleName} INFO - $message")
    }

    override fun debug(message: String)
    {
        println("${owner.simpleName} DEBUG - $message")
    }
}