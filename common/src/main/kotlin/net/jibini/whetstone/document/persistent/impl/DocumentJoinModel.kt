package net.jibini.whetstone.document.persistent.impl

import net.jibini.whetstone.document.Document
import net.jibini.whetstone.document.persistent.Join
import kotlin.reflect.KClass

class DocumentJoinModel(
    val base: KClass<out Document>,
    val joins: List<DocumentJoin> = mutableListOf()
)
{
    private val mutableJoins = joins as MutableList

    fun join(join: DocumentJoin)
    {
        mutableJoins += join
    }

    fun merge(subModel: DocumentJoinModel)
    {
        mutableJoins.addAll(subModel.joins)
    }

    companion object
    {
        fun buildFrom(base: KClass<out Document>): DocumentJoinModel
        {
            val created = DocumentJoinModel(base)

            for (member in base.members)
                for (ann in member.annotations)
                    if (ann is Join)
                    {
                        @Suppress("UNCHECKED_CAST")
                        val to = member.returnType.arguments[0].type?.classifier as KClass<out Document>
                        created.join(DocumentJoin(base, to, member.name))

                        val subModel = buildFrom(to)
                        created.merge(subModel)
                    }

            return created
        }
    }
}