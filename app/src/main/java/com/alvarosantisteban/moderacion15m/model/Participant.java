package com.alvarosantisteban.moderacion15m.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Alvaro Santisteban 13.11.14 - alvarosantisteban@gmail.com
 */
public class Participant implements Parcelable{

    private final ParticipantID mId;
    private String mName;
    private long mNumInterventions;
    private InterventionTime mInterventionsTime;
    private boolean mIsWoman;

    public static class Builder{
        // Required parameters
        private final ParticipantID mId;

        // Optional parameters - Initialized to default
        private String mName = "Participant";

        public Builder(ParticipantID theId){
            this.mId = theId;
        }

        public Builder name(String name){
            mName = name;
            return this;
        }

        public Participant build(){
            return new Participant(this);
        }
    }

    public Participant(Builder builder) {
        this.mId = builder.mId;
        this.mName = builder.mName;
    }

    public ParticipantID getId() {
        return mId;
    }

    public void addTime(long secondsToBeAdded) {
        mInterventionsTime.addSeconds(secondsToBeAdded);
    }

    public void addTimeAndIntervention(long secondsToBeAdded) {
        mInterventionsTime.addSeconds(secondsToBeAdded);
        mNumInterventions++;
    }

    public long getNumInterventions(){return mNumInterventions;}

    public long getTotalInterventionsSecs() {
        return mInterventionsTime.getNumSeconds();
    }

    public InterventionTime getInterventionsTime(){ return mInterventionsTime; }

    public String getName(){ return mName; }

    public void setName(String newName) {
        mName = newName;
    }

    @Override
    public String toString() {
        return mName;
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
        mInterventionsTime = in.readParcelable(InterventionTime.class.getClassLoader());
        mIsWoman = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(mId, flags);
        out.writeString(mName);
        out.writeLong(mNumInterventions);
        out.writeParcelable(mInterventionsTime, flags);
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
