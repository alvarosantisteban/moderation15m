package com.alvarosantisteban.moderacion15m;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.alvarosantisteban.moderacion15m.model.Participant;
import com.alvarosantisteban.moderacion15m.model.ParticipantView;
import com.alvarosantisteban.moderacion15m.util.Constants;
import com.alvarosantisteban.moderacion15m.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity allows the moderator to interact with the table of participants created.
 * @author Alvaro Santisteban 13.11.14 - alvarosantisteban@gmail.com
 */
public class ModerationActivity extends Activity {

    private static final String TAG = "ModerationActivity";

    // The pixels that are subtracted to the size of a row so the image in it looks fine
    public static final int SUBTRACT_TO_ROW_SIZE = 10;
    // The top margin defined in the layout of the table
    public static final int TOP_MARGIN_OF_TABLE = 10;
    public static final int DEFAULT_MAX_NUM_SEC_PARTICIPATION = 5;

    // The table layout with the views of the participants
    TableLayout tableLayoutOfParticipants;

    // The list of participants
    List<Participant> mParticipants = new ArrayList<Participant>();
    // The participant currently talking
    Participant mCurrentParticipant;

    // The waiting list of Participants identified by their ids
    List<String> mWaitingList = new ArrayList<String>();

    private Handler mHandler = new Handler();

    int mNumColumns;
    int mNumParticipants;
    private Context context;

    // The maximum number of seconds that a participant can talk before the timer runs out
    private int mTimeLimit;

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
        mTimeLimit = intentFromMain.getIntExtra(Constants.EXTRA_MAX_NUM_SEC_PARTICIPATION, DEFAULT_MAX_NUM_SEC_PARTICIPATION);
        Log.e(TAG, ": " +mTimeLimit);

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
        int extraRow = addExtraRow(numColumns, numParticipants);

        // By making it an integer, we ensure that it will be the right number
        int firstRowMiddleParticipants = numColumns-2;
        return ((numParticipants - firstRowMiddleParticipants)/2) + extraRow;
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

    /**
     * Creates the table layout with the participants
     *
     * @param rows the number of rows of the table
     * @param cols the number of columns of the table
     */
    private void buildTable(int rows, int cols) {
        int numAddedParticipants = 0;
        int pixelSizeForRow = calculatePaddingBetweenRows(rows);

        // Create rows
        for (int i = 1; i <= rows; i++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            // Create columns
            for (int j = 1; j <= cols; j++) {

                // Add participant if is the first or last column or is the first row
                if ((numAddedParticipants < mNumParticipants) && (i == 1 || j == 1 || j == cols)) {

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

                    row.addView(participantView);

                    numAddedParticipants++;
                } else {
                    // Add an empty image to the row
                    row.addView(new ImageView(context));
                }
            }
            // Add the row to the table
            tableLayoutOfParticipants.addView(row);
        }
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
        return new Participant(String.valueOf(num), new Time(), new Time(), new Time(), true);
    }

    ///////////////////////////////////////////////////////////
    // ON CLICK LISTENERS AND RELATED METHODS
    ///////////////////////////////////////////////////////////

    /**
     * Give the turn to a participant or puts their in the waiting list
     */
    private View.OnClickListener mOnParticipantClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Participant clickedParticipant = mParticipants.get((int) v.getTag());
            String clickParticipantID = clickedParticipant.getmName();

            // No one is talking
            if (mCurrentParticipant == null) {
                if (isTheWaitingListEmpty()) {
                    assignSpeakingTurn(clickedParticipant);
                } else {
                    if (isTheParticipantIdInTheWaitingList(clickParticipantID)) {
                        removeFromWaitingList(clickedParticipant);
                        assignSpeakingTurn(clickedParticipant);
                    } else{
                        putInWaitingList(clickedParticipant);
                    }
                }
            } else { // Someone talks
                if (isTheParticipantTalking(clickParticipantID)) { // Clicked on talking person
                    // TODO Show statistics?
                } else { // clicked on someone else
                    if (!isTheParticipantIdInTheWaitingList(clickParticipantID)) {
                        putInWaitingList(clickedParticipant);
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
            String clickParticipantID = clickedParticipant.getmName();

            if (isTheParticipantTalking(clickParticipantID)) { // Clicked on talking person
                participantFinishedTheirIntervention();
            } else {
                if (isTheParticipantIdInTheWaitingList(clickParticipantID)) {
                    removeFromWaitingList(clickedParticipant);
                }
            }
            return true;
        }
    };

    /**
     * The runnable that that is called when the time for the speaker is up.
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
        Toast.makeText(context, "The 5 seconds went through", Toast.LENGTH_SHORT).show();
        mCurrentParticipant = null;

        // TODO Change color of the image back to default
        }
    };

    /**
     * Starts the timer
     */
    private void startTimer() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, mTimeLimit * 1000);
    }

    /**
     * Gives the turn to the participant passed by parameter and starts the timer
     *
     * @param participant the participant that will receive the speaking turn
     */
    private void assignSpeakingTurn(Participant participant) {
        mCurrentParticipant = participant;
        Toast.makeText(context, "Assign the speaking turn to participant number " + mCurrentParticipant.getmName(), Toast.LENGTH_SHORT).show();

        // TODO Change color of the image to "talking status"

        startTimer();
    }

    /**
     * The current participant finishes their intervention and therefore the timer is removed and the turn given to the
     * first person in the waiting list
     */
    private void participantFinishedTheirIntervention() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        Toast.makeText(context, "The participant number " + mCurrentParticipant.getmName() + " finished their intervention", Toast.LENGTH_SHORT).show();
        mCurrentParticipant = null;

        // TODO Change color of the image back to default

        // TODO Change color of the first person in the waiting list to "blinking status"
    }

    /**
     * Puts the participant in the waiting list
     *
     * @param participant the Participant to be put in the waiting list
     */
    private void putInWaitingList(Participant participant) {
        Toast.makeText(context, "Participant added to the waiting list. Their had " + mWaitingList.size() + " persons ahead", Toast.LENGTH_SHORT).show();
        mWaitingList.add(participant.getmName());

        // TODO Change color of the image to "waiting status"
    }

    /**
     * Removes the participant from the waiting list
     *
     * @param participant the Participant to be removed from the waiting list
     */
    private void removeFromWaitingList(Participant participant) {
        mWaitingList.remove(participant.getmName());
        Toast.makeText(context, "Participant removed from the waiting list. There are " + mWaitingList.size() + " persons waiting", Toast.LENGTH_SHORT).show();

        // TODO Change color of the image back to default
    }

    ///////////////////////////////////////////////////////////
    // HELPING METHODS
    ///////////////////////////////////////////////////////////

    private boolean isTheParticipantIdInTheWaitingList(String participantId){
        return mWaitingList.contains(participantId);
    }

    private boolean isTheWaitingListEmpty(){
        return mWaitingList.size() == 0;
    }

    private boolean isTheParticipantTalking(String participantId){
        if(mCurrentParticipant != null) {
            return participantId.equals(mCurrentParticipant.getmName());
        }
        return false; // no one is talking
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
