package com.alvarosantisteban.moderacion15m;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.alvarosantisteban.moderacion15m.util.Constants;

/**
 * @author Alvaro Santisteban 17.12.14 - alvarosantisteban@gmail.com
 */
public class MainActivity extends Activity {

    EditText mEditTextColumns;
    EditText mEditTextTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextColumns = (EditText)findViewById(R.id.main_textedit_num_columns);
        mEditTextTotal = (EditText) findViewById(R.id.main_textedit_total_num_participants);
    }

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

    public void createCircle(View v){
        int numColums = getIntFromEditText(mEditTextColumns);
        int numParticipants = getIntFromEditText(mEditTextTotal);
        if (areParamsCorrect(numColums, numParticipants)){
            // Create the intent
            Intent goToModerationIntent = new Intent(this, ModerationActivity.class);

            // Put the extras
            goToModerationIntent.putExtra(Constants.EXTRA_NUM_COLUMNS, numColums);
            goToModerationIntent.putExtra(Constants.EXTRA_NUM_PARTICIPANTS, numParticipants);

            // Go to the moderation activity
            startActivity(goToModerationIntent);
        }
    }

    /**
     * Returns the int from the EditText
     *
     * @param theEditText
     * @return
     */
    private int getIntFromEditText(EditText theEditText) {
        return Integer.parseInt(theEditText.getText().toString());
    }

    /**
     * The parameters are considered correct if none of them are zero
     */
    private boolean areParamsCorrect(int numColums, int numParticipants) {
        if (numColums != 0 && numParticipants != 0){
            return true;
        }
        return false;
    }
}
