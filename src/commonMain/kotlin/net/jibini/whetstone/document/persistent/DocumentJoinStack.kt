package net.jibini.whetstone.document.persistent

import net.jibini.whetstone.document.table

class DocumentJoinStack(val mutableStack: MutableList<DocumentJoin> = mutableListOf())
{
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
                    while (mutableStack.last().from != nextFlatValue.from)
                        mutableStack.removeAt(mutableStack.lastIndex)
                    mutableStack[mutableStack.lastIndex] = nextFlatValue
                }
            }
    }

    val aggregate: String
        get()
        {
            val name = StringBuilder()

            for (level in mutableStack)
            {
                if (name.isNotEmpty())
                    name.append("__")
                name.append(level.asAggregate)
            }

            return name.toString()
        }
}