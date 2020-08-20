package net.jibini.whetstone.logic.impl

import net.jibini.whetstone.Document
import net.jibini.whetstone.logic.Selection

class And<T : Document>(
    private val children: List<Selection<T>>
) : Selection<T>
{
    override fun invoke(document: T) = children.all { it.invoke(document) }
}