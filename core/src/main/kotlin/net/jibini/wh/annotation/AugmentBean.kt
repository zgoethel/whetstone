package net.jibini.wh.annotation

import net.jibini.wh.pipeline.Augment
import kotlin.reflect.KClass

/**
 * Indicates which augmentation beans are part of the data processing pipeline
 */
@Target(AnnotationTarget.CLASS)
@Retention
annotation class AugmentBean(
    /**
     * The classes of the augmentation beans
     */
    vararg val beanClass: KClass<out Augment>
)