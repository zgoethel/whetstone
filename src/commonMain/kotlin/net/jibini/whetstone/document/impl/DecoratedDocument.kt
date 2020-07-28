package net.jibini.whetstone.document.impl

import net.jibini.whetstone.document.Document

class DecoratedDocument<T : Document>(
        var _origin: T,
        var _lastWritten: Long = _origin._rev
) : Document
{
    override var _uid: String
        get() = _origin._uid
        set(value)
        {
            _origin._uid = value
        }

    override var _rev: Long
        get() = _origin._rev
        set(value)
        {
            _origin._rev = value
        }
}