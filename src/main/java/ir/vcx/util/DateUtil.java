package ir.vcx.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

@Slf4j
@Component
public class DateUtil {

    public static Date getNowDate() {
        return getNowCalendar().getTime();
    }

    public static Date futureTime(TimeInFuture timeInFuture) {
        Calendar nowCalendar = getNowCalendar();
        nowCalendar.add(Calendar.DATE, timeInFuture.time);
        return nowCalendar.getTime();
    }

    public static Date calculateTime(int days) {
        Calendar nowCalendar = getNowCalendar();
        nowCalendar.add(Calendar.DATE, days);
        return nowCalendar.getTime();
    }

    public static Date epochToDate(long epoch) {
        return new Date(epoch);
    }

    private static Calendar getNowCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"));
    }


    @Getter
    @AllArgsConstructor
    public enum TimeInFuture {

        OneDay(1),
        OneWeek(7),
        OneHundredYears(365 * 100),

        ;

        private final int time;
    }

}
