package net.jibini.whetstone.document.impl

import net.jibini.whetstone.document.Document
import net.jibini.whetstone.document.DocumentCache
import net.jibini.whetstone.document.DocumentRepository

class DocumentCacheImpl<T : Document>(
        override val persistent: DocumentRepository<T>?
) : DocumentCache<T>
{
    private val internalCache = mutableMapOf<String, DecoratedDocument<T>>()

    override fun retrieve(_uid: String): T
    {
        if (!has(_uid))
        {
            if (persistent == null)
                throw IllegalStateException("Attempting to retrieve an un-cached document '$_uid' with no persistent" +
                        " repository")
            else
                internalCache[_uid] = DecoratedDocument(persistent.retrieve(_uid))
        }

        return internalCache[_uid]!!._origin
    }

    override fun put(_uid: String, document: T)
    {
        if (!has(_uid))
            internalCache[_uid] = DecoratedDocument(document, -1L)
        else
            internalCache[_uid]!!._origin = document
    }

    override fun has(_uid: String) = internalCache.containsKey(_uid)

    override val unwritten: List<T>
        get()
        {
            val unwritten = mutableListOf<T>()

            for (cached in internalCache.values)
            {
                if (cached._rev > cached._lastWritten)
                    unwritten += cached._origin
            }

            return unwritten
        }
}