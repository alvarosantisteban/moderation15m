package com.alvarosantisteban.moderacion15m.model;

import android.text.format.Time;

/**
 * @author Alvaro Santisteban 13.11.14 - alvarosantisteban@gmail.com
 */
public class Participant {

    private final ParticipantID mId;
    private final String mName;
    private final Time mCurrentIntervention;
    private final Time mTotalInterventions;
    private final Time mTotalInterventionsToday;
    private final boolean mIsWoman;

    public static class Builder{
        // Required parameters
        private final ParticipantID mId;

        // Optional parameters - Initialized to default
        private String mName = "Participant";
        private Time mCurrentIntervention = new Time();
        private Time mTotalInterventions = new Time();;
        private Time mTotalInterventionsToday = new Time();;
        private boolean mIsWoman = true;

        public Builder(ParticipantID theId){
            this.mId = theId;
        }

        public Builder name(String name){
            mName = name;
            return this;
        }

        public Builder currentIntervention(Time current) {
            mCurrentIntervention = current;
            return this;
        }

        public Builder totalInterventions(Time total) {
            mTotalInterventions = total;
            return this;
        }

        public Builder totalInterventionsToday(Time totalToday) {
            mTotalInterventionsToday = totalToday;
            return this;
        }

        public Builder isWoman(boolean isWoman) {
            mIsWoman = isWoman;
            return this;
        }

        public Participant build(){
            return new Participant(this);
        }
    }

    public Participant(Builder builder) {
        this.mId = builder.mId;
        this.mName = builder.mName;
        this.mCurrentIntervention = builder.mCurrentIntervention;
        this.mTotalInterventions = builder.mTotalInterventions;
        this.mTotalInterventionsToday = builder.mTotalInterventionsToday;
        this.mIsWoman = builder.mIsWoman;
    }

    public ParticipantID getId() {
        return mId;
    }

    /*
    public Participant(ParticipantID id, String name, Time currentIntervention, Time total, Time totalToday, boolean isWoman) {
        this.mId = id;
        this.mName = name;
        this.mCurrentIntervention = currentIntervention;
        this.mTotalInterventions = total;
        this.mTotalInterventionsToday = totalToday;
        this.mIsWoman = isWoman;
    }

    public ParticipantID getmId() {
        return mId;
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
      */
}
