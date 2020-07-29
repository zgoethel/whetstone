package net.jibini.whetstone.document.persistent

import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import java.sql.DriverManager
import kotlin.test.assertEquals

class TestPostgresRepository
{
    private val connection = DriverManager.getConnection("jdbc:postgresql://localhost/postgres",
        "whetstone", "password")!!

    @Before
    fun ensureRepositoriesInMemory()
    {
        TestDocumentRepository
        TestSubDocumentRepository
        TestSubSubDocumentRepository
    }

    @Test
    fun stripJoinedData()
    {
        TestDocumentRepository.put(TestDocument("TEST_DOC", 0, listOf(TestSubDocument("TEST_S_DOC",
            0, listOf(TestSubSubDocument("TEST_S_S_DOC", 0))))))

        val statement = connection.createStatement()
        val results = statement.executeQuery("select * from WhTestDocument order by _row desc limit 1;")
        results.next()

        val json = JSONObject(results.getString("data"))
        assertEquals("TEST_DOC", json.getString("_uid"))
        assertEquals("TEST_S_DOC", json.getJSONArray("subDocuments").getString(0))
    }

    @Test
    fun generateAndSubmitJoinQuery()
    {
        TODO("WRITE TEST DOCUMENTS TO JOIN")

        val read = TestDocumentRepository.retrieve("TEST_DOC")
    }
}