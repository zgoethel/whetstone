package net.jibini.whetstone

import net.jibini.whetstone.impl.RepositoryImpl
import net.jibini.whetstone.logic.Selection

interface Repository<T : Document> : Iterable<T>
{
    fun retrieve(_uid: String): T?

    fun select(selection: Selection<T>): List<T>

    fun put(value: T)

    companion object
    {
        inline fun <reified T : Document> create(persistence: Persistence<T>): Repository<T> = RepositoryImpl(
            T::class, persistence)
    }
}