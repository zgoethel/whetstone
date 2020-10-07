package net.jibini.wh.docs

import com.google.gson.JsonObject
import net.jibini.wh.pipeline.Augment
import org.springframework.stereotype.Component

@Component
class TestDocumentAugment : Augment
{
    override fun incoming(value: JsonObject)
    {
        TODO("Not yet implemented")
    }

    override fun outgoing(value: JsonObject)
    {
        TODO("Not yet implemented")
    }
}