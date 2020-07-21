package net.jibini.whetstone.document.impl

import net.jibini.whetstone.document.Document

class DecoratedDocument<T : Document>(
        var _origin: T,
        var _lastWritten: Long = _origin._rev
) : Document by _origin