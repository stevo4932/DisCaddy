package com.discaddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class Player extends Activity {
    //private DisCaddyDbAdapter mDbHelper;
    private static String TAG = "Player";
    DisCaddyDbAdapter mDbHelper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);
        //one mDbHelper for the class. (should help with locking).
        mDbHelper = new DisCaddyDbAdapter(this);
        mDbHelper.open();
        new LoadPlayers().execute();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mDbHelper.close();
    }

    /*@Override*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.players, menu);
        return true;
    }

    /*
    * @Override
    * Used only when no list items exist
    * Displays no_players string
    */
    public void onContentChanged() {
        super.onContentChanged();
        View empty = findViewById(R.id.empty);
        ListView list = (ListView) findViewById(R.id.players_list);
        list.setEmptyView(empty);
    }


    //@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add_player) {
            Intent myIntent = new Intent(Player.this, PlayerNewEdit.class);
            Player.this.startActivity(myIntent);
        }
        else if (id == R.id.action_help_player) {
            new AlertDialog.Builder(this)
                    .setTitle("Help")
                    .setMessage("Press a players name in the list to go to their profile.\n" +
                            "\nPress the add button to add a new player.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {}
                    })
                    .show();
        }
        else{
            Log.v(TAG, "Bad selection");
        }
        return super.onOptionsItemSelected(item);
    }

    //wraps database access so that it is not done in the GUI thread.
    private class LoadPlayers extends AsyncTask<Void, Object, Cursor> {

        // perform the database access
        @Override
        protected Cursor doInBackground(Void... params) {

            // get a cursor containing all data on given entry
            return mDbHelper.fetchAllPlayers();
        }


        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor c) {
            super.onPostExecute(c);
            c.moveToFirst();
            startManagingCursor(c);

            String[] from = new String[] { DisCaddyDbAdapter.KEY_NAME};
            int[] to = new int[] { R.id.text1 };

            // Now create an array adapter and set it to display using our row
            SimpleCursorAdapter players =
                    new SimpleCursorAdapter(Player.this, R.layout.list_row, c, from, to);
            ListView playerList = (ListView)findViewById(R.id.players_list);
            playerList.setAdapter(players);
            playerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    Intent myIntent = new Intent(Player.this, PlayerProfile.class);
                    myIntent.putExtra("id", id);
                    Player.this.startActivity(myIntent);
                }
            });
        }
    }
}

