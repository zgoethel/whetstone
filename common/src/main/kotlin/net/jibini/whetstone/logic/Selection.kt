package net.jibini.whetstone.logic

import net.jibini.whetstone.Document
import net.jibini.whetstone.logic.impl.And
import net.jibini.whetstone.logic.impl.If
import net.jibini.whetstone.logic.impl.Not
import net.jibini.whetstone.logic.impl.Or
import java.lang.IllegalStateException

interface Selection<T : Document> : (T) -> Boolean
{
    companion object
    {
        fun <T : Document> create(logicalGate: LogicalGate, children: List<Selection<T>>) = when (logicalGate)
        {
            LogicalGate.AND -> And(children)

            LogicalGate.OR -> Or(children)

            LogicalGate.NOT -> {
                if (children.size == 1)
                    Not(children[0])
                else
                    throw IllegalStateException("'Not' selection requires one and only one child selection")
            }

            LogicalGate.IF -> {
                if (children.size == 1)
                    If(children[0])
                else
                    throw IllegalStateException("'If' selection requires one and only one child selection")
            }
        }

        fun <T : Document> decodeRecursively(encodedSelection: EncodedSelection): Selection<T>
        {
            val children = mutableListOf<Selection<T>>()
            for (child in encodedSelection.children)
                children += decodeRecursively(child)

            return create(encodedSelection.logicalGate, children)
        }
    }
}