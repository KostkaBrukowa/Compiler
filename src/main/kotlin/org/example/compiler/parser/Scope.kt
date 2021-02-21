package org.example.compiler.parser

import org.example.compiler.parser.productions.IdReferenceHolder

typealias Block = MutableMap<String, HolderWithValue>

data class HolderWithValue(val holder: IdReferenceHolder, val value: Production?)

class Scope(private val identifiersBlocks: List<Block> = listOf(mutableMapOf())) {

    fun pushNewBlock(): Scope =
        Scope(identifiersBlocks + mutableMapOf())

    fun pushNewBlock(holder: IdReferenceHolder): Scope =
        Scope(
            identifiersBlocks + mutableMapOf(
                holder.id to HolderWithValue(
                    holder,
                    null
                )
            )
        )

    fun pushNewBlock(holderWithValue: HolderWithValue): Scope =
        Scope(identifiersBlocks + mutableMapOf(holderWithValue.holder.id to holderWithValue))

    fun addHolderToCurrentBlock(holder: IdReferenceHolder) = addHolderToCurrentBlock(
        HolderWithValue(
            holder,
            null
        )
    )
    fun addAllHoldersToCurrentBlock(holders: List<IdReferenceHolder>) =
        addAllHoldersWithValuesToCurrentBlock(holders.map {
            HolderWithValue(
                it,
                null
            )
        })


    fun addAllHoldersWithValuesToCurrentBlock(holderWithValues: List<HolderWithValue>): Scope {
        holderWithValues.forEach { addHolderToCurrentBlock(it) }

        return this
    }

    fun getLatestIdHolder(id: String): IdReferenceHolder? = getLatestId(id)?.holder

    fun getLatestId(id: String): HolderWithValue? = getLastBlockWithId(id)?.get(id)

    fun getScopeForId(id: String): Scope? {
        val firstBlockIndexWithId = identifiersBlocks.indexOfLast { it.containsKey(id) }

        return Scope(
            identifiersBlocks.subList(
                0,
                firstBlockIndexWithId + 1
            )
        )
    }

    fun modifyIdValue(id: String, newValue: Production) {
        val lastBlockWithId = getLastBlockWithId(id)
        val oldHolder =
            lastBlockWithId?.get(id)?.holder ?: throw RuntimeException("Coulndt find id during parse")

        lastBlockWithId[id] = HolderWithValue(oldHolder, newValue)
    }

    fun copy(): Scope =
        Scope(identifiersBlocks.map { it.toMutableMap() })

    private fun getLastBlockWithId(id: String): Block? = identifiersBlocks.findLast { it.containsKey(id) }

    private fun lastBlock(): Block = identifiersBlocks.last()

    private fun addHolderToCurrentBlock(holderWithValue: HolderWithValue) {
        lastBlock()[holderWithValue.holder.id] = holderWithValue
    }

}

class IdentifierRedefinitionException(id: String) :
    Exception("This id $id was already declared in this scope.")
