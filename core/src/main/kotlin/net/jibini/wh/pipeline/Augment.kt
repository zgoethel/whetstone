package net.jibini.wh.pipeline

import com.google.gson.JsonObject
import net.jibini.wh.data.Database
import net.jibini.wh.data.Persistence

/**
 * A step in the data processing pipeline which sits between a persistence and a database
 *
 * __Must be annotated as a Spring Boot bean stereotype__
 *
 * @see Database
 * @see Persistence
 */
interface Augment
{
    /**
     * Apply changes to data as it passes from a persistence towards the database
     *
     * @param value Incoming object values as JSON
     */
    fun incoming(value: JsonObject)

    /**
     * Apply changes to data as it passes from the database towards a persistence
     *
     * @param value Outgoing object values as JSON
     */
    fun outgoing(value: JsonObject)
}