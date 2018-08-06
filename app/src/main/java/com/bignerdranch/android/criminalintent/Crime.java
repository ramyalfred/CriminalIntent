package com.bignerdranch.android.criminalintent;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * Created by Ramy on 7/24/2018.
 */


public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private Boolean mSolved;

    public Crime(){
        //Generate Unique ID
        mId = UUID.randomUUID();
        mDate = new Date();
        mSolved = false;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(this.mDate);
        calendar.set(Calendar.YEAR,mDate.getYear());
        calendar.set(Calendar.MONTH,mDate.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH,mDate.getDay());
        this.mDate = calendar.getTime();
    }

    public void setTime(Date mDate){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(this.mDate);
        calendar.set(Calendar.HOUR,mDate.getHours());
        calendar.set(Calendar.MINUTE,mDate.getMinutes());
        this.mDate = calendar.getTime();
    }

    public Boolean isSolved() {
        return mSolved;
    }

    public void setSolved(Boolean mSolved) {
        this.mSolved = mSolved;
    }
}
