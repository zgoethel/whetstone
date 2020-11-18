package net.jibini.wh.pipeline

import com.google.gson.JsonObject
import java.math.BigInteger

/**
 * A data pipeline operation which increments the document revision counter as a value is written out to a persistent
 * database; revision count can be accessed by a document by specifying a field for key '_rev'
 */
class RevisionAugment : Augment
{
    override fun incoming(value: JsonObject)
    {

    }

    override fun outgoing(value: JsonObject)
    {
        if (value.has("_rev"))
            value.addProperty("_rev", value.get("_rev").asBigInteger + BigInteger.ONE)
        else
            value.addProperty("_rev", 0)
    }
}