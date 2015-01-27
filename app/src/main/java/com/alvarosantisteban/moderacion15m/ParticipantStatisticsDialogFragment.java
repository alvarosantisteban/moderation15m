package com.alvarosantisteban.moderacion15m;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alvarosantisteban.moderacion15m.model.Participant;
import com.alvarosantisteban.moderacion15m.util.Constants;

/**
 *
 */
public class ParticipantStatisticsDialogFragment extends android.support.v4.app.DialogFragment {

    private static final String TAG = "ParticipantStatisticsDialogFragment";

    /* The activity that creates an instance of this dialog fragment must
             * implement this interface in order to receive event callbacks.
             * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ParticipantStatisticsDialogListener {
        public void onDialogPositiveClick(DialogFragment dialogFragment);
    }

    private Participant mParticipant;

    // Use this instance of the interface to deliver action events
    ParticipantStatisticsDialogListener mListener;

    EditText mNameEditText;
    TextView mNumInterventions;
    TextView mTotalTimeInterventions;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View view = inflater.inflate(R.layout.participant_popup, container, false);

        mNameEditText = (EditText) view.findViewById(R.id.participant_popup_name_editText);
        mNumInterventions = (TextView) view.findViewById(R.id.participant_popup_numInterventions_text);
        mTotalTimeInterventions = (TextView) view.findViewById(R.id.participant_popup_timeInterventions_text);

        return view;
    }


    // Override the Fragment.onAttach() method to instantiate the ParticipantStatisticsDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ParticipantStatisticsDialogListener so we can send events to the host
            mListener = (ParticipantStatisticsDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ParticipantStatisticsDialogListener");
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            mParticipant = getArguments() != null ? (Participant) getArguments().getParcelable(Constants.KEY_ARG_PARTICIPANT) : null;
            if (mParticipant != null) {
                mNameEditText.setText(mParticipant.getName());
                mNumInterventions.setText(String.valueOf(mParticipant.getNumInterventions()));
                mTotalTimeInterventions.setText(String.valueOf(mParticipant.getTotalInterventionsSecs()));
            }
        }
        Log.d(TAG, "onAttach");
    }
}
