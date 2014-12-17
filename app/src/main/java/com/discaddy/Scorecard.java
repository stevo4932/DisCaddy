package com.discaddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class Scorecard extends Activity implements View.OnClickListener{

    private Map<String, int[]> scores;
    private ArrayList<String> playerNames;
    private final String TAG = "Scorecard";
    public int currentHole;
    public String courseName;

    //Variables for the swipe sensitivity.
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    //Used to attach gesture controls.
    private GestureDetector gestureDetector;

    Button nextHole, previousHole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_card);
        gestureDetector = new GestureDetector(this, new OnSwipeGestureListener());
        this.scores = new HashMap<String, int[]>();
        this.playerNames = new ArrayList<String>();
        Intent myIntent = getIntent();
        this.currentHole = 0;
        String[] parStrings = myIntent.getStringArrayExtra("courseString");
        String[] players = myIntent.getStringArrayExtra("playerString");
        //courseName = "zilker";
        courseName = myIntent.getStringExtra("courseName");
        for (String player : players) {
            int[] pars = new int[18];
            for (int i=0; i<18; i++)
                pars[i] = Integer.parseInt(parStrings[i]);
            scores.put(player, pars);
        }

        for (Map.Entry<String, int[]> e : scores.entrySet())
            playerNames.add(e.getKey());

        fillData();

        nextHole = (Button)findViewById(R.id.next_hole_button);
        previousHole = (Button)findViewById(R.id.previous_hole_button);

        nextHole.setOnClickListener(this);
        previousHole.setOnClickListener(this);
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

    @Override
    //simply used to attach our swipe detector to any user touches.
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_scorecards, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        switch(item.getItemId()){
            case R.id.action_scorecard_save:
                saveScores();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillData(){
        ScorecardCustomAdapter custAdapter = new ScorecardCustomAdapter(this, scores, currentHole);
        ListView playerList = (ListView) findViewById(R.id.scorecard_list);
        playerList.setAdapter(custAdapter);

        TextView holeDisplayNumber = (TextView) findViewById(R.id.scorecard_current_hole);
        String dispStr = "Hole ";
        dispStr = dispStr + (currentHole+1);
        holeDisplayNumber.setText(dispStr);

        TextView courseDisplayName = (TextView) findViewById(R.id.scorecard_course_name);
        courseDisplayName.setText(courseName);
    }

    //used to handle the users left or right swipes. ignores other gestures.
    //moves current hole based on gesture.
    private class OnSwipeGestureListener extends
            GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            float deltaX = e2.getX() - e1.getX();
            if ((Math.abs(deltaX) < SWIPE_MIN_DISTANCE)
                    || (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY)) {
                return false; // insignificant swipe
            } else {
                if (deltaX < 0) { // left to right
                    handleSwipeLeftToRight();
                } else { // right to left
                    handleSwipeRightToLeft();
                }
            }
            return true;
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

    private void saveScores(){
        //prompt user to enter score card name. Also how we will look them up and display them.
        //since there may be multiple from multiple courses.
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Save Scorecard");
        alert.setMessage("Input scorecard name");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String card_name = input.getText().toString();
                //make JSON Object for storage.
                String jsonString;
                if((jsonString = makeJSON()) != null)
                    new SaveScorecard().execute(card_name, jsonString); //create scorecard in background.
                else
                    Toast.makeText(Scorecard.this, "JSON ERROR, card not saved", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });
        alert.show();
    }

    //change map into json object for serialization.
    //Just makes life a little easier.
    private String makeJSON(){
        JSONObject new_scores = new JSONObject();
        for (String player : scores.keySet()) {
            JSONArray new_pars = new JSONArray();
            int[] old_pars = scores.get(player);
            for (int i : old_pars)
                new_pars.put(i);
            try {
                new_scores.put(player, new_pars);
            } catch (JSONException e) {
                Log.v(TAG, "ISSUE CREATING JSON OBJECT");
                return null;
            }
        }
        return new_scores.toString();
    }

    //wraps database access so that it is not done in the GUI thread.
    private class SaveScorecard extends AsyncTask<String, Void, Void> {

        private DisCaddyDbAdapter mDbHelper= new DisCaddyDbAdapter(Scorecard.this);
        // perform the database access
        @Override
        //pass in the user's card name and the json string and create the cards.
        protected Void doInBackground(String... params) {
            mDbHelper.open();
            long createdAt = Calendar.getInstance().getTimeInMillis();
            String card_name = params[0];
            String jsonString = params[1];
            //save the scores.
            mDbHelper.createScoreCard(createdAt, courseName, card_name, jsonString);
            return null;
        }

        protected void onPostExecute(Void v){
            super.onPostExecute(v);
            mDbHelper.close();
            Toast.makeText(Scorecard.this, "Scorecard Saved", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
