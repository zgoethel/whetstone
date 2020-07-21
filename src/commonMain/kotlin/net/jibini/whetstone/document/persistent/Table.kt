package net.jibini.whetstone.document.persistent

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Table(
        val tableName: String
)