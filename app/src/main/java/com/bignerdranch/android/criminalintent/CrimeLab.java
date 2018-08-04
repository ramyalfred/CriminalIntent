package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ramy on 7/25/2018.
 */



public class CrimeLab {

    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;

    public static CrimeLab get(Context context){
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab (Context context) {
        mCrimes = new ArrayList<>();
    }

    public List<Crime> getCrimes(){
        return mCrimes;
    }

    public Crime getCrime (UUID id){
        for (Crime crime:mCrimes){
            if(crime.getId().equals(id)){
                Log.d(getClass().getSimpleName(), "Crime Found");
                return crime;
            }
        }
            Log.d(getClass().getSimpleName(), "Crime Not Found");
            return null;
    }

    public void addCrime(Crime c){
        mCrimes.add(c);
    }
}

