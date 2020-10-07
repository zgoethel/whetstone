package net.jibini.wh.docs

import net.jibini.wh.annotation.DatabaseBean
import net.jibini.wh.annotation.FieldName
import net.jibini.wh.annotation.Join

@DatabaseBean(TestDocumentDatabase::class)
interface TestDocument
{
    var regularNamed: String

    @FieldName("OtherwiseNamed")
    var otherwiseNamed: String

    @Join
    var externalDoc: TestDocument
}