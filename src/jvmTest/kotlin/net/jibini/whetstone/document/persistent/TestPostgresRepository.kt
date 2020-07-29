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
        val statement = connection.createStatement()
        statement.execute("""
insert into WhTestSubDocument(data) values ('{"_uid": "TEST_S_DOC", "_rev": 0, "subSubDocuments": ["TEST_S_S_DOC"]}');
insert into WhTestSubSubDocument(data) values ('{"_uid": "TEST_S_S_DOC", "_rev": 0}');
        """.trimIndent().trim())

        val read = TestDocumentRepository.retrieve("TEST_DOC")

        assertEquals(read._uid, "TEST_DOC")
        assertEquals(read.subDocuments[0]._uid, "TEST_S_DOC")
        assertEquals(read.subDocuments[0].subSubDocuments[0]._uid, "TEST_S_S_DOC")
    }
}