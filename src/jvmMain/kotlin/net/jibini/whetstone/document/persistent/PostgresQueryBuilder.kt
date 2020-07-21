package net.jibini.whetstone.document.persistent

import net.jibini.whetstone.document.Document
import java.lang.StringBuilder
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

class PostgresQueryBuilder<T : Document>(
        override val document : KClass<T>
) : DocumentQueryBuilder<T, String>
{
    private val selectBuilder = StringBuilder()
    private val joinBuilder = StringBuilder()

    private val table = document.findAnnotation<Table>()!!.tableName

    override fun join(to: KClass<out Document>, on: String) : DocumentQueryBuilder<T, String>
    {
        val toTable = to.findAnnotation<Table>()!!.tableName

        selectBuilder.append(", jsonb_agg($toTable.data) as $toTable")

        if (joinBuilder.isNotEmpty())
            joinBuilder.append('\n')
        joinBuilder.append("left join $toTable on ($table.data->'$on') @> ($toTable.data->'_uid')")

        return this
    }

    override val built
        get() = """
        
            select $table._row, $table.data$selectBuilder
            from $table
    
            $joinBuilder
            
            group by $table._row, $table.data;
            
        """.trimIndent().trim()
}
