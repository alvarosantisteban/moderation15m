package com.alvarosantisteban.moderacion15m.model;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alvarosantisteban.moderacion15m.R;

/**
 * A custom view that represents the view of a Participant, which is composed of an image, a name and their position
 * in the waiting list, if applies.
 *
 * @author Alvaro Santisteban Dieguez 19/01/15 - alvarosantisteban@gmail.com
 */
public class ParticipantView extends RelativeLayout {

    private TextView mParticipantName;
    private TextView mPosWaitingList;
    private ImageView mParticipantImage;

    private Context context;

    public ParticipantView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    public ParticipantView(Context context){
        super(context, null);

        initView(context);
    }

    /**
     * Constructor to use when wanting as a name of the participant its position in the list
     * @param context the Context
     * @param positionInList the position in the list of participants from the Participant
     */
    public ParticipantView(Context context, int positionInList) {
        super(context, null);

        initView(context);
        setParticipantName("Num "+positionInList);
    }

    /**
     * Inits the components of the view
     * @param context
     */
    private void initView(Context context) {
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.participant_view, this, true);

        mParticipantName = (TextView) v.findViewById(R.id.participant_name);
        mPosWaitingList = (TextView) v.findViewById(R.id.participant_waiting_pos);
        mParticipantImage = (ImageView) v.findViewById(R.id.participant_image);
    }

    public String getParticipantName() {
        return mParticipantName.getText().toString();
    }

    public void setParticipantName(String participantName){
        mParticipantName.setText(participantName);
    }

    public void setWaitingListPos(String participantPos) {
        mPosWaitingList.setText(participantPos);
    }

    public void setParticipantImage(){
        //TODO
    }

    public void showWaitingListPos(){
        mPosWaitingList.setVisibility(VISIBLE);
    }

    public void hideWaitingListPos() {
        mPosWaitingList.setVisibility(INVISIBLE);
    }
}
