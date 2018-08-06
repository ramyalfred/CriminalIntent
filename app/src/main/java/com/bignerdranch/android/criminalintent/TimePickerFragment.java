package com.bignerdranch.android.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.LayoutInflaterCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Ramy on 8/4/2018.
 */

public class TimePickerFragment extends DialogFragment {

    private static final String ARG_TIME = "Time";
    public static final String EXTRA_TIME = "com.bignerdranch.android.criminalintent.time";

    public static TimePickerFragment newInstance (Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME,date);

        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setArguments(args);
        return timePickerFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        Date date = (Date) args.getSerializable(ARG_TIME);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time,null);
        final TimePicker mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_time_picker);

        final Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes =calendar.get(Calendar.MINUTE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(hours);
            mTimePicker.setMinute(minutes);
        } else {
            mTimePicker.setCurrentHour(hours);
            mTimePicker.setCurrentMinute(minutes);
        }

        Dialog datePickerDialog = new AlertDialog.Builder(getActivity()).setView(v).setTitle("Time of Crime: ").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int hours;
                int minutes;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hours = mTimePicker.getHour();
                    minutes = mTimePicker.getMinute();
                } else {
                    hours = mTimePicker.getCurrentHour();
                    minutes = mTimePicker.getCurrentMinute();
                }
                calendar.set(Calendar.HOUR_OF_DAY,hours);
                calendar.set(Calendar.MINUTE,minutes);

                Date date = calendar.getTime();

                sendResult(date);

            }
        }).create();

        return datePickerDialog;
    }

    private void sendResult(Date date){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME,date);

        getTargetFragment().onActivityResult(getTargetRequestCode(),RESULT_OK,intent);
    }
}
