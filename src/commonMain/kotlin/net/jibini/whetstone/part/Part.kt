package net.jibini.whetstone.part

import net.jibini.whetstone.document.Document
import net.jibini.whetstone.document.persistent.Table

@Table("WhPart")
class Part(
        override var _uid: String,
        override var _rev: Long
) : Document