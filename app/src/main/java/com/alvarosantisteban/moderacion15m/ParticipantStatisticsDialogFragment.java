package com.alvarosantisteban.moderacion15m;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
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

    // Use this instance of the interface to deliver action events
    ParticipantStatisticsDialogListener mListener;

    EditText mNameEditText;
    TextView mNumInterventions;
    TextView mTotalTimeInterventions;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.participant_popup, null);
        mNameEditText = (EditText) view.findViewById(R.id.participant_popup_name_editText);
        mNumInterventions = (TextView) view.findViewById(R.id.participant_popup_numInterventions_text);
        mTotalTimeInterventions = (TextView) view.findViewById(R.id.participant_popup_timeInterventions_text);

        Participant participant = getArguments() != null ? (Participant) getArguments().getParcelable(Constants.KEY_ARG_PARTICIPANT) : null;
        if (participant != null) {
            mNameEditText.setText(participant.getName());
            mNumInterventions.setText(String.valueOf(participant.getNumInterventions()));
            mTotalTimeInterventions.setText(participant.getInterventionsTime().toString());
        }

        builder.setView(view)

                        // Add action buttons
                .setPositiveButton(R.string.participant_popup_change_name, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                        // Change the name
                        mListener.onDialogPositiveClick(ParticipantStatisticsDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.participant_popup_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ParticipantStatisticsDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
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
        }
    }
}
