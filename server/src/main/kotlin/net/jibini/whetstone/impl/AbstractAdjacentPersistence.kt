package net.jibini.whetstone.impl

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import net.jibini.whetstone.Adjacent
import net.jibini.whetstone.AdjacentPersistence
import net.jibini.whetstone.Document
import net.jibini.whetstone.proxy.Proxy
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaSetter

abstract class AbstractAdjacentPersistence<T : Document>(
    private val documentClass: KClass<T>,

    private val parse: (json: String) -> T,
    private val encode: (document: Document) -> JsonObject
) : AdjacentPersistence<T>
{
    override fun stripAndDistribute(document: T): JsonObject
    {
        val members = documentClass.memberProperties
        val encoded = encode(document)

        for (property in members)
        {
            val annotation = property.findAnnotation<Adjacent>()

            if (annotation != null)
            {
                val adjacent = annotation.adjacentPersistence.objectInstance as AdjacentPersistence<Document>
                val get = property.get(document)

                // Remove object values from JSON document
                encoded.remove(property.name)

                when(true)
                {
                    // Write single adjacent document through to its own persistence
                    get is Document -> {
                        adjacent.writeThrough(get)

                        // Store name instead of object value
                        encoded[property.name] = get._uid
                    }

                    // Write list of adjacent document to persistence
                    get is Iterable<*> -> {
                        encoded[property.name] = JsonArray<String>()

                        for (each in get)
                        {
                            adjacent.writeThrough(each as Document)

                            // Store names instead of object values
                            encoded.array<String>(property.name)!!.add(each._uid)
                        }
                    }

                    else -> throw IllegalStateException("Adjacent documents must be in single or iterable format")
                }
            }
        }

        return encoded
    }

    override fun fillOut(json: JsonObject): T
    {
        val members = documentClass.memberProperties
        val hasAdjacent = mutableListOf<KMutableProperty<*>>()

        // Duplicate JSON object so it can be parsed into an empty document
        val stripped = Parser
            .default()
            .parse(StringBuilder(json.toString())) as JsonObject

        // Find and remove references to linked document names
        for (property in members)
        {
            val annotation = property.findAnnotation<Adjacent>()

            if (annotation != null)
            {
                stripped.remove(property.name)

                if (property is KMutableProperty<*>)
                    hasAdjacent += property
                else
                    throw IllegalStateException("Adjacent object properties must be mutable variables")
            }
        }

        // Parse an empty document with no joins yet
        val parsed = try
        {
            parse(stripped.toString())
        } catch (thrown: Throwable)
        {
            throw IllegalStateException("Parsing of stripped object failed; check that all adjacent objects have " +
                    "default values of null or an empty mutable list", thrown)
        }

        for (property in hasAdjacent)
        {
            val annotation = property.findAnnotation<Adjacent>()!!
            val adjacent = annotation.adjacentPersistence.objectInstance as AdjacentPersistence<Document>

            when(true)
            {
                // Join array of adjacent documents
                json[property.name] is JsonArray<*> -> {
                    val names = json.array<String>(property.name)!!

                    // Get empty mutable list default value
                    val propertyValue = property.getter.call(parsed)

                    if (propertyValue is MutableList<*>)
                    {
                        // Find generic type parameter of mutable list (dirty)
                        val subClass = property.javaSetter!!.parameterTypes[0]

                        for (name in names)
                            // Link each adjacent document as a proxy to its persistence object
                            MutableList::class.java
                                .getMethod("add")
                                .invoke(propertyValue, Proxy.create<Any>(subClass.kotlin) {
                                    adjacent.tank.last { it._uid == name }
                                })
                    } else
                        throw IllegalStateException("Document adjacent object lists must have default values of" +
                                " mutable lists")
                }

                // Join single adjacent document
                json[property.name] is String -> {
                    val name = json.string(property.name)
                    val subClass = property.returnType.classifier as KClass<*>

                    property.setter.call(parsed, Proxy.create<Any>(subClass) {
                        adjacent.tank.last { it._uid == name }
                    })
                }

                else -> throw IllegalStateException("Encoded document must reference single identifier or array")
            }
        }

        return parsed
    }
}