package net.jibini.whetstone

import com.beust.klaxon.JsonObject

interface AdjacentPersistence<T : Document> : Persistence<T>
{
    fun stripAndDistribute(document: T): JsonObject

    fun fillOut(json: JsonObject): T
}