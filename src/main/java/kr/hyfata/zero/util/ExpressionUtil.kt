package kr.hyfata.zero.util

import net.objecthunter.exp4j.ExpressionBuilder
import org.bukkit.entity.Player

object ExpressionUtil {

    /**
     * 주어진 수식을 계산하여 결과를 반환합니다.
     *
     * @param player 계산에 필요한 플레이스홀더 값을 제공할 플레이어
     * @param expression 계산할 수식 문자열 (예: "%player_level% * 10 + 5")
     * @return 계산된 결과 (Double 타입)
     */
    fun eval(player: Player, expression: String): Double {
        // 1. 플레이스홀더를 실제 값으로 변경합니다.
        //    (PAPI 플레이스홀더가 있다면 여기서 처리할 수도 있습니다.)
        val replacedExpression = expression
            .replace("%player_level%", player.level.toString())
            .replace("%player_health%", player.health.toString())
            // 필요한 다른 플레이스홀더들을 여기에 추가하세요.

        // 2. exp4j를 사용하여 수식을 계산합니다.
        return try {
            val builder = ExpressionBuilder(replacedExpression)
            // 수식에 변수가 있다면 여기서 추가할 수 있습니다.
            // builder.variable("x")

            val expr = builder.build()
            expr.evaluate()
        } catch (e: Exception) {
            // 수식이 잘못되었거나 계산 중 오류가 발생한 경우
            // 기본값(예: 0.0)을 반환하거나 오류를 로깅할 수 있습니다.
            println("[ZeroCore] 수식 계산 오류: $expression | ${e.message}")
            0.0
        }
    }
}
