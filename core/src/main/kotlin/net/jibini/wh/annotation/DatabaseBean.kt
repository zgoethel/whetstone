package net.jibini.wh.annotation

import net.jibini.wh.data.Database
import kotlin.reflect.KClass

/**
 * Indicates which database bean stores the annotated document type
 */
@Target(AnnotationTarget.CLASS)
@Retention
annotation class DatabaseBean(
    /**
     * The class of the repository bean
     */
    val beanClass: KClass<out Database>
)