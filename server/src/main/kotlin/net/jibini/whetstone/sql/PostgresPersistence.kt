package net.jibini.whetstone.sql

import com.beust.klaxon.Klaxon
import net.jibini.whetstone.Document
import net.jibini.whetstone.Persistence
import net.jibini.whetstone.sql.impl.PostgresPersistenceImpl
import java.sql.Connection

interface PostgresPersistence<T : Document> : Persistence<T>
{
    val connection: Connection

    companion object
    {
        inline fun <reified T : Document> create(
            table: String,

            serverAddress: String,
            username: String,
            password: String,
            ssl: Boolean
        ): PostgresPersistence<T> = PostgresPersistenceImpl<T>(
            table, serverAddress, username, password, ssl,

            parse = {
                Klaxon().parse(it)!!
            } ,

            encode = {
                Klaxon().toJsonString(it)
            }
        )
    }
}