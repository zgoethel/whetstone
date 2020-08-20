package net.jibini.whetstone.logging

import net.jibini.whetstone.logging.impl.LoggerImpl

interface Logger
{
    fun info(message: String)

    fun error(thrown: Throwable)

    fun error(message: String)

    fun debug(message: String)

    companion object
    {
        inline fun <reified T> create(): Logger = LoggerImpl(T::class)
    }
}