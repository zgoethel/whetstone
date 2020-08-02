package net.jibini.whetstone.document

import net.jibini.whetstone.document.persistent.Table
import kotlin.reflect.KClass

interface Document {
    var _uid: String
    var _rev: Long
}

val KClass<out Document>.table: String
    get() {
        for (ann in annotations)
            if (ann is Table)
                return ann.tableName

        throw IllegalStateException("Document interface must be marked with table annotation")
    }