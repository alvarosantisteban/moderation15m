package com.alvarosantisteban.moderacion15m.model;

import android.text.format.Time;

/**
 * @author Alvaro Santisteban 13.11.14 - alvarosantisteban@gmail.com
 */
public class Participant {

    String mName;
    Time mCurrentIntervention;
    Time mTotalInterventions;
    Time mTotalInterventionsToday;
    boolean mIsWoman;

    public Participant(String name, Time currentIntervention, Time total, Time totalToday, boolean isWoman) {
        this.mName = name;
        this.mCurrentIntervention = currentIntervention;
        this.mTotalInterventions = total;
        this.mTotalInterventionsToday = totalToday;
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
