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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class Courses extends Activity {
    private static final String TAG = "Courses";
    DisCaddyDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        mDbHelper = new DisCaddyDbAdapter(this);
        mDbHelper.open();

        new LoadCourses().execute();
    }

    protected void onDestroy(){
        super.onDestroy();
        mDbHelper.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_courses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            //When user selects add course.
            case R.id.action_add_course:
                Intent myIntent = new Intent(Courses.this, CoursesNewEdit.class);
                Courses.this.startActivity(myIntent);
                break;
            //When user selects find course.
            case R.id.action_find_course:
                Intent intent = new Intent(Courses.this, CoursesLookUp.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                Courses.this.startActivity(intent);
                break;
            //user selects help.
            case R.id.action_help_course:
                displayHelpMessage();
                break;
            default:
                Log.v(TAG, "Bad selection");
        }
        return super.onOptionsItemSelected(item);
    }

    /*
   * @Override
   * Used only when no list items exist
   * Displays no_courses string
   */
    public void onContentChanged() {
        super.onContentChanged();
        View empty = findViewById(R.id.empty_course);
        ListView list = (ListView) findViewById(R.id.courses_list);
        list.setEmptyView(empty);
    }

    /*Displays help message when user selects
    help action button.*/
    private void displayHelpMessage(){
        new AlertDialog.Builder(this)
            .setTitle("Help")
            .setMessage("Select a Course in the list to go to its profile.\n" +
                    "\nSelect the add button to add a new user defined course.\n" +
                    "\nSelect the search button to search for courses near you.")
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            })
            .show();
    }

    //wraps database access so that it is not done in the GUI thread.
    private class LoadCourses extends AsyncTask<Void, Object, Cursor> {

        // perform the database access
        @Override
        protected Cursor doInBackground(Void... params) {
            // get a cursor containing all data on given entry
            return mDbHelper.fetchAllCourses();
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
            SimpleCursorAdapter courses =
                    new SimpleCursorAdapter(Courses.this, R.layout.list_row, c, from, to);
            ListView courseList = (ListView)findViewById(R.id.courses_list);
            courseList.setAdapter(courses);
            courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    Intent myIntent = new Intent(Courses.this, CoursesProfile.class);
                    myIntent.putExtra("id", id);
                    Courses.this.startActivity(myIntent);
                }
            });
        }
    }
}
