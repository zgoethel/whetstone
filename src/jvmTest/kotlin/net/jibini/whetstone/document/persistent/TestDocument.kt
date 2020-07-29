package net.jibini.whetstone.document.persistent

import net.jibini.whetstone.document.Document
import net.jibini.whetstone.document.DocumentRepository
import net.jibini.whetstone.document.persistent.impl.PostgresRepository

object TestDocumentRepository : DocumentRepository<TestDocument> by PostgresRepository(
    "jdbc:postgresql://localhost/postgres",
    DocumentJoinModel.buildFrom(TestDocument::class),

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


object TestSubDocumentRepository : DocumentRepository<TestSubDocument> by PostgresRepository(
    "jdbc:postgresql://localhost/postgres",
    DocumentJoinModel.buildFrom(TestSubDocument::class),

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


object TestSubSubDocumentRepository : DocumentRepository<TestSubSubDocument> by PostgresRepository(
    "jdbc:postgresql://localhost/postgres",
    DocumentJoinModel.buildFrom(TestSubSubDocument::class),

    "whetstone", "password"
)

@Table("WhTestSubSubDocument")
class TestSubSubDocument(
        override var _uid: String,
        override var _rev: Long
) : Document