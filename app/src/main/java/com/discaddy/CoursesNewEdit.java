package com.discaddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;


public class CoursesNewEdit extends Activity {
    private static final String TAG = "CoursesNewEdit";
    private DisCaddyDbAdapter mDbHelper;
    private ParCustomAdapter adapter;
    private long course_id;
    private boolean isEdit = true;
    public int[] course_pars;
    private static final int PARS = 4832;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_newedit);
        course_pars = new int[18];

        //open database for storing new course info
        mDbHelper = new DisCaddyDbAdapter(this);
        mDbHelper.open();

        //get profile info from intent
        Intent playerIntent = getIntent();
        course_id = playerIntent.getLongExtra("id", -1);
        if(course_id != -1)
            fillData();
        else {
            isEdit = false;
            for(int i = 0; i<18; i++)
                course_pars[i] = 3;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_courses_newedit, menu);
        return true;
    }

    @Override
    /* Used when the user selects an action bar item.*/
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //user selects save.
            case R.id.action_save_course:
                saveCourse();
                finish();
                break;
            //user selects help.
            case R.id.action_help_new_course:
                displayHelpMessage();
                break;
            default:
                Log.v(TAG, "bad selection");
        }
        return super.onOptionsItemSelected(item);
    }


    /*called on selection of save action button.
      stores the new course in the database.*/
    public void saveCourse(){
        int[] adt_pars = course_pars;
        long createdAt = Calendar.getInstance().getTimeInMillis();
        String course = ((EditText) findViewById(R.id.course_new_name)).getText().toString();
        String web = ((EditText) findViewById(R.id.course_new_website)).getText().toString();
        String address = ((EditText) findViewById(R.id.course_new_location)).getText().toString();
        String rating = ((EditText) findViewById(R.id.course_new_rating)).getText().toString();
        String number = ((EditText) findViewById(R.id.course_new_number)).getText().toString();

        if(isEdit)
            mDbHelper.updateCourse(course_id, createdAt, course, web, address, number, rating, adt_pars);
        else
            mDbHelper.createCourse(createdAt, course, web, address, number, rating, adt_pars);
    }

    /*Called on selection of the help action button.
    Displays message to user on how to use this activity.*/
    private void displayHelpMessage(){
        new AlertDialog.Builder(this)
                .setTitle("Help")
                .setMessage("Input the courses name into the text box.\n" +
                        "\nSet each hole's par values using the incrementing and decrementing" +
                        "buttons or select the individual text boxes to input numbers from the keyboard.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .show();
    }
    //on button click
    public void setPars(View view){
        Intent myIntent = new Intent(CoursesNewEdit.this, CoursesSetPars.class);
        myIntent.putExtra("array", course_pars);
        myIntent.putExtra("isEdit", true);
        CoursesNewEdit.this.startActivityForResult(myIntent,PARS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        course_pars = data.getIntArrayExtra("array");
    }

    //is isEdit = true set the text fields.
    private void fillData(){

        //fill data structures.
        Cursor playerData = mDbHelper.fetchCourse(course_id);

        EditText name = (EditText) findViewById(R.id.course_new_name);
        name.setText(playerData.getString(2));

        EditText website = (EditText) findViewById(R.id.course_new_website);
        website.setText(playerData.getString(3));

        EditText address = (EditText) findViewById(R.id.course_new_location);
        address.setText(playerData.getString(4));

        EditText number = (EditText) findViewById(R.id.course_new_number);
        number.setText(playerData.getString(5));

        EditText rating = (EditText) findViewById(R.id.course_new_rating);
        rating.setText(playerData.getString(6));

        //fill course par array with user data.
        for(int i = 0; i < 18; i++)
            course_pars[i] = playerData.getInt(i+7);

    }
}
