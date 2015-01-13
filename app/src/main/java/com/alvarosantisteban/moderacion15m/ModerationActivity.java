package com.alvarosantisteban.moderacion15m;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

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

    public static final int SUBSTRACT_FOR_ROW_SIZE = 10;
    // The top margin defined in the layout of the table
    public static final int TOP_MARGIN_OF_TABLE = 10;

    TableLayout tableLayoutOfParticipants;

    List<Participant> mParticipants = new ArrayList<Participant>();
    Participant mCurrentParticipant;

    int mNumColumns;
    int mNumParticipants;
    private Context context;

    private View.OnClickListener mOnParticipantClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Participant participant = mParticipants.get((int)v.getTag());
            Toast.makeText(context, "Tocado el participante numero " + participant.getmName(), Toast.LENGTH_SHORT).show();
        }
    };
    private View.OnLongClickListener mOnParticipantLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(context, "Tocado y hundido!", Toast.LENGTH_SHORT).show();
            return false;
        }
    };

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
        //int extraRow = 0;
        int extraRow = addExtraRow(numColumns, numParticipants);

        // By making it an integer, we ensure that it will be the right number
        int firstRowMiddleParticipants = numColumns-2;
        return ((numParticipants - firstRowMiddleParticipants)/2) + extraRow;
    }

    /**
     * Checks if the adding of the two parameters produces an odd number, in which case a extra row is needed
     *
     * @param numColumns
     * @param numParticipants
     * @return 1 if a row must be added, 0 otherwise
     */
    private int addExtraRow(int numColumns, int numParticipants){
        // Check if the adding of the two parameters produces an odd number
        if (Utils.isOdd(numColumns + numParticipants)){
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

                // Create the imageview and set its size
                final ImageView participantImage = new ImageView(this);
                participantImage.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        pixelSizeForRow - SUBSTRACT_FOR_ROW_SIZE));

                // Set the margins
                if (i > 1) {
                    setMargins(participantImage, Constants.MARGIN_TABLE_SIDES, Constants.MARGIN_TABLE_BOTTOM,
                            Constants.MARGIN_TABLE_SIDES, Constants.MARGIN_TABLE_BOTTOM);
                } else {
                    setMargins(participantImage, Constants.MARGIN_TABLE_SIDES, Constants.MARGIN_TABLE_BOTTOM,
                            Constants.MARGIN_TABLE_SIDES, Constants.MARGIN_TABLE_BOTTOM);
                }

                // Add participant if first or last column or first row
                if ((numAddedParticipants < mNumParticipants) && (i == 1 || j == 1 || j == cols)) {
                    // Set the image
                    participantImage.setImageResource(R.drawable.btn_anonymous_participant);
                    participantImage.setTag(numAddedParticipants);

                    // Set the click listener
                    participantImage.setOnClickListener(mOnParticipantClickListener);
                    // Set the long click listener
                    participantImage.setOnLongClickListener(mOnParticipantLongClickListener);

                    // Create and add the participant to the list
                    mParticipants.add(createFakeParticipant(numAddedParticipants));

                    // Add the image to the row
                    row.addView(participantImage);

                    numAddedParticipants++;
                } else {
                    // Add an empty image to the row
                    row.addView(participantImage);
                }
            }
            // Add the row to the table
            tableLayoutOfParticipants.addView(row);
        }
    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
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
        int windowHeight = Utils.getWindowHeight(this);
        //TODO Control if the navigation bar is at the bottom
        windowHeight = windowHeight
                -Utils.getActionBarHeight(this)
                -Utils.getNavigationBarHeight(this)
                -Utils.getStatusBarHeight(this)
                -TOP_MARGIN_OF_TABLE;

        return windowHeight/numRows;
    }

    private Participant createFakeParticipant(int num) {
        return new Participant(String.valueOf(num), new Time(), new Time(), new Time(), true);
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
