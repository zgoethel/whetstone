package net.jibini.whetstone.document.persistent

import net.jibini.whetstone.document.Document
import net.jibini.whetstone.document.DocumentRepository
import net.jibini.whetstone.document.table
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

class PostgresRepository<T : Document>(
    private val serverAddress: String,
    private val joinModel: DocumentJoinModel,

    username: String,
    password: String,

    ssl: Boolean = true
) : DocumentRepository<T>
{
    private var connection: Connection
    private val props = Properties()

    init
    {
        Class.forName("org.postgresql.Driver")

        props["user"] = username
        props["password"] = password
        props["ssl"] = ssl

        connection = DriverManager.getConnection(serverAddress, props)

        val statement = connection.createStatement()
        val createTable = String.format(SQL_TABLE_CREATE, joinModel.base.table, SQL_TABLE_DEFAULT_SCHEMA)

        statement.execute(createTable)
    }

    override fun retrieve(_uid: String): T
    {
        if (connection.isClosed)
            connection = DriverManager.getConnection(serverAddress, props)

        val statement = connection.createStatement()
        val results = statement.executeQuery(joinModel.postgresQuery)

        while (results.next())
        {

        }

        TODO("ASSEMBLE JSON AND PARSE")
    }

    override fun put(_uid: String, document: T)
    {
        TODO("STRIP JOINS AND ENCODE TO JSON")
    }

    companion object
    {
        const val SQL_TABLE_CREATE = "create table if not exists %s(%s);"
        const val SQL_TABLE_DEFAULT_SCHEMA = "_row serial primary key, data jsonb"
    }
}