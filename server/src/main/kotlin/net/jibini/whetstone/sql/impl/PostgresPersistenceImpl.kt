package net.jibini.whetstone.sql.impl

import net.jibini.whetstone.Document
import net.jibini.whetstone.logging.Logger
import net.jibini.whetstone.sql.PostgresPersistence
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

class PostgresPersistenceImpl<T : Document>(
    private val table: String,

    private val serverAddress: String,
    username: String,
    password: String,
    ssl: Boolean,

    private val parse: (json: String) -> T,
    private val encode: (document: T) -> String
) : PostgresPersistence<T>
{
    private val logger = Logger.create<PostgresPersistence<*>>()

    override val tank: List<T>
        get()
        {
            return _tank
        }

    override val connection: Connection
        get()
        {
            return _connection
        }

    private lateinit var _tank: MutableList<T>
    private lateinit var _connection: Connection

    private val properties = Properties()

    init
    {
        Class.forName("org.postgresql.Driver")

        properties["user"] = username
        properties["password"] = password
        properties["ssl"] = ssl

        logger.debug("Establishing database connection to '$serverAddress' . . .")
        _connection = DriverManager.getConnection(serverAddress, properties)

        logger.debug("Ensuring persistence table '$table' exists . . .")
        _connection
            .createStatement()
            .execute(String.format(SQL_QUERY_CREATE_TABLE, table))

        logger.debug("Performing one-time cache of table '$table' . . .")
        val results = _connection
            .createStatement()
            .executeQuery(String.format(SQL_QUERY_SELECT_ALL, table))

        _tank = mutableListOf()

        TODO("PARSE/LINK ALL MASS-LOADED VALUES")
    }

    override fun writeThrough(value: T)
    {
        if (!_connection.isValid(4))
        {
            logger.debug("Connection to '$serverAddress' is dead or invalid, re-connecting . . .")
            _connection = DriverManager.getConnection(serverAddress, properties)
        }

        TODO("WRITE THROUGH DATA TO DATABASE AND ADJACENT PERSISTENCE ENGINES")
    }

    companion object
    {
        private const val SQL_QUERY_CREATE_TABLE = "create table if not exists %s(_row serial primary key, data jsonb);"

        private const val SQL_QUERY_SELECT_ALL = "select * from %s;"

        private const val SQL_QUERY_PUT_VALUE = "insert into %s(data) values ('%s');"
    }
}