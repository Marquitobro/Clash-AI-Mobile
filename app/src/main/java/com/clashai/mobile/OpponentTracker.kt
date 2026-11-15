package com.clashai.mobile

/**
 * Match-state logic fed by confirmed visual card-play events.
 * It does not inspect the screen itself; the vision layer supplies card names.
 */
data class OpponentSnapshot(
    val elixir: Int,
    val recentCards: List<CardInfo>,
    val cardsUntilReturn: Map<String, Int>
)

class OpponentTracker(initialElixir: Int = 5) {
    private var elixir = initialElixir.coerceIn(0, 10)
    private var lastElixirUpdateMs = 0L
    private var lastCardKey = ""
    private var lastCardAtMs = 0L
    private val recentCards = mutableListOf<CardInfo>()
    private val cardsUntilReturn = linkedMapOf<String, Int>()

    fun reset(initialElixir: Int = 5) {
        elixir = initialElixir.coerceIn(0, 10)
        lastElixirUpdateMs = 0L
        lastCardKey = ""
        lastCardAtMs = 0L
        recentCards.clear()
        cardsUntilReturn.clear()
    }

    /** Regenerates elixir up to 10. Pass 2800 ms for normal or 1400 ms for double elixir. */
    fun updateElixir(nowMs: Long, millisPerElixir: Long): OpponentSnapshot {
        if (millisPerElixir > 0L) {
            if (lastElixirUpdateMs == 0L) {
                lastElixirUpdateMs = nowMs
            } else if (nowMs > lastElixirUpdateMs) {
                val gained = ((nowMs - lastElixirUpdateMs) / millisPerElixir).toInt()
                if (gained > 0) {
                    elixir = (elixir + gained).coerceAtMost(10)
                    lastElixirUpdateMs += gained * millisPerElixir
                }
            }
        }
        return snapshot()
    }

    /**
     * Records one confirmed opponent card play. Repeated detections of the same
     * card within 2.2 seconds are treated as the same play, not a second spend.
     */
    fun recordPlay(cardName: String, nowMs: Long, millisPerElixir: Long): OpponentSnapshot? {
        val card = CardCatalog.findByName(cardName) ?: return null
        updateElixir(nowMs, millisPerElixir)

        if (card.key == lastCardKey && nowMs - lastCardAtMs < 2200L) {
            return snapshot()
        }

        cardsUntilReturn.entries.toList().forEach { entry ->
            if (entry.key != card.key) {
                val remaining = entry.value - 1
                if (remaining <= 0) cardsUntilReturn.remove(entry.key)
                else cardsUntilReturn[entry.key] = remaining
            }
        }
        cardsUntilReturn[card.key] = 4

        elixir = (elixir - card.elixirCost).coerceAtLeast(0)
        recentCards.add(0, card)
        if (recentCards.size > 8) recentCards.removeAt(recentCards.lastIndex)
        lastCardKey = card.key
        lastCardAtMs = nowMs
        return snapshot()
    }

    fun snapshot(): OpponentSnapshot = OpponentSnapshot(
        elixir = elixir,
        recentCards = recentCards.toList(),
        cardsUntilReturn = cardsUntilReturn.toMap()
    )
}
