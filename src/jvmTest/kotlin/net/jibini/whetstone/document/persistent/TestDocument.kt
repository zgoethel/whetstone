package net.jibini.whetstone.document.persistent

import net.jibini.whetstone.document.Document
import net.jibini.whetstone.document.DocumentRepository

object TestDocumentRepository : DocumentRepository<TestDocument> by PostgresRepository.create(
    "jdbc:postgresql://localhost/postgres",

    "whetstone", "password"
)

@Table("WhTestDocument")
@Suppress("UNUSED")
class TestDocument(
        override var _uid: String,
        override var _rev: Long,

        @Cache(TestSubDocumentRepository::class)
        @Join
        var subDocuments: List<TestSubDocument>
) : Document


object TestSubDocumentRepository : DocumentRepository<TestSubDocument> by PostgresRepository.create(
    "jdbc:postgresql://localhost/postgres",

    "whetstone", "password"
)

@Table("WhTestSubDocument")
@Suppress("UNUSED")
class TestSubDocument(
        override var _uid: String,
        override var _rev: Long,

        @Cache(TestSubSubDocumentRepository::class)
        @Join
        var subSubDocuments: List<TestSubSubDocument>
) : Document


object TestSubSubDocumentRepository : DocumentRepository<TestSubSubDocument> by PostgresRepository.create(
    "jdbc:postgresql://localhost/postgres",

    "whetstone", "password"
)

@Table("WhTestSubSubDocument")
class TestSubSubDocument(
        override var _uid: String,
        override var _rev: Long
) : Document