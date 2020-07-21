package net.jibini.whetstone.document

import net.jibini.whetstone.document.impl.DocumentCacheImpl

interface DocumentCache<T : Document> : DocumentRepository<T>
{
    fun has(_uid: String): Boolean

    val unwritten: List<T>
    val persistent: DocumentRepository<T>?

    companion object
    {
        fun <T : Document> create(persistent: DocumentRepository<T>? = null) = DocumentCacheImpl(persistent)
    }

    fun put(_uid: String, document: T, checkRev: Boolean)
    {
        if (!checkRev || document._rev > retrieve(_uid)._rev)
            put(_uid, document)
    }
}