package com.aj.need.tools.components.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by joan on 31/10/2017.
 */

public class DatePanelFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private final static String TIME_IN_MILLIS = "TIME_IN_MILLIS";


    public static DatePanelFragment newFrozenInstance(long time) {
        Bundle args = new Bundle();
        args.putString(TIME_IN_MILLIS, new Long(time).toString());
        DatePanelFragment fragment = new DatePanelFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog
                (getActivity(), this, year, month, day);

        String timeStr = getArguments().getString(TIME_IN_MILLIS);

        Log.d("DatePanelFragment/time", timeStr);

        freezeTime(dpd, Long.parseLong(timeStr));

        return dpd;
    }

    private void freezeTime(DatePickerDialog dpd, long time) {
        dpd.getDatePicker().setMinDate(time);
        dpd.getDatePicker().setMaxDate(time);
    }


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
    }
}

