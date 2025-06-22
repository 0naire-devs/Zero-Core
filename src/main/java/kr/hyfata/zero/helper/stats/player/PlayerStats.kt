package kr.hyfata.zero.helper.stats.player

import kr.hyfata.zero.helper.stats.entity.EntityStats

class PlayerStats(val point: PlayerStatPoint) : EntityStats() {
    var attack = 0
    var criticalDamage = 0
    var criticalChance = 0
    var speed = 0
    var defense = 0
    var superCoolDecrease = 0
    var skillDamageIncrease = 0
    var skillLevelLimit = 0
    var currentMana = 0
    var maxMana = 0
    var currentXp = 0
    var maxXp = 0
}