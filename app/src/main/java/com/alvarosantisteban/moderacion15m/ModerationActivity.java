package com.alvarosantisteban.moderacion15m;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.alvarosantisteban.moderacion15m.model.InterventionTime;
import com.alvarosantisteban.moderacion15m.model.Participant;
import com.alvarosantisteban.moderacion15m.model.ParticipantID;
import com.alvarosantisteban.moderacion15m.model.ParticipantView;
import com.alvarosantisteban.moderacion15m.util.Constants;
import com.alvarosantisteban.moderacion15m.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * This activity allows the moderator to interact with the table of participants created.
 * @author Alvaro Santisteban 13.11.14 - alvarosantisteban@gmail.com
 */
public class ModerationActivity extends FragmentActivity implements ParticipantStatisticsDialogFragment.ParticipantStatisticsDialogListener{

    private static final String TAG = "ModerationActivity";

    // The pixels that are subtracted to the size of a row so the image in it looks fine
    private static final int SUBTRACT_TO_ROW_SIZE = 10;
    // The top margin defined in the layout of the table
    private static final int TOP_MARGIN_OF_TABLE = 10;
    private static final int DEFAULT_MAX_NUM_SEC_PARTICIPATION = 5;
    private static final int DEFAULT_MAX_NUM_SEC_DEBATE = 30;
    private static final int DEVICE_VIBRATION_IN_MILLISECONDS = 2000;
    private static final int PARTICIPANT_INTERVENTION_TIMER = 0;
    private static final int DEBATE_TOTAL_TIME_TIMER = 1;
    private static final int MAX_NUM_PARTICIPANTS_TO_ADD_EXTRA_COLUMN = 28;

    // The table layout with the views of the participants
    TableLayout tableLayoutOfParticipants;

    // The list of participants
    List<Participant> mParticipants = new ArrayList<Participant>();
    // The participant currently talking
    Participant mCurrentParticipant;
    // The participant currently being edited
    Participant mEditedParticipant;
    // The ParticipantView currently being edited
    ParticipantView mEditedParticipantView;

    // The waiting list of Participants identified by their ids
    List<ParticipantView> mWaitingList = new ArrayList<ParticipantView>();
    // A HasMap that connects the id and the ParticipantView
    Map<ParticipantID, ParticipantView> mIdAndViewHashMap = new HashMap<ParticipantID, ParticipantView>();

    // The scheduler used as a timer for the interventions
    ScheduledFuture mScheduleFutureIntervention;
    // The scheduler used as a timer for the debate
    ScheduledFuture mScheduleFutureDebate;

    int mNumColumns;
    int mNumParticipants;
    private Context context;

    // The maximum number of seconds that a participant can talk before the timer runs out
    private int mParticipantTimeLimit;
    // The maximum number of seconds that the debate can last
    private int mDebateTimeLimit;
    private boolean mDebateHasStarted = false;
    private boolean mDebateHasEnded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderation);

        context = this;

        tableLayoutOfParticipants = (TableLayout) findViewById(R.id.participants_table);

        // Get the intent to obtain its extras
        Intent intentFromMain = getIntent();
        //mNumColumns = intentFromMain.getIntExtra(Constants.EXTRA_NUM_COLUMNS, 0);
        mNumParticipants = intentFromMain.getIntExtra(Constants.EXTRA_NUM_PARTICIPANTS, 0);
        mParticipantTimeLimit = intentFromMain.getIntExtra(Constants.EXTRA_MAX_NUM_SEC_PARTICIPATION, DEFAULT_MAX_NUM_SEC_PARTICIPATION);
        mDebateTimeLimit = intentFromMain.getIntExtra(Constants.EXTRA_TOTAL_TIME_DEBATE_SECS, DEFAULT_MAX_NUM_SEC_DEBATE);

        // Add or subtract one column to make it look better
        mNumColumns = mNumParticipants < MAX_NUM_PARTICIPANTS_TO_ADD_EXTRA_COLUMN ?
                (mNumParticipants / 4) + 1 :
                (mNumParticipants / 4) - 1;

        // Calculate the number of needed rows
        int numRows = calculateNumOfRows(mNumColumns, mNumParticipants);

        // Build the table tableLayoutOfParticipants
        buildTable(numRows, mNumColumns);
    }

    /**
     * Calculates the number of rows needed in order to have the number of participants passed by parameter and the asked
     * number of columns.
     *
     * @param numColumns the desired number of columns
     * @param numParticipants the desired number of participants
     * @return the number of needed rows
     */
    private int calculateNumOfRows(int numColumns, int numParticipants) {
        /*
        int extraRow = addExtraRow(numColumns, numParticipants);

        // By making it an integer, we ensure that it will be the right number
        int firstRowMiddleParticipants = numColumns-2;
        return ((numParticipants - firstRowMiddleParticipants)/2) + extraRow;


        */

        // By making it an integer, we ensure that it will be the right number
        int firstRowMiddleParticipants = numColumns - 2;
        return ((numParticipants - firstRowMiddleParticipants - firstRowMiddleParticipants) / 2)+1;
    }

    /**
     * Checks if the adding of the two parameters produces an odd number, in which case a extra row is needed
     *
     * @param numColumns the number of columns of the table
     * @param numParticipants the number of participants in the table
     * @return 1 if a row must be added, 0 otherwise
     */
    private int addExtraRow(int numColumns, int numParticipants){
        // Check if the adding of the two parameters produces an odd number
        if (Utils.isOdd(numColumns + numParticipants)){
            // If so, needs an extra row
            return 1;
        }
        return 0;
    }

    private boolean isMiddleColumn(int column){
        int pos = mNumColumns/2 + 1;
        return pos == column;
    }

    /**
     * Creates the table layout with the participants
     *
     * @param rows the number of rows of the table
     * @param cols the number of columns of the table
     */
    private void buildTable(int rows, int cols) {
        int numAddedParticipants = 0;
        int pixelSizeForRow = calculatePaddingBetweenRows(rows);

        boolean isModeratorAdded = false;

        // Create rows
        for (int i = 1; i <= rows; i++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            // Create columns
            for (int j = 1; j <= cols; j++) {

                if (numAddedParticipants < mNumParticipants){

                    if (i == 1){ // First row
                        numAddedParticipants = createAndAddParticipantView(numAddedParticipants, pixelSizeForRow, row);
                    }else if (i == rows){ // Last row
                        if (isMiddleColumn(j)) {
                            // Add Moderator
                            isModeratorAdded = createAndAddModerator(pixelSizeForRow, row);
                        }  else {
                            // Add all except moderator
                            // Create the ParticipantView
                            numAddedParticipants = createAndAddParticipantView(numAddedParticipants, pixelSizeForRow, row);
                        }
                    }else if (j == 1 || j == cols){ // First or last column
                        // Create the ParticipantView
                        numAddedParticipants = createAndAddParticipantView(numAddedParticipants, pixelSizeForRow, row);
                    } else{ // Columns in between
                        // Add an empty image to the row
                        row.addView(new ImageView(context));
                    }
                } else{
                    // Last check to see if the moderator was added
                    if(!isModeratorAdded){
                        isModeratorAdded = createAndAddModerator(pixelSizeForRow, row);

                    }
                }
            }

            // Add the row to the table
            tableLayoutOfParticipants.addView(row);
        }
    }

    /**
     * Creates a ImageView that represents a moderator and adds it to the row
     *
     * @param pixelSizeForRow the size for the row in pixels
     * @param row the row where the ImageView is added
     * @return true if no exception arose
     */
    private boolean createAndAddModerator(int pixelSizeForRow, TableRow row) {
        boolean isModeratorAdded;// Add Moderator
        final ImageView moderatorImage = new ImageView(this);
        moderatorImage.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                pixelSizeForRow - SUBTRACT_TO_ROW_SIZE));
        // Set the margins of the ParticipantView
        setMargins(moderatorImage, Constants.MARGIN_IMAGEVIEW_IN_TABLE_SIDES, Constants.MARGIN_IMAGEVIEW_IN_TABLE_BOTTOM,
                Constants.MARGIN_IMAGEVIEW_IN_TABLE_SIDES, Constants.MARGIN_IMAGEVIEW_IN_TABLE_BOTTOM);

        // Set the click listener
        moderatorImage.setOnClickListener(mOnModeratorClickListener);

        moderatorImage.setImageResource(R.drawable.btn_moderator_normal);
        row.addView(moderatorImage);
        isModeratorAdded = true;
        return isModeratorAdded;
    }

    /**
     *
     * Creates a ParticipantView, sets the click listeners on it, adds it to the HashMap and adds it to the row.
     *
     * @param numAddedParticipants the number of added participants
     * @param pixelSizeForRow the size for the row in pixels
     * @param row the row where the ImageView is added
     * @return
     */
    private int createAndAddParticipantView(int numAddedParticipants, int pixelSizeForRow, TableRow row) {
        // Create the ParticipantView
        ParticipantView participantView = new ParticipantView(context, numAddedParticipants);
        participantView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                pixelSizeForRow - SUBTRACT_TO_ROW_SIZE));
        // Set the margins of the ParticipantView
        setMargins(participantView, Constants.MARGIN_IMAGEVIEW_IN_TABLE_SIDES, Constants.MARGIN_IMAGEVIEW_IN_TABLE_BOTTOM,
                Constants.MARGIN_IMAGEVIEW_IN_TABLE_SIDES, Constants.MARGIN_IMAGEVIEW_IN_TABLE_BOTTOM);

        // Set its position in the list as tag, so it can be found afterwards
        participantView.setTag(numAddedParticipants);

        // Set the gesture detector
        final GestureDetector gdt = new GestureDetector(new MyGestureDetector(participantView));
        participantView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                gdt.onTouchEvent(event);
                return true;
            }
        });

        // Create and add the participant to the List
        mParticipants.add(createParticipant(numAddedParticipants));

        // Add it to the Hashmap
        mIdAndViewHashMap.put(new ParticipantID(numAddedParticipants), participantView);

        row.addView(participantView);

        numAddedParticipants++;
        return numAddedParticipants;
    }

    /**
     * Sets the margins of the View passed as parameter, in pixels.
     *
     * @param v the view whose margins are gonna be set
     * @param left the left margin size
     * @param top the top margin size
     * @param right  the right margin size
     * @param bottom the bottom margin size
     */
    public static void setMargins(View v, int left, int top, int right, int bottom) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            v.requestLayout();
        }
    }

    /**
     * Calculates the padding between the rows of the table so all the space in the screen is taken (that means,
     * the first row is at the top and the last row at the bottom).
     *
     * @param numRows the number of rows of the table
     * @return the padding between the rows so all the space in the screen is taken
     */
    private int calculatePaddingBetweenRows(int numRows) {
        // Get the height of the window
        int windowHeight = Utils.getWindowHeight(this);

        // Subtract to the windows height the different bars
        windowHeight = windowHeight
                -Utils.getActionBarHeight(this)
                -Utils.getNavigationBarHeight(this)
                -Utils.getStatusBarHeight(this)
                -TOP_MARGIN_OF_TABLE;

        return windowHeight/numRows;
    }

    /**
     * Creates a Participant whose name is its position in the list of participants mParticipants converted to String
     * and the different time statistics the current time.
     *
     * @param num the position of the Participant in the list of participants
     * @return the created Participant
     */
    private Participant createParticipant(int num) {
        return new Participant.Builder(new ParticipantID(num)).name("Num"+num).build();
    }

    ///////////////////////////////////////////////////////////
    // ON CLICK LISTENERS AND RELATED METHODS
    ///////////////////////////////////////////////////////////

    /**
     * Give the turn to a participant or puts their in the waiting list
     */
    private View.OnClickListener mOnModeratorClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mDebateHasEnded) {
                // Go to Results Activity
                Intent goToResultsIntent = new Intent(context, ResultsActivity.class);
                goToResultsIntent.putExtra(Constants.EXTRA_LIST_PARTICIPANTS, (ArrayList) mParticipants);
                startActivity(goToResultsIntent);
            } else if (!mDebateHasStarted) {
                // Start the timer of the debate
                startTimer(DEBATE_TOTAL_TIME_TIMER);
            } else {
                // Show remaining time
                long remainingMinutes = mScheduleFutureDebate.getDelay(TimeUnit.SECONDS);
                Toast.makeText(context, new InterventionTime(remainingMinutes).toString() + getString(R.string.moderation_toast_remaining_debate_time), Toast.LENGTH_SHORT).show();
            }
        }
    };

    ///////////////////////////////////////////////////////////
    // TIMER RELATED
    ///////////////////////////////////////////////////////////

    /**
     * The runnable that is called when the time for the speaker is up.
     */
    private Runnable mInterventionTimeEndedRunnable = new Runnable() {
        public void run() {
            try {
                makeDeviceVibrate();
                makeDeviceBeep();

                // Add the time of their intervention to their profile
                mCurrentParticipant.addTime(mParticipantTimeLimit);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // Reset the view
                        //resetParticipantView(mIdAndViewHashMap.get(mCurrentParticipant.getId()));
                        Toast.makeText(context, getString(R.string.moderation_toast_intervention_time_ended), Toast.LENGTH_SHORT).show();

                        //mCurrentParticipant = null;
                    }
                });
                startTimer(PARTICIPANT_INTERVENTION_TIMER);
            }catch (Exception e){
                Log.e(TAG, "Error. Most likely due to the use of ScheduledExecutorService." );
                e.printStackTrace();
            }
        }
    };

    /**
     * The runnable that is called when the time of the debate is up.
     */
    private Runnable mDebateTimeEndedRunnable = new Runnable() {
        public void run() {
            mDebateHasEnded = true;
            mDebateHasStarted = false;

            makeDeviceVibrate();
            makeDeviceBeep();

            try{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getString(R.string.moderation_toast_debate_time_ended), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error. Most likely due to the use of ScheduledExecutorService.");
                e.printStackTrace();
            }
        }
    };

    /**
     * Starts the timer
     */
    private void startTimer(int timerType) {
        final ScheduledExecutorService mScheduledTaskExecutor = Executors.newScheduledThreadPool(1);
        switch (timerType){
            case PARTICIPANT_INTERVENTION_TIMER:
                mScheduleFutureIntervention = mScheduledTaskExecutor.schedule(mInterventionTimeEndedRunnable, mParticipantTimeLimit, TimeUnit.SECONDS);
                break;
            case DEBATE_TOTAL_TIME_TIMER:
                Toast.makeText(context, getString(R.string.moderation_toast_debate_timer_start), Toast.LENGTH_SHORT).show();
                mScheduleFutureDebate = mScheduledTaskExecutor.schedule(mDebateTimeEndedRunnable, mDebateTimeLimit, TimeUnit.SECONDS);
                mDebateHasStarted = true;
                break;
        }
    }

    ///////////////////////////////////////////////////////////
    // CURRENT-PARTICIPANT RELATED
    ///////////////////////////////////////////////////////////

    /**
     * Gives the turn to the participant passed by parameter and starts the timer. Changes its image to speaking.
     *
     * @param participant the participant that will receive the speaking turn
     */
    private void assignSpeakingTurn(Participant participant) {
        mCurrentParticipant = participant;
        Toast.makeText(context, getString(R.string.moderation_toast_assign_turn_to_participant) + mCurrentParticipant.toString(), Toast.LENGTH_SHORT).show();

        ParticipantView pView = mIdAndViewHashMap.get(participant.getId());
        pView.setWaitingListPos("");
        pView.showWaitingListPos();
        pView.setParticipantImage((R.drawable.participant_speaking_selector));

        startTimer(PARTICIPANT_INTERVENTION_TIMER);
    }

    /**
     * The current participant finishes their intervention and therefore the timer is removed and the turn given to the
     * first person in the waiting list
     */
    private void participantFinishedTheirIntervention() {
        // Add the time of their intervention to their profile
        long remainingTimeFromTimer = mScheduleFutureIntervention.getDelay(TimeUnit.SECONDS);
        mCurrentParticipant.addTimeAndIntervention(mParticipantTimeLimit - remainingTimeFromTimer);

        // Cancel the timer
        mScheduleFutureIntervention.cancel(true);

        Toast.makeText(context, mCurrentParticipant.toString() + getString(R.string.moderation_toast_participant_finished_intervention), Toast.LENGTH_SHORT).show();

        // Reset the view
        resetParticipantView(mIdAndViewHashMap.get(mCurrentParticipant.getId()));

        mCurrentParticipant = null;
    }

    ///////////////////////////////////////////////////////////
    // WAITING LIST
    ///////////////////////////////////////////////////////////

    /**
     * Puts the participant in the waiting list and changes its image to waiting in list
     *
     * @param participant the ParticipantView to be put in the waiting list
     */
    private void putInWaitingList(ParticipantView participant) {
        Toast.makeText(context, participant.getParticipantName() + getResources().getQuantityString(R.plurals.moderation_toast_participant_added_to_queue, mWaitingList.size(), mWaitingList.size()), Toast.LENGTH_SHORT).show();
        mWaitingList.add(participant);

        participant.setParticipantImage(R.drawable.participant_waiting_selector);

        // Update the waiting list
        updateWaitingListView();
    }

    /**
     * Removes the participant from the waiting list
     *
     * @param participantView the ParticipantView to be removed from the waiting list
     */
    private void removeFromWaitingList(ParticipantView participantView) {
        // Update the view of the participant that is about to be removed
        resetParticipantView(participantView);

        mWaitingList.remove(participantView);
        Toast.makeText(context, participantView.getParticipantName() + getResources().getQuantityString(R.plurals.moderation_toast_remove_participant_from_queue, mWaitingList.size(), mWaitingList.size()), Toast.LENGTH_SHORT).show();

        // Update the waiting list
        updateWaitingListView();
    }

    ///////////////////////////////////////////////////////////
    // HELPING METHODS
    ///////////////////////////////////////////////////////////

    private boolean isTheParticipantIdInTheWaitingList(ParticipantID participantId){
        return mWaitingList.contains(mIdAndViewHashMap.get(participantId));
    }

    private boolean isTheWaitingListEmpty(){
        return mWaitingList.size() == 0;
    }

    private boolean isTheParticipantTalking(ParticipantID participantId){
        if(mCurrentParticipant != null) {
            return participantId.equals(mCurrentParticipant.getId());
        }
        return false; // no one is talking
    }

    /**
     * Resets and hides the position of the waiting list from the ParticipantView of the currentParticipant and changes
     * its image to listening.
     */
    private void resetParticipantView(ParticipantView pView) {
        pView.setWaitingListPos("");
        pView.hideWaitingListPos();

        pView.setParticipantImage(R.drawable.participant_listening_selector);
    }

    /**
     * Updates the index of all the ParticipantView in the waiting list
     */
    private void updateWaitingListView() {
        int i = 1;
        for (ParticipantView participantView : mWaitingList) {
            participantView.setWaitingListPos(Integer.toString(i++));
            participantView.showWaitingListPos();
        }
    }

    private void makeDeviceBeep() {
        // Make the device beep
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2000);
    }

    private void makeDeviceVibrate() {
        // Make the device vibrate
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(DEVICE_VIBRATION_IN_MILLISECONDS);
    }

    ///////////////////////////////////////////////////////////
    // POPUP / DIALOG IMPLEMENTATION
    ///////////////////////////////////////////////////////////

    /**
     * Creates and shows a ParticipantStatisticsDialogFragment with the statistics of the participant and allowing to
     * change the name of their.
     * @param participant the participant whose name can be changed and statistics seen.
     */
    public void showParticipantStatisticsDialog(Participant participant) {
        // Create an instance of the dialog fragment and show it
        ParticipantStatisticsDialogFragment dialog = new ParticipantStatisticsDialogFragment();

        // Give the dialog the reference to the Participant
        Bundle args = new Bundle();
        args.putParcelable(Constants.KEY_ARG_PARTICIPANT, participant);
        dialog.setArguments(args);

        dialog.show(getSupportFragmentManager(), "ParticipantStatisticsDialogFragment");
    }

    /**
     * Changes the name of the double clicked participant mEditedParticipant to the one set by the user in the dialog
     * @param dialogFragment the fragment containing the EditText with the new name for the participant
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {
        Dialog dialog = dialogFragment.getDialog();
        EditText inputTemp = (EditText) dialog.findViewById(R.id.participant_popup_name_editText);
        String newName = inputTemp.getText().toString();
        mEditedParticipant.setName(newName);
        mEditedParticipantView.setParticipantName(newName);
    }

    ///////////////////////////////////////////////////////////
    // MY GESTURE DETECTOR CLASS
    ///////////////////////////////////////////////////////////

    /**
     * Small class to handle the click, long click and double click of the participantViews
     */
    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        // The ParticipantView that was touched
        ParticipantView mTouchedParticipantView;

        public MyGestureDetector(ParticipantView tpv){
            mTouchedParticipantView = tpv;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            // Needs to return true for the other methods to work
            return true;
        }

        /**
         * Takes the turn from the speaking Participant or removes it from the waiting list
         */
        @Override
        public void onLongPress(MotionEvent event) {
            Participant clickedParticipant = mParticipants.get((int) mTouchedParticipantView.getTag());
            ParticipantID clickParticipantID = clickedParticipant.getId();

            if (isTheParticipantTalking(clickParticipantID)) { // Clicked on talking person
                participantFinishedTheirIntervention();
            } else {
                if (isTheParticipantIdInTheWaitingList(clickParticipantID)) {
                    removeFromWaitingList((ParticipantView) mTouchedParticipantView);
                }
            }
        }

        /**
         *  Opens a ParticipantStatisticsDialog
          */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // Mark the participant as the edited one
            mEditedParticipantView = mTouchedParticipantView;
            mEditedParticipant = mParticipants.get((int) mTouchedParticipantView.getTag());

            showParticipantStatisticsDialog(mEditedParticipant);
            return true;
        }

        /**
         * Give the turn to a participant or puts their in the waiting list
         */
        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            Participant clickedParticipant = mParticipants.get((int) mTouchedParticipantView.getTag());
            ParticipantID clickParticipantID = clickedParticipant.getId();

            // No one is talking
            if (mCurrentParticipant == null) {
                if (isTheWaitingListEmpty()) {
                    assignSpeakingTurn(clickedParticipant);
                } else {
                    if (isTheParticipantIdInTheWaitingList(clickParticipantID)) {
                        removeFromWaitingList((ParticipantView) mTouchedParticipantView);
                        assignSpeakingTurn(clickedParticipant);
                    } else {
                        putInWaitingList((ParticipantView) mTouchedParticipantView);
                    }
                }
            } else { // Someone talks
                if (!isTheParticipantTalking(clickParticipantID)) {
                    // Clicked on someone not talking
                    if (!isTheParticipantIdInTheWaitingList(clickParticipantID)) {
                        putInWaitingList((ParticipantView) mTouchedParticipantView);
                    }
                }
            }
            return false;
        }
    }
}
