package net.jibini.wh.data

import net.jibini.wh.pipeline.Augment

/**
 * A persistent database location which can only communicate data via JSON objects; data may be processed via a data
 * processing pipeline and is exposed as an object proxy via a database
 *
 * __Must be annotated as a Spring Boot bean stereotype__
 *
 * @see Augment
 * @see Database
 */
abstract class Persistence
{

}