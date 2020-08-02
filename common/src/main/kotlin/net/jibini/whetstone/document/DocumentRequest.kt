package net.jibini.whetstone.document

class DocumentRequest(
    val clauses: MutableList<RequestClause<*, *>>
)
{
    companion object
    {
        fun create(clauses: MutableList<RequestClause<*, *>> = mutableListOf()) = DocumentRequest(clauses)

        fun specific(uid: String) = create(mutableListOf(EqualsClause("_uid", uid)))
    }
}

interface RequestClause<S, T>
{
    val identifier: String

    val valueS: S
    val valueT: T
}

class SimpleClause<S, T>(
    override val identifier: String,

    override val valueS: S,
    override val valueT: T
) : RequestClause<S, T>

class EqualsClause<T>(
    field: String,
    value: T
) : RequestClause<String, T> by SimpleClause("equals", field, value)