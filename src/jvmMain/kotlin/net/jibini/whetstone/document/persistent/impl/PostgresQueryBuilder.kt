package net.jibini.whetstone.document.persistent.impl

import net.jibini.whetstone.document.persistent.DocumentJoinModel
import net.jibini.whetstone.document.persistent.DocumentJoinStack
import net.jibini.whetstone.document.table
import java.lang.StringBuilder

val DocumentJoinModel.postgresQuery: String
    get()
    {
        val selectBuilder = StringBuilder()
        val joinBuilder = StringBuilder()

        val stack = DocumentJoinStack()

        for (join in joins)
        {
            stack.navigateFlat(join)

            val toTable = join.to.table
            val fromTable = join.from.table
            val agg = join.asAggregate

            selectBuilder.append(", ${PostgresRepository._sqlJSONBAgg(toTable, stack.aggregate)}")
            joinBuilder.append(" ${PostgresRepository._sqlJSONBJoin(toTable, agg, fromTable)}")
        }

        return PostgresRepository._sqlRowSelect(base.table, selectBuilder.toString().trim(),
            joinBuilder.toString().trim())
    }