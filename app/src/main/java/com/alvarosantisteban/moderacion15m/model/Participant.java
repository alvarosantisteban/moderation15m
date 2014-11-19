package com.alvarosantisteban.moderacion15m.model;

import android.text.format.Time;

/**
 * Created by alvaro on 13.11.14.
 */
public class Participant {

    private final String name;
    private final int currentIntervention;
    private final int total;
    private final int totalToday;
    String mName;
    Time mCurrentIntervention;
    Time mTotalInterventions;
    Time mTotalInterventionsToday;
    boolean mIsWoman;

    public Participant(String name, int currentIntervention, int total, int totalToday, boolean isWoman) {
        this.name = name;
        this.currentIntervention = currentIntervention;
        this.total = total;
        this.totalToday = totalToday;
        this.mIsWoman = isWoman;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public Time getmCurrentIntervention() {
        return mCurrentIntervention;
    }

    public void setmCurrentIntervention(Time mCurrentIntervention) {
        this.mCurrentIntervention = mCurrentIntervention;
    }

    public Time getmTotalInterventions() {
        return mTotalInterventions;
    }

    public void setmTotalInterventions(Time mTotalInterventions) {
        this.mTotalInterventions = mTotalInterventions;
    }

    public Time getmTotalInterventionsToday() {
        return mTotalInterventionsToday;
    }

    public void setmTotalInterventionsToday(Time mTotalInterventionsToday) {
        this.mTotalInterventionsToday = mTotalInterventionsToday;
    }

    public boolean ismIsWoman() {
        return mIsWoman;
    }

    public void setmIsWoman(boolean mIsWoman) {
        this.mIsWoman = mIsWoman;
    }

}
