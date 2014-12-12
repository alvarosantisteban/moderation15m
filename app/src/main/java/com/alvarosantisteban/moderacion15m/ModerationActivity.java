package com.alvarosantisteban.moderacion15m;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alvarosantisteban.moderacion15m.model.Participant;
import com.alvarosantisteban.moderacion15m.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class ModerationActivity extends Activity {

    Participant mCurrentParticipant;
    List<Participant> mParticipants = new ArrayList<Participant>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderation);

        // Create participants
        createParticipantsList();
    }

    private void createParticipantsList() {
        for (int i=0; i < Constants.NUM_PARTICIPANTS; i++){
            mParticipants.add(createFakeParticipant(i));
        }
    }

    private Participant createFakeParticipant(int num) {
        return new Participant(String.valueOf(num), 0, num*2, num*1, true);
    }

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
    }

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
