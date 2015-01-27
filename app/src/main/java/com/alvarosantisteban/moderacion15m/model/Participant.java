package com.alvarosantisteban.moderacion15m.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Alvaro Santisteban 13.11.14 - alvarosantisteban@gmail.com
 */
public class Participant implements Parcelable{

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

    public long getTotalInterventionsSecs() {
        return mTotalInterventionsSecs;
    }

    public String getName(){ return mName; }

    @Override
    public String toString() {
        return mId.toString();
    }

    ///////////////////////////////////////////////////////////
    // PARCELABLE
    ///////////////////////////////////////////////////////////

    @Override
    public int describeContents() {
        return 0;
    }

    private Participant(Parcel in) {
        mId = in.readParcelable(ParticipantID.class.getClassLoader());
        mName = in.readString();
        mNumInterventions = in.readLong();
        mTotalInterventionsSecs = in.readLong();
        mIsWoman = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(mId, flags);
        out.writeString(mName);
        out.writeLong(mNumInterventions);
        out.writeLong(mTotalInterventionsSecs);
        out.writeByte((byte) (mIsWoman ? 1 : 0));
    }

    public static final Parcelable.Creator<Participant> CREATOR = new Parcelable.Creator<Participant>() {
        public Participant createFromParcel(Parcel in) {
            return new Participant(in);
        }

        public Participant[] newArray(int size) {
            return new Participant[size];
        }
    };

}
