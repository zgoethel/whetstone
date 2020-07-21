package net.jibini.whetstone.document

interface DocumentRepository<T : Document>
{
    fun retrieve(_uid: String): T

    fun put(_uid: String, document: T)
}