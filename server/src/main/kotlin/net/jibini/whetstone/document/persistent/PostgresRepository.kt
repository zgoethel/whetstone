package net.jibini.whetstone.document.persistent

import com.beust.klaxon.Klaxon
import net.jibini.whetstone.document.Document
import net.jibini.whetstone.document.DocumentRepository
import net.jibini.whetstone.document.persistent.impl.DocumentJoinModel
import net.jibini.whetstone.document.persistent.impl.PostgresRepositoryImpl

interface PostgresRepository<T : Document> : DocumentRepository<T>
{
    companion object
    {
        inline fun <reified T : Document> create(
            serverAddress: String,

            username: String,
            password: String,

            ssl: Boolean = true
        ) =
            PostgresRepositoryImpl<T>(serverAddress,
                DocumentJoinModel.buildFrom(T::class),
                username, password, ssl,

                {
                    Klaxon().parse(it)!!
                },

                {
                    Klaxon().toJsonString(it)
                }
            )
    }
}