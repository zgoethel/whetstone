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

            selectBuilder.append(", jsonb_agg(distinct $toTable.data) as $agg")
            joinBuilder.append("left join $toTable on ($fromTable.data->'$agg') @> ($toTable.data->'_uid')")
                    .append('\n')
        }

        val baseTable = base.table

        return """
select $baseTable._row, $baseTable.data$selectBuilder
from $baseTable

$joinBuilder
group by $baseTable._row, $baseTable.data;
        """.trim()
    }