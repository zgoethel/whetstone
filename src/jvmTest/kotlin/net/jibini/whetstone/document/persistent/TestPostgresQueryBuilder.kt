package net.jibini.whetstone.document.persistent

import net.jibini.whetstone.vendorpart.VendorPart
import org.junit.Test

class TestPostgresQueryBuilder
{
    @Test fun createQueryForDocument()
    {
        println("-------------")
        println(PostgresQueryBuilder(VendorPart::class).joinAll().built)
        println("-------------")
    }
}