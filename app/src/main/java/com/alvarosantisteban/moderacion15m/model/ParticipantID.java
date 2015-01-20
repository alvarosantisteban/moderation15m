package com.alvarosantisteban.moderacion15m.model;

/**
 * @author Alvaro Santisteban Dieguez 20/01/15 - alvarosantisteban@gmail.com
 */
public class ParticipantID {

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
}
