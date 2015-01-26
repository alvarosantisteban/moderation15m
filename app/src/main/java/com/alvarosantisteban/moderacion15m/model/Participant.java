package com.alvarosantisteban.moderacion15m.model;

/**
 * @author Alvaro Santisteban 13.11.14 - alvarosantisteban@gmail.com
 */
public class Participant {

    private final ParticipantID mId;
    private final String mName;
    private long mNumInterventions;
    private long mTotalInterventionsSecs;
    private final boolean mIsWoman;

    public static class Builder{
        // Required parameters
        private final ParticipantID mId;

        // Optional parameters - Initialized to default
        private String mName = "Participant";
        private long mNumInterventions = 0;
        private long mTotalInterventionsSecs = 0;
        private boolean mIsWoman = true;

        public Builder(ParticipantID theId){
            this.mId = theId;
        }

        public Builder name(String name){
            mName = name;
            return this;
        }

        public Builder numInterventions(long numInterventions) {
            mNumInterventions = numInterventions;
            return this;
        }

        public Builder totalInterventionsSecs(long totalSecs) {
            mTotalInterventionsSecs = totalSecs;
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
        this.mNumInterventions = builder.mNumInterventions;
        this.mTotalInterventionsSecs = builder.mTotalInterventionsSecs;
        this.mIsWoman = builder.mIsWoman;
    }

    public ParticipantID getId() {
        return mId;
    }

    public void addTime(long timeToBeAdded) {
        mTotalInterventionsSecs += timeToBeAdded;

        mNumInterventions++;
    }

    public long getNumInterventions(){return mNumInterventions;}

}
