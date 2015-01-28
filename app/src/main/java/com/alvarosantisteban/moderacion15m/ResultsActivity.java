package com.alvarosantisteban.moderacion15m;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.alvarosantisteban.moderacion15m.model.Participant;
import com.alvarosantisteban.moderacion15m.util.Constants;

import java.util.ArrayList;

public class ResultsActivity extends Activity {

    ListView mParticipantsListView;
    ArrayAdapter<Participant> mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        mParticipantsListView = (ListView) findViewById(R.id.results_list);

        ArrayList<Participant> participants = getIntent().getParcelableArrayListExtra(Constants.EXTRA_LIST_PARTICIPANTS);

        mListAdapter = new ArrayAdapter<Participant>(this, android.R.layout.simple_list_item_1, participants);
        mParticipantsListView.setAdapter(mListAdapter);
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
