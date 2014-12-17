package com.discaddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class CoursesProfile extends Activity {
    private static String TAG = "CourseProfile";
    private DisCaddyDbAdapter mDbHelper;
    private long course_id;
    private int[] course_pars;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_profile);
        course_pars = new int[18];

        mDbHelper = new DisCaddyDbAdapter(this);
        mDbHelper.open();

        Intent courseIntent = getIntent();
        course_id = courseIntent.getLongExtra("id", 0);
        fillData();
    }

    @Override
    public void onResume (){
        super.onResume();
        fillData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_courses_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_course_profile_edit:
                Intent myIntent = new Intent(CoursesProfile.this, CoursesNewEdit.class);
                myIntent.putExtra("id", course_id);
                CoursesProfile.this.startActivity(myIntent);
                break;
            case R.id.action_course_profile_delete:
                deleteCourse();
                break;
            case R.id.action_course_profile_help:
                help();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void deleteCourse(){
        new AlertDialog.Builder(this)
            .setTitle("Delete Player")
            .setMessage("Are you sure you want to delete this course?")
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // continue with delete
                    mDbHelper.deleteCourse(course_id);
                    finish();
                }
            })
            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            })
            .show();
    }

    private void help(){
        new AlertDialog.Builder(this)
                .setTitle("Help")
                .setMessage("Displays selected courses' information.\n" +
                        "\nPress edit button to edit courses' information.\n" +
                        "\nPress the delete button to remove this courses' profile.\n")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .show();
    }

    public void viewPars(View view){
        Intent myIntent = new Intent(CoursesProfile.this, CoursesSetPars.class);
        myIntent.putExtra("array", course_pars);
        myIntent.putExtra("isEdit", false);
        CoursesProfile.this.startActivityForResult(myIntent,4832);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void fillData(){
        Cursor c = mDbHelper.fetchCourse(course_id);
        //startManagingCursor(c);

        TextView name = (TextView) findViewById(R.id.course_profile_name);
        name.setText(c.getString(2));

        TextView address = (TextView) findViewById(R.id.course_profile_location);
        address.setText(c.getString(4));

        TextView number = (TextView) findViewById(R.id.course_profile_number);
        number.setText(c.getString(5));

        TextView website = (TextView) findViewById(R.id.course_profile_website);
        website.setText(c.getString(3));

        TextView rating = (TextView) findViewById(R.id.course_profile_rating);
        rating.setText(c.getString(6));

        for(int i = 0; i < 18; i++)
            course_pars[i] = c.getInt(i+7);
    }
}
