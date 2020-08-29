package net.jibini.whetstone.sql.impl

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import net.jibini.whetstone.Document
import net.jibini.whetstone.impl.AbstractAdjacentPersistence
import net.jibini.whetstone.impl.AdjacentProxyFactory
import net.jibini.whetstone.logging.Logger
import net.jibini.whetstone.sql.PostgresPersistence
import java.lang.IllegalStateException
import java.sql.Connection
import java.sql.DriverManager
import java.util.*
import kotlin.reflect.KClass

class PostgresPersistenceImpl<T : Document>(
    documentClass: KClass<T>,

    private val table: String,
    private val serverAddress: String,

    username: String,
    password: String,
    ssl: Boolean,

    parse: (json: String) -> T,
    encode: (document: Document) -> JsonObject
) : AbstractAdjacentPersistence<T>(documentClass, parse, encode), PostgresPersistence<T>
{
    private val logger = Logger.create<PostgresPersistence<*>>()

    override val tank: Iterable<T>
        get()
        {
            return _tank
        }

    override val connection: Connection
        get()
        {
            return _connection
        }

    private var _tank: MutableList<T>
    private var _connection: Connection

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

        //TODO CREATE PROXY TO JSON AT OBJECT LEVEL, NOT OBJECT MEMBER LEVEL
        logger.debug("Filling out the tank's adjacent proxy links . . .")
        _tank = mutableListOf()

        while (results.next())
            try
            {
                val data = Parser
                    .default()
                    .parse(StringBuilder(results.getString("data"))) as JsonObject

                val f =  fillOut(data)
                val filled = AdjacentProxyFactory.create(documentClass) { f }
                _tank.add(filled)
            } catch (ex: IllegalStateException)
            {
                logger.error("Skipping over malformed database entry ${results.getString("data")}")
                logger.error(ex)
            }
    }

    override fun writeThrough(value: T)
    {
        if (!_connection.isValid(4))
        {
            logger.error("Connection to '$serverAddress' is dead or invalid, re-connecting . . .")
            _connection = DriverManager.getConnection(serverAddress, properties)
        }

        value._rev++

        val strippedString = stripAndDistribute(value)
        _tank.add(fillOut(strippedString))

        _connection
            .createStatement()
            .execute(String.format(SQL_QUERY_PUT_VALUE, table, strippedString.toJsonString()
                .replace("'", "''")))
    }

    companion object
    {
        private const val SQL_QUERY_CREATE_TABLE = "create table if not exists %s(_row serial primary key, data jsonb);"

        private const val SQL_QUERY_PUT_VALUE = "insert into %s(data) values ('%s');"

        private const val SQL_QUERY_SELECT_ALL = "select distinct on (data->'_uid') data from %s " +
                "order by data->'_uid' asc, _row desc"
    }
}