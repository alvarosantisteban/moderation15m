package com.alvarosantisteban.moderacion15m;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.alvarosantisteban.moderacion15m.util.Constants;

/**
 * This activity allows the moderator to configure the moderation; set the number of columns, number of participants.
 * @author Alvaro Santisteban 17.12.14 - alvarosantisteban@gmail.com
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    // Minimum number of accepted columns
    public static final int MIN_NUM_COLUMNS = 2;

    EditText mEditTextColumns;
    EditText mEditTextTotal;
    EditText mEditTextNumSec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextColumns = (EditText)findViewById(R.id.main_textedit_num_columns);
        mEditTextTotal = (EditText) findViewById(R.id.main_textedit_total_num_participants);
        mEditTextNumSec = (EditText) findViewById(R.id.main_textEdit_maximum_time_intervention);
    }

    /**
     * Gets the parameters from the EditTexts and if they are valid, goes to the ModerationActivity.
     * @param v the clicked View
     */
    public void createCircle(View v) {
        int numColumns = getIntFromEditText(mEditTextColumns);
        int numParticipants = getIntFromEditText(mEditTextTotal);
        int numSeconds = getIntFromEditText(mEditTextNumSec) * 60;

        if (areParamsCorrect(numColumns, numParticipants)){
            // Create the intent
            Intent goToModerationIntent = new Intent(this, ModerationActivity.class);

            // Put the extras
            goToModerationIntent.putExtra(Constants.EXTRA_NUM_COLUMNS, numColumns);
            goToModerationIntent.putExtra(Constants.EXTRA_NUM_PARTICIPANTS, numParticipants);
            if(numSeconds > 0){
                goToModerationIntent.putExtra(Constants.EXTRA_MAX_NUM_SEC_PARTICIPATION, numSeconds);
            }

            // Go to the moderation activity
            startActivity(goToModerationIntent);
        } else{
            Toast.makeText(this, getString(R.string.main_activity_incorrect_params), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Returns the int from the EditText or -1 if there was a NumberFormatException
     *
     * @param theEditText the EditText with the int
     * @return the int contained in the EditText or -1 if there was a NumberFormatException
     */
    private int getIntFromEditText(EditText theEditText) throws NumberFormatException{
        try {
            return Integer.parseInt(theEditText.getText().toString());
        } catch (NumberFormatException exception) {
            Log.e(TAG, "Problem with the parameter: " + theEditText.getText().toString());
            exception.printStackTrace();
            return -1;
        }
    }

    /**
     *
     * Checks the validity of the parameters.
     * The parameters are considered correct if:
     *  - There are at least two columns
     *  - The number of participants is at least equal to the number of columns
     *
     * @param numColumns the number of columns
     * @param numParticipants the number of participants
     * @return true if the params are correct, false otherwise.
     */
    private boolean areParamsCorrect(int numColumns, int numParticipants) {
        if (numColumns >= MIN_NUM_COLUMNS && numParticipants >= numColumns){
            return true;
        }
        return false;
    }

    @Override
    protected void onResume(){
        super.onResume();

        // Make the softkeyboard appear and focuss it on the columns EditText
        mEditTextColumns.requestFocus();
        mEditTextColumns.postDelayed(new Runnable() {

            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(mEditTextColumns, 0);
            }
        }, 200);
    }

    ////////////////////////////////////////////////////////////////
    // MENU RELATED
    ////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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