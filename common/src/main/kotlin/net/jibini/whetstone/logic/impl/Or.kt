package net.jibini.whetstone.logic.impl

import net.jibini.whetstone.Document
import net.jibini.whetstone.logic.Selection

class Or<T : Document>(
    private val children: List<Selection<T>>
) : Selection<T>
{
    override fun invoke(document: T) = children.any { it.invoke(document) }
}