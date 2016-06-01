package fiuba.matchapp.utils;

import java.util.Calendar;

/**
 * Created by ger on 31/05/16.
 */
public class AgeUtils {

    public static int getAgeFromBirthDay(int year, int month, int day){
        final Calendar today = Calendar.getInstance();
        int currentYear = today.get(Calendar.YEAR);

        int age = currentYear - year;

        if (today.get(Calendar.MONTH) < month ) {
            age--;
        } else if (today.get(Calendar.MONTH) == month
                && today.get(Calendar.DAY_OF_MONTH) < day) {
            age--;
        }
        return age;
    }
}
