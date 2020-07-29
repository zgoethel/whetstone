package net.jibini.whetstone.document.persistent

import net.jibini.whetstone.document.table
import java.lang.StringBuilder

val DocumentJoinModel.postgresQuery: String
    get()
    {
        val selectBuilder = StringBuilder()
        val joinBuilder = StringBuilder()

        for (join in joins)
        {
            val toTable = join.to.table
            val fromTable = join.from.table
            val agg = join.asAggregate


            selectBuilder.append(", ${PostgresRepository._sqlJSONBAgg(toTable, agg)}")
            joinBuilder.append(" ${PostgresRepository._sqlJSONBJoin(toTable, agg, fromTable)}")
        }

        return PostgresRepository._sqlRowSelect(base.table, selectBuilder.toString().trim(),
            joinBuilder.toString().trim())
    }