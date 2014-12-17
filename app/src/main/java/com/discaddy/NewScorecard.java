package com.discaddy;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NewScorecard extends Activity {

    private DisCaddyDbAdapter mDbHelper;
    private static final String TAG = "NewScorecard";
    private ArrayList<String> player_list;

    private final String LOG_TAG = "NewScorecard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_scorecard);

        player_list = new ArrayList<String>();
        mDbHelper = new DisCaddyDbAdapter(this);
        mDbHelper.open();
        //fillData();
    }

    //resets player list and textview's background colors.
    @Override
    protected void onResume(){
        super.onResume();
        player_list.clear();
        fillData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_scorecard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_add_player:
                addNewPlayer();
                break;
            case R.id.action_next:
                next();
                break;
            default:
                Log.v(TAG, "Bad selection");
        }
        return super.onOptionsItemSelected(item);
    }




    public void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor c = this.mDbHelper.fetchAllPlayers();
        startManagingCursor(c);

        String[] from = new String[] { DisCaddyDbAdapter.KEY_NAME};
        int[] to = new int[] { R.id.text1 };

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter players =
                new SimpleCursorAdapter(this, R.layout.list_row, c, from, to);
        final ListView playerList = (ListView)findViewById(R.id.players_list);
        playerList.setAdapter(players);

        playerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                //get selected textview and name string.
                TextView tv = (TextView) view;
                String name = tv.getText().toString();

                //If selected name does not exist, add it and change color to white.
                //If selected name exists, remove it and change color to clear
                if(!player_list.contains(name)){
                    player_list.add(name);
//                    tv.setBackgroundColor(Color.argb(125, 75, 236, 90));
                    tv.setBackgroundColor(Color.argb(255, 255, 255, 255));
                }else{
                    player_list.remove(name);
                    tv.setBackgroundColor(Color.argb(0, 255, 255, 255));
                }
            }
        });
    }

    private void addNewPlayer() {
        Intent myIntent = new Intent(NewScorecard.this, PlayerNewEdit.class);
        NewScorecard.this.startActivity(myIntent);
    }

    public void next() {
        //players_list should contain all players. if not let user know and do nothing.
        if(player_list.isEmpty()){
            Toast.makeText(this, "Select at least one player to start!", Toast.LENGTH_SHORT).show();
            return;
        }
        //change player_list arraylist to a string array.
        String[] playerString = new String[player_list.size()];
        player_list.toArray(playerString);

        //send user and the player's list to the select course stage.
        Intent myIntent = new Intent(NewScorecard.this, ScorecardSelectCourse.class);
        myIntent.putExtra("playerString", playerString);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        NewScorecard.this.startActivity(myIntent);


    }
}
