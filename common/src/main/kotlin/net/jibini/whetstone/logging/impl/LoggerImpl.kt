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
        thrown.printStackTrace()
    }

    override fun error(message: String)
    {
        System.err.println("${owner.simpleName} ERROR - $message")
    }

    override fun debug(message: String)
    {
        println("${owner.simpleName} DEBUG - $message")
    }
}