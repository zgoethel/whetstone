package net.jibini.whetstone.document.persistent

import net.jibini.whetstone.document.persistent.impl.postgresQuery
import org.junit.Test
import kotlin.test.assertTrue

class TestPostgresQueryBuilder
{
    @Test
    fun createQueryForDocument()
    {
        val joinModel = DocumentJoinModel.buildFrom(TestDocument::class)
        val query = joinModel.postgresQuery

        println("-------------")
        println(query)
        println("-------------")

        assertTrue(query.contains("jsonb_agg(distinct WhTestSubDocument.data) as subDocuments"))
        assertTrue(query.contains("left join WhTestSubDocument on (WhTestDocument.data->'subDocuments') @> " +
                "(WhTestSubDocument.data->'_uid')"))

        assertTrue(query.contains("jsonb_agg(distinct WhTestSubSubDocument.data) as subDocuments__subSubDocuments"))
        assertTrue(query.contains("left join WhTestSubSubDocument on (WhTestSubDocument.data->'subSubDocuments') " +
                "@> (WhTestSubSubDocument.data->'_uid')"))
    }
}