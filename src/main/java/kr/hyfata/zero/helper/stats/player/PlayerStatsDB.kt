package kr.hyfata.zero.helper.stats.player

import kr.hyfata.zero.zeroDBCore.ZeroDB
import kr.hyfata.zero.zeroDBCore.ZeroDBCore
import org.bukkit.entity.Player
import java.sql.SQLException

class PlayerStatsDB {
    val zeroDB: ZeroDB = ZeroDBCore.getInstance().zeroDB
    
    @Throws(SQLException::class)
    fun getPlayerStats(p: Player): PlayerStats {
        var result : PlayerStats? = null
        val uuid = p.uniqueId.toString()
        val query = "SELECT * FROM player_status WHERE uuid = ?"

        zeroDB.executeQuery(query, uuid).use { rs ->
            rs.statement.use { stmt ->
                stmt.connection.use { ignored ->
                    if (rs.next()) {
                        val point = PlayerStatPoint()

                        point.pointAmount = rs.getInt("point_amount")
                        point.hp = rs.getInt("hp")
                        point.str = rs.getInt("str")
                        point.agi = rs.getInt("agi")
                        point.vit = rs.getInt("vit")
                        point.skl = rs.getInt("skill")
                        point.int = rs.getInt("int")

                        result = PlayerStats(point)
                        result.currentHp = rs.getInt("current_hp")
                        result.level = rs.getInt("level")
                        result.currentXp = rs.getInt("xp")
                        result.currentMana = rs.getInt("mana")
                    }
                }
            }
        }
        return result ?: PlayerStats(PlayerStatPoint()) // return default stats
    }

    @Throws(SQLException::class)
    fun setPlayerStats(p: Player, stats: PlayerStats) {
        val uuid = p.uniqueId.toString()
        val statPoint = stats.point
        zeroDB.executeUpdate(
            "insert into player_status (uuid, point_amount, hp, str, agi, vit, skill, int, current_hp, level, xp, mana) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "on conflict (uuid) do update " +
                    "set point_amount = EXCLUDED.point_amount, hp = EXCLUDED.hp, str = EXCLUDED.str, agi = EXCLUDED.agi, vit = EXCLUDED.vit, skill = EXCLUDED.skill, int = EXCLUDED.int, " +
                    "current_hp = EXCLUDED.current_hp, level = EXCLUDED.level, xp = EXCLUDED.xp, mana = EXCLUDED.mana",
            uuid, statPoint.pointAmount, statPoint.hp, statPoint.str, statPoint.agi, statPoint.vit, statPoint.skl, statPoint.int, stats.currentHp, stats.level, stats.currentXp, stats.currentMana
        )
    }
}