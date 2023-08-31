package ir.vcx.util;

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

    private static Calendar getNowCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"));
    }

}
