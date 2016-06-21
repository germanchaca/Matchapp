package fiuba.matchapp.utils;

import java.util.Calendar;
import java.util.regex.Pattern;

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

    public static int getAgeFromBirthDay(String birthdate){

        String[] parts = birthdate.split(Pattern.quote("/"));
        int year = Integer.parseInt(parts[2]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[0]);

        return getAgeFromBirthDay(year,month,day);
    }
}
