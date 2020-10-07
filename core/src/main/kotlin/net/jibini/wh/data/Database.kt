package net.jibini.wh.data

import net.jibini.wh.pipeline.Augment

/**
 * A collection of documents of a given type; the contained data relies on an underlying system of one or more
 * persistent databases and a data processing pipeline
 *
 * __Must be annotated as a Spring Boot bean stereotype__
 *
 * @see Augment
 * @see Persistence
 */
abstract class Database
{

}