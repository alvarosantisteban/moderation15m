package com.alvarosantisteban.moderacion15m;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.alvarosantisteban.moderacion15m.model.Participant;
import com.alvarosantisteban.moderacion15m.util.Constants;
import com.alvarosantisteban.moderacion15m.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alvaro Santisteban 13.11.14 - alvarosantisteban@gmail.com
 */
public class ModerationActivity extends Activity {

    private static final String TAG = "ModerationActivity";

    TableLayout tableLayoutOfParticipants;

    List<Participant> mParticipants = new ArrayList<Participant>();
    Participant mCurrentParticipant;

    int mNumColumns;
    int mNumParticipants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderation);

        tableLayoutOfParticipants = (TableLayout) findViewById(R.id.participants_table);

        // Create participants
        //createParticipantsList();

        // Get the intent to obtain its extras
        Intent intentFromMain = getIntent();
        mNumColumns = intentFromMain.getIntExtra(Constants.EXTRA_NUM_COLUMNS, 0);
        mNumParticipants = intentFromMain.getIntExtra(Constants.EXTRA_NUM_PARTICIPANTS, 0);

        int numRows = calculateNumOfRows(mNumColumns, mNumParticipants);

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
        // By making it an integer, we ensure that it will be the right number
        int firstRowMiddleParticipants = (numColumns-2)/2;
        return (numParticipants/2)-firstRowMiddleParticipants;
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
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            //row.setBackgroundColor(Color.BLUE);
            // Create columns
            for (int j = 1; j <= cols; j++) {
                // Add participant if first or last column or first row
                TextView tv = new TextView(this);
                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                tv.measure(0, 0);       //must call measure!
                int textViewHeightSize = tv.getMeasuredHeight();  //get height
                if(i>1) {
                    tv.setPadding(Constants.PADDING_TABLE_SIDES, pixelSizeForRow - textViewHeightSize,
                            Constants.PADDING_TABLE_SIDES, Constants.PADDING_TABLE_BOTTOM);
                }else{
                    tv.setPadding(Constants.PADDING_TABLE_SIDES, Constants.PADDING_TABLE_TOP,
                            Constants.PADDING_TABLE_SIDES, Constants.PADDING_TABLE_BOTTOM);
                }


                if ((numAddedParticipants < mNumParticipants) && (i == 1 || j == 1 || j == cols)) {

                    tv.setText("R " + i + ", C" + j);

                    row.addView(tv);

                    numAddedParticipants++;
                } else{
                    tv.setText("");

                    row.addView(tv);
                }
            }
            tableLayoutOfParticipants.addView(row);
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
        int windowHeight = Utils.getWindowHeight(this);
        windowHeight = windowHeight
                -Utils.getActionBarHeight(this)
                -Utils.getNavigationBarHeight(this)
                -Utils.getStatusBarHeight(this);
        return windowHeight/numRows;
    }

    private void createParticipantsList() {
        for (int i=0; i < Constants.NUM_PARTICIPANTS; i++){
            mParticipants.add(createFakeParticipant(i));
        }
    }

    private Participant createFakeParticipant(int num) {
        return new Participant(String.valueOf(num), 0, num*2, num*1, true);
    }

    /*
    public void startTimer(View v){
        String message;
        switch(v.getId()){
            case R.id.button_topLeft:
                message = "Soy el boton de arriba a la izq";
                break;
            case R.id.button_topCenter:
                message = "Soy el boton de arriba en el centro";
                break;
            case R.id.button_topRight:
                message = "Soy el boton de arriba a la derecha";
                break;
            default:
                message = "Otro";
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }  */

    ///////////////////////////////////////////////////////////
    // MENU
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
