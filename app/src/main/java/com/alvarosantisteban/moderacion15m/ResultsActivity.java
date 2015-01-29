package com.alvarosantisteban.moderacion15m;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.alvarosantisteban.moderacion15m.model.Participant;
import com.alvarosantisteban.moderacion15m.model.ResultsListAdapter;
import com.alvarosantisteban.moderacion15m.util.Constants;
import com.alvarosantisteban.moderacion15m.util.Utils;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * This activity allows the moderator to see the statistical results of the Participants and order them alphabetically,
 * by number of interventions or total time used in the interventions.
 *
 * @author Alvaro Santisteban 28.01.15 - alvarosantisteban@gmail.com
 */
public class ResultsActivity extends Activity {

    private Intent mShareIntent;
    private OutputStream os;



    ArrayList<Participant> mParticipants;

    ListView mParticipantsListView;
    ArrayAdapter<Participant> mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        mParticipantsListView = (ListView) findViewById(R.id.results_list);

        mParticipants = getIntent().getParcelableArrayListExtra(Constants.EXTRA_LIST_PARTICIPANTS);

        mListAdapter = new ResultsListAdapter(this, R.layout.list_item_results, mParticipants);
        mParticipantsListView.setAdapter(mListAdapter);

        // Order the list by number of interventions
        orderListByNumInterventions();
    }

    /**
     * Order the list mParticipants by the number of interventions of each Participant
     */
    public void orderListByNumInterventions(){
        Collections.sort(mParticipants, new Comparator<Participant>() {
            @Override
            public int compare(Participant participant1, Participant participant2) {
                return ((Long)participant2.getNumInterventions()).compareTo(participant1.getNumInterventions());
            }
        });
        mListAdapter.notifyDataSetChanged();
    }

    /**
     * Order the list mParticipants by the total time of interventions of each Participant
     */
    public void orderListByTimeOfInterventions() {
        Collections.sort(mParticipants, new Comparator<Participant>() {
            @Override
            public int compare(Participant participant1, Participant participant2) {
                return ((Long) participant2.getTotalInterventionsSecs()).compareTo(participant1.getTotalInterventionsSecs());
            }
        });
        mListAdapter.notifyDataSetChanged();
    }

    /**
     * Order the list mParticipants alphabetically using the name of each Participant
     */
    public void orderListAlphabetically() {
        Collections.sort(mParticipants, new Comparator<Participant>() {
            @Override
            public int compare(Participant participant1, Participant participant2) {
                return participant1.getName().compareToIgnoreCase(participant2.getName());
            }
        });
        mListAdapter.notifyDataSetChanged();
    }

    /**
     * Generates a statistics file in the SD card with the following format:
     *
     *  ParticipantName1 - ParticipantNumInterventions1 + ParticipantTotalTimeInterventions1
     *  ParticipantName2 - ParticipantNumInterventions2 + ParticipantTotalTimeInterventions2
     *  ...
     */
    private void generateStatisticsFile() {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        sb.append("Debate - " + dateFormat.format(Calendar.getInstance().getTime()));
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("Name - Num Interventions - Total time");
        sb.append(System.getProperty("line.separator"));
        for (Participant participant : mParticipants) {
            sb.append(participant.getName()
                    + " - "
                    + participant.getNumInterventions()
                    + " - "
                    + participant.getTotalInterventionsSecs()
                    + System.getProperty("line.separator"));
        }
        String result = sb.toString();
        Utils.writeToFile(result, Constants.FILE_NAME_STATISTICS);
    }

    ///////////////////////////////////////////////////////////
    // MENU RELATED
    ///////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_order_by_interventions) {
            orderListByNumInterventions();
            return true;
        } else if ( id == R.id.action_order_by_time) {
            orderListByTimeOfInterventions();
            return true;
        } else if (id == R.id.action_order_alphabetically) {
            orderListAlphabetically();
            return true;
        } else if (id == R.id.action_generate_file){
            generateStatisticsFile();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
