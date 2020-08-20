package net.jibini.whetstone

interface Persistence<T : Document>
{
    val tank: List<T>

    fun writeThrough(value: T)
}