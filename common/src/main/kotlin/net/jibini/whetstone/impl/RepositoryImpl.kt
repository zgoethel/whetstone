package net.jibini.whetstone.impl

import net.jibini.whetstone.Document
import net.jibini.whetstone.Persistence
import net.jibini.whetstone.Repository
import net.jibini.whetstone.logging.Logger
import net.jibini.whetstone.logic.Selection
import kotlin.reflect.KClass

class RepositoryImpl<T : Document>(
    documentClass: KClass<T>,

    private val persistence: Persistence<T>
) : Repository<T>, Iterable<T> by persistence.tank
{
    private val logger = Logger.create<RepositoryImpl<*>>()

    init
    {
        logger.info("Initialized '${documentClass.simpleName}' persistence and query engine " +
                "via ${persistence::class.simpleName}")
    }

    override fun retrieve(_uid: String): T? = findLast { it._uid == _uid }

    override fun select(selection: Selection<T>) = filter { selection.invoke(it) }

    override fun put(value: T)
    {
        value._rev++

        persistence.writeThrough(value)
    }
}