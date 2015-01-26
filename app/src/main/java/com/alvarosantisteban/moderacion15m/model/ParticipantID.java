package com.alvarosantisteban.moderacion15m.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Alvaro Santisteban Dieguez 20/01/15 - alvarosantisteban@gmail.com
 */
public class ParticipantID implements Parcelable {

    String mID;

    public ParticipantID(String ID){
        mID = ID;
    }

    public ParticipantID(Integer ID){
        mID = new String(String.valueOf(ID));
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }
        if(!(o instanceof ParticipantID)){
            return false;
        }
        ParticipantID participantID = (ParticipantID)o;
        return participantID.mID.equals(this.mID);
    }

    @Override
    public int hashCode(){
        int result = 17;
        result = 31*result+Integer.parseInt(this.mID);
        return result;
    }

    @Override
    public String toString(){
        return mID;
    }

    ///////////////////////////////////////////////////////////
    // PARCELABLE
    ///////////////////////////////////////////////////////////

    @Override
    public int describeContents() {
        return 0;
    }

    private ParticipantID(Parcel in) {
        mID = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mID);
    }

    public static final Parcelable.Creator<ParticipantID> CREATOR = new Parcelable.Creator<ParticipantID>() {
        public ParticipantID createFromParcel(Parcel in) {
            return new ParticipantID(in);
        }

        public ParticipantID[] newArray(int size) {
            return new ParticipantID[size];
        }
    };
}
