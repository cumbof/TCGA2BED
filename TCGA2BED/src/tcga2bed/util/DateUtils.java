/*
 * Application: TCGA2BED
 * Version: 1.0
 * Author: Fabio Cumbo
 * Organization: Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy
 *
 */
package tcga2bed.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateUtils {

    public static String retrieveAgeFromDaysToBirth(String days) {
        int x_days = Integer.valueOf(days);
        Calendar old_cal = new GregorianCalendar();
        old_cal.add(Calendar.DATE, x_days);
        Calendar actual_cal = new GregorianCalendar();
        return String.valueOf(actual_cal.get(Calendar.YEAR) - old_cal.get(Calendar.YEAR));
    }

    public static String retrieveDaysToBirthFromAge(String age) {
        return String.valueOf(Integer.valueOf(age) * 365);
    }

}
