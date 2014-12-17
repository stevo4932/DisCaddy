package com.discaddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class PlayerProfile extends Activity {
    private static String TAG = "PlayerProfie";
    private String name, course, aveScore, disk, image, createdAt, lowScore, lowCourse;
    private ImageView imageView;
    private long player_id;
    public DisCaddyDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile);
        Intent playerIntent = getIntent();
        player_id = playerIntent.getLongExtra("id", 0);
        mDbHelper = new DisCaddyDbAdapter(this);
        //mDbHelper.open();
        //new LoadPlayerData().execute();
    }

    protected void onStart(){
        super.onStart();
        mDbHelper.open();
        Log.v(TAG, "Start Called");
        new LoadPlayerData().execute();
    }

    protected void onStop(){
        super.onStop();
        Log.v(TAG, "Stop called");
        mDbHelper.close();
    }

    //wraps database access so that it is not done in the GUI thread.
    private class LoadPlayerData extends AsyncTask<Void, Object, Cursor> {

        // perform the database access
        @Override
        protected Cursor doInBackground(Void... params) {
            // get a cursor containing all data on given entry
            return mDbHelper.fetchPlayer(player_id);
        }


        // use the Cursor returned from the doInBackground method
        // Runs on UI thread.
        @Override
        protected void onPostExecute(Cursor playerData) {
            super.onPostExecute(playerData);

            playerData.moveToFirst();

            ///get data from database
            createdAt = playerData.getString(1); // created at time
            name = playerData.getString(2); // name
            course = playerData.getString(3); // course
            disk = playerData.getString(4); // disk
            aveScore = playerData.getString(5); //ave score
            lowScore = playerData.getString(6); //low score
            lowCourse = playerData.getString(7); //low course
            image = playerData.getString(8); //image

            //set data to available text fields.
            TextView nameView = (TextView) findViewById(R.id.player_name);
            nameView.setText("Player Name: " + name);
            TextView courseView = (TextView) findViewById(R.id.player_fav_course);
            courseView.setText("Favorite Course: " + course);
            TextView scoreView = (TextView) findViewById(R.id.player_score);
            scoreView.setText("Player Score: " + aveScore);
            TextView diskView = (TextView) findViewById(R.id.player_fav_disc);
            diskView.setText("Favorite Disk: " + disk);
            imageView = (ImageView) findViewById(R.id.player_image);
            new SetImage().execute();
            playerData.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.player_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            Intent myIntent = new Intent(PlayerProfile.this, PlayerNewEdit.class);
            myIntent.putExtra("id", player_id);
            PlayerProfile.this.startActivity(myIntent);
            return true;
        }
        else if (id == R.id.action_delete){
            deletePlayer();
        }
        else if (id == R.id.action_help){
            new AlertDialog.Builder(this)
                    .setTitle("Help")
                    .setMessage("Displays selected player's information and statistics.\n" +
                            "\nPress edit button to edit player's information.\n" +
                            "\nPress the delete button to remove this player's profile.\n")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {}
                    })
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    /* Set's the image view to the Player's stored image.
    * Does this off the UI thread.*/
    class SetImage extends AsyncTask<Void, Void, Bitmap> {

        // Decode image in background in found.
        @Override
        protected Bitmap doInBackground(Void... params) {
            if(image != null)
                return BitmapFactory.decodeFile(image);
            return null;
        }
        //set image button to newly decoded image.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null) {
                imageView.setImageBitmap(bitmap);
                imageView.getLayoutParams().height = 256;
                imageView.getLayoutParams().width = 256;
                imageView.requestLayout();
            }else
                if(image != null)
                    Toast.makeText(PlayerProfile.this, "Image Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    /* display alert dialog box to check delete selection.
    * Deletes player profile off UI thread then calls finish() */
    private void deletePlayer(){
        new AlertDialog.Builder(this)
            .setTitle("Delete Player")
            .setMessage("Are you sure you want to delete this player?")
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int button) {

                    // create an AsyncTask that deletes the Player in another
                    // thread, then calls finish after the deletion
                    AsyncTask<Void, Void, Void> deleteTask =
                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... params) {
                                mDbHelper.deletePlayer(player_id);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void v) {
                                super.onPostExecute(v);
                                finish();
                            }
                        };
                    deleteTask.execute();
                }
            })
            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {}})
            .show();
    }

}

