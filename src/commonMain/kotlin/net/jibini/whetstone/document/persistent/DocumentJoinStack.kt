package net.jibini.whetstone.document.persistent

import net.jibini.whetstone.document.table

class DocumentJoinStack()
{
    val mutableStack = mutableListOf<DocumentJoin>()

    fun navigateFlat(nextFlatValue: DocumentJoin)
    {
        if (mutableStack.isEmpty())
            mutableStack += nextFlatValue
        else
            when (nextFlatValue.from)
            {
                mutableStack.last().to -> {
                    // Step further into join stack
                    mutableStack += nextFlatValue
                }

                mutableStack.last().from -> {
                    // Stay at same stack depth
                    mutableStack[mutableStack.lastIndex] = nextFlatValue
                }

                else -> {
                    // Pop the join stack by one level
                    mutableStack.removeAt(mutableStack.lastIndex)
                    mutableStack[mutableStack.lastIndex] = nextFlatValue
                }
            }
    }

    val aggregatePrefix: String
        get()
        {
            val prefix = StringBuilder()
            for (level in mutableStack)
                prefix.append("${level.from.table}__")

            return prefix.toString()
        }
}