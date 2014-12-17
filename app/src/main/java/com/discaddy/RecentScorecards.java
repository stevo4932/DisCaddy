package com.discaddy;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class RecentScorecards extends Activity {
    //private DisCaddyDbAdapter mDbHelperScore;
    private static final String TAG = "RecentScorecards";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_scorecards);
        new LoadScorecards().execute();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        View empty = findViewById(R.id.empty);
        ListView list = (ListView) findViewById(R.id.score_card_list);
        list.setEmptyView(empty);
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recent_scorecards, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            //noinspection SimplifiableIfStatement
            case R.id.action_help_recent_scorecard:
                return true;
            default:
                Log.v(TAG, "BAD SELECTION");
        }
        return super.onOptionsItemSelected(item);
    }*/

    //wraps database access so that it is not done in the GUI thread.
    private class LoadScorecards extends AsyncTask<Void, Void, Cursor> {

     private DisCaddyDbAdapter mDbHelperScore =
                new DisCaddyDbAdapter(RecentScorecards.this);

        // perform the database access
        @Override
        protected Cursor doInBackground(Void... params) {
            mDbHelperScore.open();
            // get a cursor containing all data on given entry
            return mDbHelperScore.fetchAllScorecards();
        }


        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor c) {
            super.onPostExecute(c);
            c.moveToFirst();
            startManagingCursor(c);

            String[] from = new String[] { DisCaddyDbAdapter.KEY_CARD_NAME};
            int[] to = new int[] { R.id.text1 };

            // Now create an array adapter and set it to display using our row
            SimpleCursorAdapter scorecards =
                    new SimpleCursorAdapter(RecentScorecards.this, R.layout.list_row, c, from, to);
            ListView scorecardList = (ListView)findViewById(R.id.score_card_list);
            scorecardList.setAdapter(scorecards);
            scorecardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    //Log.v(TAG, "id: "+id);
                    Intent myIntent = new Intent(RecentScorecards.this,ScorecardViewer.class);
                    myIntent.putExtra("id", id);
                    RecentScorecards.this.startActivity(myIntent);
                }
            });
        }
    }


}
