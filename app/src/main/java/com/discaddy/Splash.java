package com.discaddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class Splash extends Activity implements View.OnClickListener {

    Button newScorecard;
    Button recentScorecard;
    Button players;
    Button Courses;
    Button discs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        newScorecard = (Button)findViewById(R.id.new_scorecard_button);
        recentScorecard = (Button)findViewById(R.id.recent_scorecards_button);
        players = (Button)findViewById(R.id.players_button);
        Courses = (Button)findViewById(R.id.find_a_course_button);
        discs = (Button)findViewById(R.id.discs_button);

        newScorecard.setOnClickListener(this);
        recentScorecard.setOnClickListener(this);
        players.setOnClickListener(this);
        Courses.setOnClickListener(this);
        /*findACourse.setOnClickListener(this);(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String uri = String.format("geo:0,0?q=disc+golf+course"); // for example pass geo:75.333000,30.003030 to search for latitude = 75.333000 and longitude = 30.003030 as your default search query
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });*/
        discs.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == newScorecard){
            Intent myIntent = new Intent(Splash.this, NewScorecard.class);
            //myIntent.putExtra("key", value);
            Splash.this.startActivity(myIntent);
        }
        if(v == recentScorecard){
            Intent myIntent = new Intent(Splash.this, RecentScorecards.class);
            //myIntent.putExtra("key", value);
            Splash.this.startActivity(myIntent);
        }
        if(v == players){
            Intent myIntent = new Intent(Splash.this, Player.class);
            //myIntent.putExtra("key", value);
            Splash.this.startActivity(myIntent);
        }
        if(v == Courses) {
            Intent myIntent = new Intent(Splash.this, Courses.class);
            //myIntent.putExtra("key", value);
            Splash.this.startActivity(myIntent);
        }
        if(v == discs) {
            Intent myIntent = new Intent(Splash.this, Discs.class);
            //myIntent.putExtra("key", value);
            Splash.this.startActivity(myIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_splash_help) {
            displayHelpMessage();
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayHelpMessage(){
        new AlertDialog.Builder(this)
            .setTitle("Help")
            .setMessage("Welcome to DisCady!\n" +
                    "\nThis app is designed to assist you in playing Disc Golf.\n" +
                    "\nTo start a new scorecard, select New Scorecard" +
                    " (Note: you will need to have set up the player's profiles and the " +
                    "course's profile before you begin).\n" +
                    "\nTo view saved scorecards, select Scorecards.\n" +
                    "\nTo view, or create new players, select Players.\n" +
                    "\nTo view, find, or create new courses, select Courses.")
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}
            })
            .show();
}
}
