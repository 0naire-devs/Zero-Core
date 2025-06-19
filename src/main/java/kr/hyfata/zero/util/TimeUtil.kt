package kr.hyfata.zero.util

import kr.hyfata.zero.helper.format.TextFormatHelper
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object TimeUtil {
    fun getRemainingTimeText(expire: Timestamp): String {
        val currentTime = Timestamp(Date().time)
        if (currentTime.after(expire)) {
            return TextFormatHelper.getFormattedText("&c만료됨")
        } else {
            val milliseconds = expire.getTime() - currentTime.getTime()
            val seconds = milliseconds.toInt() / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            return TextFormatHelper.getFormattedText("&c만료까지 남은 시간: " + days + "일 " + hours % 24 + "시간 " + minutes % 60 + "분 " + seconds % 60 + "초")
        }
    }

    @Throws(ParseException::class)
    fun stringToTimestamp(timestampString: String?): Timestamp {
        val pattern = "yyyy-MM-dd HH:mm:ss"
        val dateFormat = SimpleDateFormat(pattern)
        val parsedDate = dateFormat.parse(timestampString)
        return Timestamp(parsedDate.time)
    }

    val currentDateTimeString: String
        get() {
            val now = LocalDateTime.now() // 현재 날짜와 시간
            val formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // 원하는 형식 지정
            return now.format(formatter) // 포맷된 문자열 반환
        }
}
