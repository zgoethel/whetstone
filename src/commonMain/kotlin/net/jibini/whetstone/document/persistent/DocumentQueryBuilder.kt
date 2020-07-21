package net.jibini.whetstone.document.persistent

import net.jibini.whetstone.document.Document
import kotlin.reflect.KClass

interface DocumentQueryBuilder<T : Document, V>
{
    fun join(to: KClass<out Document>, on: String) : DocumentQueryBuilder<T, V>

    val built : V
    val document : KClass<out Document>

    @Suppress("UNCHECKED_CAST")
    fun joinAll() : DocumentQueryBuilder<T, V>
    {
        for (member in document.members)
        {
            for (ann in member.annotations)
                if (ann is Join)
                    // Joins by first type parameter of return type (should be Array<out Document>)
                    join(member.returnType.arguments[0].type?.classifier as KClass<out Document>, member.name)
        }

        return this
    }
}