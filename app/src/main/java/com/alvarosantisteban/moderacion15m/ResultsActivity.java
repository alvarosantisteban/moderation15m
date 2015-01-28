package com.alvarosantisteban.moderacion15m;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.alvarosantisteban.moderacion15m.model.Participant;
import com.alvarosantisteban.moderacion15m.model.ResultsListAdapter;
import com.alvarosantisteban.moderacion15m.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ResultsActivity extends Activity {

    ArrayList<Participant> mParticipants;

    ListView mParticipantsListView;
    ArrayAdapter<Participant> mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        mParticipantsListView = (ListView) findViewById(R.id.results_list);

        mParticipants = getIntent().getParcelableArrayListExtra(Constants.EXTRA_LIST_PARTICIPANTS);
        orderListByNumInterventions();
        //orderListAlphabetically();
        //orderListByTimeOfInterventions();

        mListAdapter = new ResultsListAdapter(this, R.layout.list_item_results, mParticipants);
        mParticipantsListView.setAdapter(mListAdapter);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
