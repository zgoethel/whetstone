package net.jibini.whetstone.logic

class EncodedSelection(
    val logicalGate: LogicalGate,

    val children: Array<EncodedSelection>
)