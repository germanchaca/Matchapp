package fiuba.matchapp.controller.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final int MIN_AGE = 16 ;
    TextView _dateText;
    public int birthYear,birthMonth,birthDay;

    public void setEditText(TextView _dateText){
        this._dateText = _dateText;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        c.set(year - MIN_AGE,month,day);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog( getActivity(),this, year - MIN_AGE, month, day);


        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        return datePickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        birthYear = year;
        birthMonth = month;
        birthDay = day;
       this._dateText.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year) );
    }


}
