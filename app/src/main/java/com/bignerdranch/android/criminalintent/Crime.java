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
    private String mSuspect;
    private Date mDate;
    private boolean mSolved;

    public Crime(){
        this(UUID.randomUUID());
    }

    public Crime(UUID id){
        mId = id;
        mDate = new Date();
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

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String mSuspect) {
        this.mSuspect = mSuspect;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        Calendar newDate = new GregorianCalendar();
        newDate.setTime(mDate);

        Calendar updatedDate = new GregorianCalendar();
        updatedDate.setTime(this.mDate);

        updatedDate.set(Calendar.YEAR,newDate.get(Calendar.YEAR));
        updatedDate.set(Calendar.MONTH,newDate.get(Calendar.MONTH));
        updatedDate.set(Calendar.DAY_OF_MONTH,newDate.get(Calendar.DAY_OF_MONTH));
        this.mDate = updatedDate.getTime();
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
