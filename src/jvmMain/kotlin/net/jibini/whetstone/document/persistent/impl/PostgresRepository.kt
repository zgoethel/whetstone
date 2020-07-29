package net.jibini.whetstone.document.persistent.impl

import net.jibini.whetstone.document.Document
import net.jibini.whetstone.document.DocumentRepository
import net.jibini.whetstone.document.persistent.DocumentJoinModel
import net.jibini.whetstone.document.persistent.DocumentJoinStack
import net.jibini.whetstone.document.table
import org.json.JSONArray
import org.json.JSONObject
import java.lang.IllegalStateException
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

//TODO MOVE TO IMPL CLASS, ADD INTERFACE
//TODO FACTORY METHOD WITH REIFIED T
class PostgresRepository<T : Document>(
    private val serverAddress: String,
    private val joinModel: DocumentJoinModel,

    username: String,
    password: String,

    ssl: Boolean = true,

    private val parse: (json: String) -> T,
    private val encode: (document: T) -> String
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
        statement.execute(
            _sqlTableCreate(
                joinModel.base.table,
                SQL_TABLE_DEFAULT_SCHEMA
            )
        )
    }

    private fun recursivelyPlace(parent: JSONObject, stack: DocumentJoinStack, uidToJSON: Map<String, JSONObject>)
    {
        if (stack.mutableStack.size > 1)
        {
            val newParents = parent.getJSONArray(stack.mutableStack.first().asAggregate)
            val shortenedStack = DocumentJoinStack(stack.mutableStack.subList(1, stack.mutableStack.size))

            if (!newParents.isNull(0))
                for (i in 0 until newParents.length())
                    recursivelyPlace(newParents.getJSONObject(i), shortenedStack, uidToJSON)
        } else
        {
            val finalArray = parent.getJSONArray(stack.mutableStack.last().asAggregate)

            for (i in 0 until finalArray.length())
            {
                val uid = finalArray.getString(i)
                finalArray.put(i, uidToJSON[uid])
            }
        }
    }

    override fun retrieve(_uid: String): T
    {
        if (connection.isClosed)
            connection = DriverManager.getConnection(serverAddress, props)

        val statement = connection.createStatement()
        val results = statement.executeQuery(joinModel.postgresQuery)

        while (results.next())
        {
            val stack = DocumentJoinStack()
            val json = JSONObject(results.getString("data"))

            for (join in joinModel.joins)
            {
                stack.navigateFlat(join)

                val columnString = results.getString(stack.aggregate)
                val columnJSON = JSONArray(columnString)

                val uidToJSON = mutableMapOf<String, JSONObject>()

                if (!columnJSON.isNull(0))
                    for (i in 0 until columnJSON.length())
                    {
                        val entry = columnJSON.getJSONObject(i)
                        uidToJSON[entry.getString("_uid")] = entry
                    }

                recursivelyPlace(json, stack, uidToJSON)
            }

            return parse(json.toString())
        }

        throw IllegalStateException("Requested document could not be found")
    }

    override fun put(document: T)
    {
        val encoded = encode(document)
        val json = JSONObject(encoded)

        for (join in joinModel.joins)
            if (join.from == joinModel.base)
            {
                val uidReplacement = mutableListOf<String>()
                val fullArray = json.getJSONArray(join.asAggregate)

                for (i in 0 until fullArray.length())
                    uidReplacement += fullArray.getJSONObject(i).getString("_uid")

                json.put(join.asAggregate, uidReplacement)
            }

        val statement = connection.createStatement()
        statement.execute(
            _sqlRowInsert(
                joinModel.base.table, json.toString()
                    .replace("'", "\\'")
            )
        )
    }

    companion object
    {
        /*
         * SQL TEMPLATES
         */

        // Table name, schema
        const val SQL_TABLE_CREATE = "create table if not exists %s(%s);"
        fun _sqlTableCreate(tableName: String, schema: String) = String.format(SQL_TABLE_CREATE, tableName, schema)

        const val SQL_TABLE_DEFAULT_SCHEMA = "_row serial primary key, data jsonb"

        // Table name, JSON string
        const val SQL_ROW_INSERT = "insert into %s(data) values ('%s')"
        fun _sqlRowInsert(tableName: String, json: String) = String.format(SQL_ROW_INSERT, tableName, json)

        // Table name, additional selects (comma delimited), left joins (separate with space)
        const val SQL_ROW_SELECT = "select %1\$s._row, %1\$s.data%2\$s from %1\$s %3\$s group by %1\$s._row, %1\$s.data"
        fun _sqlRowSelect(tableName: String, selects: String, joins: String) =
            String.format(SQL_ROW_SELECT, tableName, selects, joins)

        // Joined table name, aggregation name
        const val SQL_JSONB_AGG = "jsonb_agg(distinct %s.data) as %s"
        fun _sqlJSONBAgg(joinedTable: String, aggregation: String) =
            String.format(SQL_JSONB_AGG, joinedTable, aggregation)

        // Joined table name, aggregation name, original table name
        const val SQL_JSONB_JOIN = "left join %1\$s on (%3\$s.data->'%2\$s') @> (%1\$s.data->'_uid')"
        fun _sqlJSONBJoin(joinedTable: String, aggregation: String, base: String) =
            String.format(SQL_JSONB_JOIN, joinedTable, aggregation, base)
    }
}