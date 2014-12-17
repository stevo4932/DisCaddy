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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;


public class ScorecardViewer extends Activity implements View.OnClickListener{

    private static final String TAG = "ScorecardViewer";
    private String courseName;
    private String cardName;
    private HashMap<String, int[]> scores;
    private DisCaddyDbAdapter mDbHelper;
    private long cardId;
    protected int currentHole;

    Button nextHole, previousHole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_card);
        scores = new HashMap<String, int[]>();
        mDbHelper = new DisCaddyDbAdapter(ScorecardViewer.this);
        currentHole = 0;

        Intent myIntent = getIntent();
        cardId = myIntent.getLongExtra("id", -1);
        if(cardId == -1) {
            Log.v(TAG, "Could not get ID");
            finish();
        }

        nextHole = (Button)findViewById(R.id.next_hole_button);
        previousHole = (Button)findViewById(R.id.previous_hole_button);

        nextHole.setOnClickListener(this);
        previousHole.setOnClickListener(this);

        new LoadScorecard().execute(cardId);
        //nothing below this is reachable.

    }

    @Override
    public void onClick(View v) {
        if (v == nextHole) {
            handleSwipeLeftToRight();
        }
        if (v == previousHole) {
            handleSwipeRightToLeft();
        }
    }

    private void handleSwipeLeftToRight() {
        if(currentHole < 17) {
            currentHole++;
            fillData();
        }
    }

    private void handleSwipeRightToLeft() {
        if(currentHole > 0) {
            currentHole--;
            fillData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scorecard_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete_scorecard:
                deleteCard();
                break;
            default:
                Toast.makeText(this, "BAD SELECTION", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    /* display alert dialog box to check delete selection.
    * Deletes scorecard off UI thread then calls finish() if successful*/
    private void deleteCard(){

        new AlertDialog.Builder(this)
        .setTitle("Delete Scorecard")
        .setMessage("Are you sure you want to delete this scorecard?")
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {

                // create an AsyncTask that deletes the scorecard in another
                // thread, then calls finish after the deletion
                AsyncTask<Void, Void, Boolean> deleteTask =

                        new AsyncTask<Void, Void, Boolean>() {

                            @Override
                            protected Boolean doInBackground(Void... params) {
                                mDbHelper.open();
                                return mDbHelper.deleteScorecard(cardId);
                            }

                            @Override
                            protected void onPostExecute(Boolean v) {
                                super.onPostExecute(v);
                                if (!v)
                                    Toast.makeText(ScorecardViewer.this, "ERROR: card not deleted", Toast.LENGTH_SHORT).show();
                                else {
                                    mDbHelper.close();
                                    finish();
                                }
                            }
                        };
                deleteTask.execute();
            }
        })
        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        })
        .show();

    }


    //used to set up the scores hashmap from the json string.
    //This step can be removed if using json object instead of hashmap.
    private boolean inflateJSON(String jsonString){
        try {
            JSONObject new_scores = new JSONObject(jsonString);
            Iterator<String> itr = new_scores.keys();
            while(itr.hasNext()){
                String name = itr.next();
                JSONArray new_pars = new_scores.getJSONArray(name);
                int new_pars_length = new_pars.length();
                int[] pars = new int[new_pars_length];
                for(int i = 0; i < new_pars_length; i++)
                    pars[i] = new_pars.getInt(i);
                scores.put(name, pars);
            }
            return true;
        }catch(JSONException e){
            Log.v(TAG, "Problem loading Json object");
            return false;
        }
    }

    private void fillData(){
        ScorecardViewerAdapter custAdapter = new ScorecardViewerAdapter(this, scores, currentHole);
        ListView playerList = (ListView) findViewById(R.id.scorecard_list);
        playerList.setAdapter(custAdapter);

        TextView holeDisplayNumber = (TextView) findViewById(R.id.scorecard_current_hole);
        String dispStr = "Hole ";
        dispStr = dispStr + (currentHole+1);
        holeDisplayNumber.setText(dispStr);

        TextView courseDisplayName = (TextView) findViewById(R.id.scorecard_course_name);
        courseDisplayName.setText(courseName);
    }


    private class LoadScorecard extends AsyncTask<Long, Void, Boolean> {

        private DisCaddyDbAdapter mDbHelperScore =
                new DisCaddyDbAdapter(ScorecardViewer.this);

        // perform the database access
        @Override
        protected Boolean doInBackground(Long... params) {
            mDbHelperScore.open();
            // get a cursor containing all data on given entry
            Cursor c =  mDbHelperScore.fetchScore(params[0]);
            if(c == null)
                return false;
            startManagingCursor(c);
            //store data and create hashmap scores.
            courseName = c.getString(2);
            cardName = c.getString(3);
            String map = c.getString(4);
            return inflateJSON(map);
        }


        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);

            if(!isSuccess) {
                Toast.makeText(ScorecardViewer.this, "ERROR: could not load card", Toast.LENGTH_SHORT).show();
                finish();
            }
            fillData();
        }
    }
}
