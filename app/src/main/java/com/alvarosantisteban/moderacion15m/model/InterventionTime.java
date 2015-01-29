package com.alvarosantisteban.moderacion15m.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.concurrent.TimeUnit;

/**
 * @author Alvaro Santisteban Dieguez 29/01/15 - alvarosantisteban@gmail.com
 */
public class InterventionTime implements Parcelable {

    private long mNumSeconds;

    public InterventionTime(long numSeconds){
        mNumSeconds = numSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InterventionTime)) {
            return false;
        }
        InterventionTime interventionTime = (InterventionTime) o;
        return ((Long) interventionTime.mNumSeconds).equals(this.mNumSeconds);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (int)this.mNumSeconds;
        return result;
    }

    /**
     * Returns a string with the following format:
     * mm:ss
     * where m = minutes and s = seconds
     *
     * @return the number of minutes and seconds of the intervention with the explained format.
     */
    @Override
    public String toString() {
        return String.format("%02d:%02d",
                TimeUnit.SECONDS.toMinutes(mNumSeconds),
                TimeUnit.SECONDS.toSeconds(mNumSeconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(mNumSeconds))
        );
    }

    public long getNumSeconds(){
        return mNumSeconds;
    }

    public void setNumSeconds(long seconds){
        mNumSeconds = seconds;
    }

    public void setNumSeconds(int seconds) {
        mNumSeconds = seconds;
    }

    ///////////////////////////////////////////////////////////
    // PARCELABLE
    ///////////////////////////////////////////////////////////

    @Override
    public int describeContents() {
        return 0;
    }

    private InterventionTime(Parcel in) {
        mNumSeconds = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(mNumSeconds);
    }

    public static final Parcelable.Creator<InterventionTime> CREATOR = new Parcelable.Creator<InterventionTime>() {
        public InterventionTime createFromParcel(Parcel in) {
            return new InterventionTime(in);
        }

        public InterventionTime[] newArray(int size) {
            return new InterventionTime[size];
        }
    };
}
