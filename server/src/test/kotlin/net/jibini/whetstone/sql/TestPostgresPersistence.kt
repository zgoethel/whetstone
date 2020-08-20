package net.jibini.whetstone.sql

import net.jibini.whetstone.Adjacent
import net.jibini.whetstone.Document
import net.jibini.whetstone.Repository
import org.junit.Assert
import org.junit.Test
import java.util.*

interface SimpleDocument : Document
{
    var number: Int

    companion object
    {
        fun create(
            _uid: String,
            _rev: Int,

            number: Int
        ): SimpleDocument = SimpleDocumentImpl(_uid, _rev, number)
    }
}

class SimpleDocumentImpl(
    override val _uid: String = "",
    override var _rev: Int = 0,

    override var number: Int = 0
) : SimpleDocument

val newSimplePersistence: PostgresPersistence<SimpleDocument>
    get() = PostgresPersistence.create<SimpleDocument, SimpleDocumentImpl>(
        "WhTestSimpleDocument",
        "jdbc:postgresql://localhost/postgres",
        "whetstone",
        "password",
        true
    )

object SimpleDocumentPersistence : PostgresPersistence<SimpleDocument>
        by newSimplePersistence

interface ComplexDocument : Document
{
    @Adjacent(SimpleDocumentPersistence::class)
    var document: SimpleDocument?

    @Adjacent(SimpleDocumentPersistence::class)
    var documentList: MutableList<SimpleDocument>

    companion object
    {
        fun create(
            _uid: String,
            _rev: Int,

            document: SimpleDocument?,

            documentList: MutableList<SimpleDocument> = mutableListOf()
        ): ComplexDocument = ComplexDocumentImpl(_uid, _rev, document, documentList)
    }
}

class ComplexDocumentImpl(
    override val _uid: String = "",
    override var _rev: Int = 0,

    override var document: SimpleDocument? = null,

    override var documentList: MutableList<SimpleDocument> = mutableListOf()
) : ComplexDocument

object ComplexDocumentRepository : Repository<ComplexDocument> by Repository.create(
    PostgresPersistence.create<ComplexDocument, ComplexDocumentImpl>(
        "WhTestComplexDocument",
        "jdbc:postgresql://localhost/postgres",
        "whetstone",
        "password",
        true
    )
)

class TestPostgresPersistence
{
    @Test
    fun loadSimpleValues()
    {
        val number = Random().nextInt()

        SimpleDocumentPersistence.connection
            .createStatement()
            .execute("""
                create table if not exists WhTestSimpleDocument(_row serial primary key, data jsonb);
                insert into WhTestSimpleDocument(data) values ('{"_uid": "simple", "_rev": 0, "number": $number}');
            """.trimIndent()
            )

        val repo = Repository.create(newSimplePersistence)

        Assert.assertEquals(number, repo.retrieve("simple" )!!.number)
    }

    @Test
    fun readWriteSimpleValue()
    {
        val number = Random().nextInt()

        SimpleDocumentPersistence.writeThrough(SimpleDocument.create(
            "written",
            0,

            number
        ))

        val repo = Repository.create(newSimplePersistence)

        Assert.assertEquals(number, SimpleDocumentPersistence.tank.last { it._uid == "written" }.number)
        Assert.assertEquals(number, repo.retrieve("written" )!!.number)
    }

    @Test
    fun writeComplexAdjacentValue()
    {
        ComplexDocumentRepository.put(ComplexDocument.create(
            "complex",
            0,
            SimpleDocument.create(
                "simple",
                0,
                0
            )
        ))

        Assert.assertEquals(0, SimpleDocumentPersistence.tank.last { it._uid == "simple" }.number)
    }

    @Test
    fun readWriteComplexAdjacentValue()
    {
        ComplexDocumentRepository.put(ComplexDocument.create(
            "complex",
            0,
            SimpleDocument.create(
                "simple",
                0,
                0
            )
        ))

        val complexRead = ComplexDocumentRepository.retrieve("complex")

        Assert.assertEquals(0, complexRead!!.document!!.number)

        val number = Random().nextInt()
        complexRead.document!!.number = number

        Assert.assertEquals(number, SimpleDocumentPersistence.tank.last { it._uid == "simple" }.number)
    }

    @Test
    fun reviseExistingComplexEntry()
    {
        val value = ComplexDocumentRepository.retrieve("existing-complex") ?:
            ComplexDocument.create(
                "existing-complex",
                0,
                SimpleDocument.create(
                    "simple",
                    0,
                    0
                )
            )

        val currentRevA = value._rev
        val currentRevB = value.document!!._rev
        ComplexDocumentRepository.put(value)

        val complexRead = ComplexDocumentRepository.retrieve("existing-complex")

        // Assert revisions were incremented upon write
        Assert.assertEquals(currentRevA + 1, complexRead!!._rev)
        Assert.assertEquals(currentRevB + 1, complexRead.document!!._rev)

        // Assert that the proxy links between repositories have not been broken
        complexRead.document!!.number++
        Assert.assertEquals(complexRead.document!!.number, SimpleDocumentPersistence.tank.last
            { it._uid == "simple" }.number)
    }

    @Test
    fun readWriteComplexAdjacentValueList()
    {
        val numberA = Random().nextInt()
        val numberB = Random().nextInt()

        ComplexDocumentRepository.put(ComplexDocument.create(
            "with-list",
            0,

            document = SimpleDocumentPersistence.tank.findLast { it._uid == "simple" },

            documentList = mutableListOf(
                SimpleDocument.create(
                    "simpleA",
                    0,
                    numberA
                ),

                SimpleDocument.create(
                    "simpleB",
                    0,
                    numberB
                )
            )
        ))

        val read = ComplexDocumentRepository.retrieve("with-list")!!

        Assert.assertEquals(numberA, read.documentList[0].number)
        Assert.assertEquals(numberB, read.documentList[1].number)

        Assert.assertEquals(numberA, SimpleDocumentPersistence.tank.last { it._uid == "simpleA" }.number)
        Assert.assertEquals(numberB, SimpleDocumentPersistence.tank.last { it._uid == "simpleB" }.number)
    }
}