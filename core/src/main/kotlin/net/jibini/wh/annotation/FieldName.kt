package net.jibini.wh.annotation

/**
 * In case a document field name does not match the JSON field name, use this annotation to specify the actual encoded
 * JSON field name
 */
@Target(AnnotationTarget.PROPERTY)
@Retention
annotation class FieldName(
    /**
     * Name of the JSON field name corresponding to the document field
     */
    val name: String
)