package net.jibini.whetstone.logic.impl

import net.jibini.whetstone.Document
import net.jibini.whetstone.logic.Selection

class Not<T : Document>(
    private val child: Selection<T>
) : Selection<T>
{
    override fun invoke(document: T) = !child.invoke(document)
}