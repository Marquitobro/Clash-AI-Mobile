package com.clashai.mobile

import java.util.Locale

/**
 * Static Clash Royale card metadata used by the read-only match analyzer.
 * Card names and elixir costs are sourced from RoyaleAPI/cr-api-data.
 */
data class CardInfo(
    val key: String,
    val name: String,
    val elixirCost: Int
)

object CardCatalog {
    val all: List<CardInfo> = listOf(
        CardInfo("knight", "Knight", 3),
        CardInfo("archers", "Archers", 3),
        CardInfo("goblins", "Goblins", 2),
        CardInfo("giant", "Giant", 5),
        CardInfo("pekka", "P.E.K.K.A", 7),
        CardInfo("minions", "Minions", 3),
        CardInfo("balloon", "Balloon", 5),
        CardInfo("witch", "Witch", 5),
        CardInfo("barbarians", "Barbarians", 5),
        CardInfo("golem", "Golem", 8),
        CardInfo("skeletons", "Skeletons", 1),
        CardInfo("valkyrie", "Valkyrie", 4),
        CardInfo("skeleton-army", "Skeleton Army", 3),
        CardInfo("bomber", "Bomber", 2),
        CardInfo("musketeer", "Musketeer", 4),
        CardInfo("baby-dragon", "Baby Dragon", 4),
        CardInfo("prince", "Prince", 5),
        CardInfo("wizard", "Wizard", 5),
        CardInfo("mini-pekka", "Mini P.E.K.K.A", 4),
        CardInfo("spear-goblins", "Spear Goblins", 2),
        CardInfo("giant-skeleton", "Giant Skeleton", 6),
        CardInfo("hog-rider", "Hog Rider", 4),
        CardInfo("minion-horde", "Minion Horde", 5),
        CardInfo("ice-wizard", "Ice Wizard", 3),
        CardInfo("royal-giant", "Royal Giant", 6),
        CardInfo("guards", "Guards", 3),
        CardInfo("princess", "Princess", 3),
        CardInfo("dark-prince", "Dark Prince", 4),
        CardInfo("three-musketeers", "Three Musketeers", 9),
        CardInfo("lava-hound", "Lava Hound", 7),
        CardInfo("ice-spirit", "Ice Spirit", 1),
        CardInfo("fire-spirit", "Fire Spirit", 1),
        CardInfo("miner", "Miner", 3),
        CardInfo("sparky", "Sparky", 6),
        CardInfo("bowler", "Bowler", 5),
        CardInfo("lumberjack", "Lumberjack", 4),
        CardInfo("battle-ram", "Battle Ram", 4),
        CardInfo("inferno-dragon", "Inferno Dragon", 4),
        CardInfo("ice-golem", "Ice Golem", 2),
        CardInfo("mega-minion", "Mega Minion", 3),
        CardInfo("dart-goblin", "Dart Goblin", 3),
        CardInfo("goblin-gang", "Goblin Gang", 3),
        CardInfo("electro-wizard", "Electro Wizard", 4),
        CardInfo("elite-barbarians", "Elite Barbarians", 6),
        CardInfo("hunter", "Hunter", 4),
        CardInfo("executioner", "Executioner", 5),
        CardInfo("bandit", "Bandit", 3),
        CardInfo("royal-recruits", "Royal Recruits", 7),
        CardInfo("night-witch", "Night Witch", 4),
        CardInfo("bats", "Bats", 2),
        CardInfo("royal-ghost", "Royal Ghost", 3),
        CardInfo("ram-rider", "Ram Rider", 5),
        CardInfo("zappies", "Zappies", 4),
        CardInfo("rascals", "Rascals", 5),
        CardInfo("cannon-cart", "Cannon Cart", 5),
        CardInfo("mega-knight", "Mega Knight", 7),
        CardInfo("skeleton-barrel", "Skeleton Barrel", 3),
        CardInfo("flying-machine", "Flying Machine", 4),
        CardInfo("wall-breakers", "Wall Breakers", 2),
        CardInfo("royal-hogs", "Royal Hogs", 5),
        CardInfo("goblin-giant", "Goblin Giant", 6),
        CardInfo("fisherman", "Fisherman", 3),
        CardInfo("magic-archer", "Magic Archer", 4),
        CardInfo("electro-dragon", "Electro Dragon", 5),
        CardInfo("firecracker", "Firecracker", 3),
        CardInfo("mighty-miner", "Mighty Miner", 4),
        CardInfo("super-witch", "Super Witch", 6),
        CardInfo("elixir-golem", "Elixir Golem", 3),
        CardInfo("battle-healer", "Battle Healer", 4),
        CardInfo("skeleton-king", "Skeleton King", 4),
        CardInfo("super-lava-hound", "Super Lava Hound", 8),
        CardInfo("super-magic-archer", "Super Magic Archer", 5),
        CardInfo("archer-queen", "Archer Queen", 5),
        CardInfo("santa-hog-rider", "Santa Hog Rider", 5),
        CardInfo("golden-knight", "Golden Knight", 4),
        CardInfo("super-ice-golem", "Super Ice Golem", 4),
        CardInfo("monk", "Monk", 5),
        CardInfo("super-archers", "Super Archers", 3),
        CardInfo("skeleton-dragons", "Skeleton Dragons", 4),
        CardInfo("terry", "Terry", 4),
        CardInfo("super-mini-pekka", "Super Mini P.E.K.K.A", 5),
        CardInfo("mother-witch", "Mother Witch", 4),
        CardInfo("electro-spirit", "Electro Spirit", 1),
        CardInfo("electro-giant", "Electro Giant", 7),
        CardInfo("raging-prince", "Raging Prince", 5),
        CardInfo("phoenix", "Phoenix", 4),
        CardInfo("cannon", "Cannon", 3),
        CardInfo("goblin-hut", "Goblin Hut", 5),
        CardInfo("mortar", "Mortar", 4),
        CardInfo("inferno-tower", "Inferno Tower", 5),
        CardInfo("bomb-tower", "Bomb Tower", 4),
        CardInfo("barbarian-hut", "Barbarian Hut", 6),
        CardInfo("tesla", "Tesla", 4),
        CardInfo("elixir-collector", "Elixir Collector", 6),
        CardInfo("x-bow", "X-Bow", 6),
        CardInfo("tombstone", "Tombstone", 3),
        CardInfo("furnace", "Furnace", 4),
        CardInfo("goblin-cage", "Goblin Cage", 4),
        CardInfo("goblin-drill", "Goblin Drill", 4),
        CardInfo("party-hut", "Party Hut", 5),
        CardInfo("fireball", "Fireball", 4),
        CardInfo("arrows", "Arrows", 3),
        CardInfo("rage", "Rage", 2),
        CardInfo("rocket", "Rocket", 6),
        CardInfo("goblin-barrel", "Goblin Barrel", 3),
        CardInfo("freeze", "Freeze", 4),
        CardInfo("mirror", "Mirror", 1),
        CardInfo("lightning", "Lightning", 6),
        CardInfo("zap", "Zap", 2),
        CardInfo("poison", "Poison", 4),
        CardInfo("graveyard", "Graveyard", 5),
        CardInfo("the-log", "The Log", 2),
        CardInfo("tornado", "Tornado", 3),
        CardInfo("clone", "Clone", 3),
        CardInfo("earthquake", "Earthquake", 3),
        CardInfo("barbarian-barrel", "Barbarian Barrel", 2),
        CardInfo("heal-spirit", "Heal Spirit", 1),
        CardInfo("giant-snowball", "Giant Snowball", 2),
        CardInfo("royal-delivery", "Royal Delivery", 3),
        CardInfo("party-rocket", "Party Rocket", 5)
    )

    private fun normalize(value: String): String =
        value.lowercase(Locale.ROOT).filter { it.isLetterOrDigit() }

    fun findByName(value: String): CardInfo? {
        val target = normalize(value)
        if (target.isEmpty()) return null
        return all.firstOrNull {
            normalize(it.name) == target || normalize(it.key) == target
        }
    }

    fun elixirCostOf(value: String): Int? = findByName(value)?.elixirCost

    fun findByKey(key: String): CardInfo? =
        all.firstOrNull { it.key.equals(key.trim(), ignoreCase = true) }
}
