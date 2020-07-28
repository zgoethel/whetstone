package net.jibini.whetstone.document.persistent

import net.jibini.whetstone.document.Document

@Table("WhTestDocument")
@Suppress("UNUSED")
class TestDocument(
        override var _uid: String,
        override var _rev: Long,

        @Join
        var subDocuments: List<TestSubDocument>
) : Document

@Table("WhTestSubDocument")
@Suppress("UNUSED")
class TestSubDocument(
        override var _uid: String,
        override var _rev: Long,

        @Join
        var subSubDocuments: List<TestSubSubDocument>
) : Document

@Table("WhTestSubSubDocument")
class TestSubSubDocument(
        override var _uid: String,
        override var _rev: Long
) : Document