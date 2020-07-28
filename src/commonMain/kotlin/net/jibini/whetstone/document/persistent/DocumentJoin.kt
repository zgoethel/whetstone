package net.jibini.whetstone.document.persistent

import net.jibini.whetstone.document.Document
import kotlin.reflect.KClass

class DocumentJoin(
    val from: KClass<out Document>,
    val to: KClass<out Document>,

    val asAggregate: String
)