package com.alvarosantisteban.moderacion15m;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.alvarosantisteban.moderacion15m.model.Participant;
import com.alvarosantisteban.moderacion15m.model.ParticipantID;
import com.alvarosantisteban.moderacion15m.model.ParticipantView;
import com.alvarosantisteban.moderacion15m.util.Constants;
import com.alvarosantisteban.moderacion15m.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This activity allows the moderator to interact with the table of participants created.
 * @author Alvaro Santisteban 13.11.14 - alvarosantisteban@gmail.com
 */
public class ModerationActivity extends Activity {

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

    // The table layout with the views of the participants
    TableLayout tableLayoutOfParticipants;

    // The list of participants
    List<Participant> mParticipants = new ArrayList<Participant>();
    // The participant currently talking
    Participant mCurrentParticipant;

    // The waiting list of Participants identified by their ids
    List<ParticipantView> mWaitingList = new ArrayList<ParticipantView>();
    // A HasMap that connects the id and the ParticipantView
    Map<ParticipantID, ParticipantView> mIdAndViewHashMap = new HashMap<ParticipantID, ParticipantView>();

    private Handler mHandler = new Handler();

    int mNumColumns;
    int mNumParticipants;
    private Context context;

    // The maximum number of seconds that a participant can talk before the timer runs out
    private int mParticipantTimeLimit;
    // The maximum number of seconds that the debate can last
    private int mDebateTimeLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderation);

        context = this;

        tableLayoutOfParticipants = (TableLayout) findViewById(R.id.participants_table);

        // Get the intent to obtain its extras
        Intent intentFromMain = getIntent();
        mNumColumns = intentFromMain.getIntExtra(Constants.EXTRA_NUM_COLUMNS, 0);
        mNumParticipants = intentFromMain.getIntExtra(Constants.EXTRA_NUM_PARTICIPANTS, 0);
        mParticipantTimeLimit = intentFromMain.getIntExtra(Constants.EXTRA_MAX_NUM_SEC_PARTICIPATION, DEFAULT_MAX_NUM_SEC_PARTICIPATION);
        mDebateTimeLimit = intentFromMain.getIntExtra(Constants.EXTRA_TOTAL_TIME_DEBATE_SECS, DEFAULT_MAX_NUM_SEC_DEBATE);

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

        moderatorImage.setImageResource(R.drawable.btn_moderator);
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

        // Set the click listener
        participantView.setOnClickListener(mOnParticipantClickListener);
        // Set the long click listener
        participantView.setOnLongClickListener(mOnParticipantLongClickListener);

        // Create and add the participant to the List
        mParticipants.add(createFakeParticipant(numAddedParticipants));

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

        //TODO Control if the navigation bar is at the right side, instead of the bottom

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
    private Participant createFakeParticipant(int num) {
        return new Participant.Builder(new ParticipantID(num)).build();
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
            // Re-starts or pauses the timer for the debate
            Toast.makeText(context,"The timer for the debate starts", Toast.LENGTH_SHORT).show();
            startTimer(DEBATE_TOTAL_TIME_TIMER);
        }
    };


    /**
     * Give the turn to a participant or puts their in the waiting list
     */
    private View.OnClickListener mOnParticipantClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Participant clickedParticipant = mParticipants.get((int) v.getTag());
            ParticipantID clickParticipantID = clickedParticipant.getId();

            // No one is talking
            if (mCurrentParticipant == null) {
                if (isTheWaitingListEmpty()) {
                    assignSpeakingTurn(clickedParticipant);
                } else {
                    if (isTheParticipantIdInTheWaitingList(clickParticipantID)) {
                        removeFromWaitingList((ParticipantView)v);
                        assignSpeakingTurn(clickedParticipant);
                    } else{
                        putInWaitingList((ParticipantView)v);
                    }
                }
            } else { // Someone talks
                if (isTheParticipantTalking(clickParticipantID)) { // Clicked on talking person
                    // TODO Show statistics?
                } else { // clicked on someone else
                    if (!isTheParticipantIdInTheWaitingList(clickParticipantID)) {
                        putInWaitingList((ParticipantView)v);
                    }
                }
            }
        }
    };

    /**
     * Takes the turn from the speaking Participant or removes it from the waiting list
     */
    private View.OnLongClickListener mOnParticipantLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Participant clickedParticipant = mParticipants.get((int) v.getTag());
            ParticipantID clickParticipantID = clickedParticipant.getId();

            if (isTheParticipantTalking(clickParticipantID)) { // Clicked on talking person
                participantFinishedTheirIntervention();
            } else {
                if (isTheParticipantIdInTheWaitingList(clickParticipantID)) {
                    removeFromWaitingList((ParticipantView)v);
                }
            }
            return true;
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

            // Make the device vibrate
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(DEVICE_VIBRATION_IN_MILLISECONDS);

            // Make the device beep
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2000);

            // Reset the view
            resetParticipantView();

            Toast.makeText(context, "The " + mParticipantTimeLimit +" seconds went through", Toast.LENGTH_SHORT).show();

            // Add the time of their intervention to their profile
            mCurrentParticipant.addTime(mParticipantTimeLimit);

            mCurrentParticipant = null;

            // TODO Change color of the image back to default
        }
    };

    /**
     * The runnable that is called when the time of the debate is up.
     */
    private Runnable mDebateTimeEndedRunnable = new Runnable() {
        public void run() {

            //
            Toast.makeText(context, "The time for the debate ended", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * Starts the timer
     */
    private void startTimer(int timerType) {
        switch (timerType){
            case PARTICIPANT_INTERVENTION_TIMER:
                mHandler.removeCallbacks(mInterventionTimeEndedRunnable);
                mHandler.postDelayed(mInterventionTimeEndedRunnable, mParticipantTimeLimit * 1000);
                break;
            case DEBATE_TOTAL_TIME_TIMER:
                mHandler.removeCallbacks(mDebateTimeEndedRunnable);
                mHandler.postDelayed(mDebateTimeEndedRunnable, mDebateTimeLimit * 1000);
                break;
        }
    }

    ///////////////////////////////////////////////////////////
    // CURRENT PARTICIPANT RELATED
    ///////////////////////////////////////////////////////////

    /**
     * Gives the turn to the participant passed by parameter and starts the timer
     *
     * @param participant the participant that will receive the speaking turn
     */
    private void assignSpeakingTurn(Participant participant) {
        mCurrentParticipant = participant;
        Toast.makeText(context, "Assign the speaking turn to participant number " + mCurrentParticipant.getId(), Toast.LENGTH_SHORT).show();

        // TODO Change color of the image to "talking status"

        ParticipantView pView = mIdAndViewHashMap.get(participant.getId());
        pView.setWaitingListPos("X");
        pView.showWaitingListPos();

        startTimer(PARTICIPANT_INTERVENTION_TIMER);

        Log.d(TAG, "-------------------- INTERVENTIONS: " +mCurrentParticipant.getNumInterventions() +"---- SECS: " +mCurrentParticipant.getTotalInterventionsSecs());
    }

    /**
     * The current participant finishes their intervention and therefore the timer is removed and the turn given to the
     * first person in the waiting list
     */
    private void participantFinishedTheirIntervention() {
        mHandler.removeCallbacks(mInterventionTimeEndedRunnable);
        Toast.makeText(context, "The participant number " + mCurrentParticipant.toString() + " finished their intervention", Toast.LENGTH_SHORT).show();

        // Reset the view
        resetParticipantView();

        // TODO Add the time of their intervention to their profile
        // mCurrentParticipant.addTime(remainingTimeFromTimer - PARTICIPANT_INTERVENTION_TIMER);

        mCurrentParticipant = null;

        // TODO Change color of the image back to default

        // TODO Change color of the first person in the waiting list to "blinking status"
    }

    ///////////////////////////////////////////////////////////
    // WAITING LIST
    ///////////////////////////////////////////////////////////

    /**
     * Puts the participant in the waiting list
     *
     * @param participant the ParticipantView to be put in the waiting list
     */
    private void putInWaitingList(ParticipantView participant) {
        Toast.makeText(context, "Participant added to the waiting list. Their had " + mWaitingList.size() + " persons ahead", Toast.LENGTH_SHORT).show();
        mWaitingList.add(participant);

        // TODO Change color of the image to "waiting status"

        // Update the waiting list
        updateWaitingListView();
    }

    /**
     * Removes the participant from the waiting list
     *
     * @param participant the ParticipantView to be removed from the waiting list
     */
    private void removeFromWaitingList(ParticipantView participant) {
        // Update the view of the participant that is about to be removed
        participant.setWaitingListPos("");
        participant.hideWaitingListPos();

        mWaitingList.remove(participant);
        Toast.makeText(context, "Participant removed from the waiting list. There are " + mWaitingList.size() + " persons waiting", Toast.LENGTH_SHORT).show();

        // TODO Change color of the image back to default

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
     * Resets and hides the position of the waiting list from the ParticipantView of the currentParticipant
     */
    private void resetParticipantView() {
        ParticipantView pView = mIdAndViewHashMap.get(mCurrentParticipant.getId());
        pView.setWaitingListPos("");
        pView.hideWaitingListPos();
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

    ///////////////////////////////////////////////////////////
    // MENU RELATED
    ///////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_moderation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
