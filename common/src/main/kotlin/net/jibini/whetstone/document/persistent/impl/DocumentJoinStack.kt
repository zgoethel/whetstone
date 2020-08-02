package net.jibini.whetstone.document.persistent.impl

class DocumentJoinStack(val mutableStack: MutableList<DocumentJoin> = mutableListOf()) {
    fun navigateFlat(nextFlatValue: DocumentJoin) {
        if (mutableStack.isEmpty())
            mutableStack += nextFlatValue
        else
            when (nextFlatValue.from) {
                mutableStack.last().to -> mutableStack += nextFlatValue

                mutableStack.last().from -> mutableStack[mutableStack.lastIndex] = nextFlatValue

                else -> {
                    while (mutableStack.last().from != nextFlatValue.from)
                        mutableStack.removeAt(mutableStack.lastIndex)
                    mutableStack[mutableStack.lastIndex] = nextFlatValue
                }
            }
    }

    val aggregate: String
        get() {
            val name = StringBuilder()

            for (level in mutableStack) {
                if (name.isNotEmpty())
                    name.append("__")
                name.append(level.asAggregate)
            }

            return name.toString()
        }
}