package net.jibini.whetstone.sql

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import net.jibini.whetstone.AdjacentPersistence
import net.jibini.whetstone.Document
import net.jibini.whetstone.sql.impl.PostgresPersistenceImpl
import java.sql.Connection

interface PostgresPersistence<T : Document> : AdjacentPersistence<T>
{
    val connection: Connection

    companion object
    {
        inline fun <reified T : Document, reified I : T> create(
            table: String,
            serverAddress: String,

            username: String,
            password: String,
            ssl: Boolean
        ): PostgresPersistence<T> = PostgresPersistenceImpl(
            T::class, table, serverAddress, username, password, ssl,

            parse = {
                Klaxon().parse<I>(it)!!
            },

            encode = {
                Parser
                    .default()
                    .parse(StringBuilder(Klaxon().toJsonString(it))) as JsonObject
            }
        )
    }
}