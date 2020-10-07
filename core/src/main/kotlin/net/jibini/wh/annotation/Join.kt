package net.jibini.wh.annotation

/**
 * Indicates that a field in a document should be pulled from and written to its respective external repository bean;
 * uses the bean as specified in the annotated type's annotations
 *
 * @see DatabaseBean
 */
@Target(AnnotationTarget.PROPERTY)
@Retention
annotation class Join