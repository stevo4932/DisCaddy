package com.discaddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Calendar;


public class PlayerNewEdit extends Activity {
    private long player_id;
    private EditText nameView, courseView, diskView, scoreView;
    private String name, course, aveScore, disk, image, lowScore, lowCourse;
    private long createdAt;
    private ImageButton imageButton;
    private static final int PIC_ID = 1;
    private static final String TAG = "PlayerNewEdit";
    private boolean isEdit = true;
    DisCaddyDbAdapter mDbHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_newedit);
        mDbHelper = new DisCaddyDbAdapter(PlayerNewEdit.this);
        mDbHelper.open();
        //get profile info from intent if edit.
        Intent playerIntent = getIntent();
        player_id = playerIntent.getLongExtra("id", -1);
        if (player_id == -1)
            isEdit = false;
        getTextViews();
        if(isEdit)
            new LoadPlayerData().execute(player_id);
    }

    protected void onDestroy(){
        super.onDestroy();
        mDbHelper.close();
    }

    void getTextViews() {
        nameView = (EditText) findViewById(R.id.name_edit_player);
        courseView = (EditText) findViewById(R.id.course_edit_player);
        scoreView = (EditText) findViewById(R.id.score_edit_player);
        diskView = (EditText) findViewById(R.id.disk_edit_player);
        imageButton = (ImageButton) findViewById(R.id.image_edit_player);

    }

    //wraps database access so that it is not done in the GUI thread.
    private class LoadPlayerData extends AsyncTask<Long, Object, Cursor> {

         // perform the database access
        @Override
        protected Cursor doInBackground(Long... params) {

            // get a cursor containing all data on given entry
            return mDbHelper.fetchPlayer(params[0]);
        }


        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor playerData) {
            super.onPostExecute(playerData);

            playerData.moveToFirst();

            //Get Data from cursor.
            createdAt = playerData.getLong(1); // created at time
            name = playerData.getString(2); // name
            course = playerData.getString(3); // course
            disk = playerData.getString(4); // disk
            aveScore = playerData.getString(5); //ave score
            lowScore = playerData.getString(6); //low score
            lowCourse = playerData.getString(7); //low course
            image = playerData.getString(8); //image

            //set edit fields
            nameView.setText(name);
            courseView.setText(course);
            scoreView.setText(aveScore);
            diskView.setText(disk);
            new SetImage().execute();

            playerData.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.player_new_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_save_player) {
            new SavePlayerData().execute();
        } else if (id == R.id.action_help_player) {
            new AlertDialog.Builder(this)
                    .setTitle("Help")
                    .setMessage("Input player's information into the text fields.\n" +
                            "\n Select the save button when done to go back.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        } else {
            Log.v(TAG, "Bad selection");
        }
        return super.onOptionsItemSelected(item);
    }

    /* Set's the image button to the Player's stored image.
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
                imageButton.setBackgroundColor(Color.argb(0, 0, 0, 0));
                imageButton.setImageBitmap(bitmap);
            }else
                if(image != null)
                    Toast.makeText(PlayerNewEdit.this, "Image Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    /*called when user clicks on the image button.
    * Fires intents to take a picture or select a picture from memory.*/
    public void updateImage(View view) {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra (Intent.EXTRA_INITIAL_INTENTS, new Intent[] { takePhotoIntent });

        startActivityForResult(chooserIntent, PIC_ID);
    }

    /*After user selects the picture, this function is called.
    * Sets the image's path to the image string variable */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PIC_ID && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            image = cursor.getString(columnIndex);
            cursor.close();
            new SetImage().execute();

        }
    }



    /*
    *Called upon save button click.
    *Add or Update Player.
    *Sends user back to previous activity.
    */
    private class SavePlayerData extends AsyncTask<Void, Object, Void> {

        /*DisCaddyDbAdapter mDbHelper =
                new DisCaddyDbAdapter(PlayerNewEdit.this);*/

        //get info from EditText fields and update strings.
        //Runs on UI Thread.
        @Override
        protected void onPreExecute (){
            name = nameView.getText().toString();
            course = courseView.getText().toString();
            aveScore = scoreView.getText().toString();
            disk = diskView.getText().toString();
            createdAt = Calendar.getInstance().getTimeInMillis();
        }

        // perform the database access on non UI thread.
        // Saves the Player.
        @Override
        protected Void doInBackground(Void...params) {

            if (!name.equals("")) {
                //mDbHelper.open();
                if (isEdit)
                    mDbHelper.updatePlayer(player_id, name, course, aveScore, lowScore, createdAt, lowCourse, disk, image);
                else
                    mDbHelper.createPlayer(name, course, aveScore, lowScore, createdAt, lowCourse, disk, image);
            }
            return null;
        }

        // Clean up database access and call finish();
        // executes on UI Thread.
        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if(!name.equals("")) {/*
                mDbHelper.close();*/
                finish();
            }else
                Toast.makeText(PlayerNewEdit.this, "Player's name is Required", Toast.LENGTH_SHORT).show();
        }
    }
}





