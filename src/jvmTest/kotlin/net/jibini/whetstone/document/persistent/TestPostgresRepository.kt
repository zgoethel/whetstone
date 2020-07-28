package net.jibini.whetstone.document.persistent

import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class TestPostgresRepository
{
    @Before
    fun ensureRepositoriesInMemory()
    {
        TestDocumentRepository.toString()
        TestSubDocumentRepository.toString()
        TestSubSubDocumentRepository.toString()
    }

    @Test
    fun generateAndSubmitJoinQuery()
    {
        TestDocumentRepository.retrieve("TEST_ELEMENT")
    }
}