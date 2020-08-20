package net.jibini.whetstone

interface Persistence<T : Document>
{
    val tank: Iterable<T>

    fun writeThrough(value: T)
}