package net.jibini.whetstone.vendorpart

import net.jibini.whetstone.document.DocumentCache

actual object VendorPartRepository : DocumentCache<VendorPart> by DocumentCache.create()