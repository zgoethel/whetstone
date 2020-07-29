package net.jibini.whetstone.document.persistent.impl

import com.beust.klaxon.Klaxon
import net.jibini.whetstone.document.Document
import net.jibini.whetstone.document.DocumentRepository
import net.jibini.whetstone.document.persistent.DocumentJoin
import net.jibini.whetstone.document.persistent.DocumentJoinModel
import net.jibini.whetstone.document.persistent.DocumentJoinStack
import net.jibini.whetstone.document.table
import org.json.JSONObject
import java.sql.Connection
import java.sql.DriverManager
import java.util.*
import kotlin.reflect.KClass

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
        statement.execute(
            _sqlTableCreate(
                joinModel.base.table,
                SQL_TABLE_DEFAULT_SCHEMA
            )
        )
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
            val json = JSONObject()

            for (join in joinModel.joins)
            {
                stack.navigateFlat(join)

                val columnName = "${stack.aggregatePrefix}${join.asAggregate}"
                val columnString = results.getString(columnName)
                if (columnString == "[null]")
                    continue

                TODO("MAP INSTANCES TO THEIR _uid AND POPULATE AGGREGATES")
            }
        }

        TODO("NOT YET IMPLEMENTED")
    }

    override fun put(document: T)
    {
        val encoded = Klaxon().toJsonString(document)
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