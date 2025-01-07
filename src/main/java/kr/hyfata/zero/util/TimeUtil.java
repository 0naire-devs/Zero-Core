package kr.hyfata.zero.util;

import java.sql.Timestamp;
import java.util.Date;

public class TimeUtil {
    public static String getRemainingTimeText(Timestamp expire) {
        Timestamp currentTime = new Timestamp(new Date().getTime());
        if (currentTime.after(expire)) {
            return TextFormatUtil.getFormattedText("&c만료됨");
        } else {
            long milliseconds = expire.getTime() - currentTime.getTime();
            int seconds = (int) milliseconds / 1000;
            int minutes = seconds / 60;
            int hours = minutes / 60;
            int days = hours / 24;
            return TextFormatUtil.getFormattedText("&c만료까지 남은 시간: " + days + "일 " + hours % 24 + "시간 " + minutes % 60 + "분 " + seconds % 60 + "초");
        }
    }

    public static boolean isExpired(Timestamp expire) {
        Timestamp currentTime = new Timestamp(new Date().getTime());
        return currentTime.after(expire);
    }
}
