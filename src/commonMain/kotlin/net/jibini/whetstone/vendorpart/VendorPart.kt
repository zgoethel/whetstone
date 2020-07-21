package net.jibini.whetstone.vendorpart

import net.jibini.whetstone.document.Document
import net.jibini.whetstone.document.persistent.Cache
import net.jibini.whetstone.document.persistent.Join
import net.jibini.whetstone.document.persistent.Table
import net.jibini.whetstone.part.Part
import net.jibini.whetstone.part.PartRepository

@Table("WhVendorPart")
class VendorPart(
        override var _uid: String,
        override var _rev: Long,

        @Cache(PartRepository::class)
        @Join
        var parts: Array<Part>
) : Document