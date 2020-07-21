package net.jibini.whetstone.part

import net.jibini.whetstone.document.DocumentCache

actual object PartRepository : DocumentCache<Part> by DocumentCache.create()