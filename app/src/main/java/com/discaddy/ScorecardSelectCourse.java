package com.discaddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;


public class ScorecardSelectCourse extends Activity {

    private String[] player_list;
    private String courseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scorecard_select_course);

        Intent intent = getIntent();
        if((player_list = intent.getStringArrayExtra("playerString")) == null)
            finish();

        new LoadCourses().execute();
    }

    public void onContentChanged() {
        super.onContentChanged();
        View empty = findViewById(R.id.empty_scorecard_course);
        ListView list = (ListView) findViewById(R.id.scorecard_course_list);
        list.setEmptyView(empty);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scorecard_select_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_scorecard_add_course) {
            addCourse();
        }

        return super.onOptionsItemSelected(item);
    }

    private void addCourse(){
        String[] options = {"Find Course", "Create Course"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Course")
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int index) {
                        if(index == 0){
                            Intent myIntent = new Intent(ScorecardSelectCourse.this, CoursesLookUp.class);
                            myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            ScorecardSelectCourse.this.startActivity(myIntent);
                        }else{
                            Intent myIntent = new Intent(ScorecardSelectCourse.this, CoursesNewEdit.class);
                            ScorecardSelectCourse.this.startActivity(myIntent);
                        }
                    }
                });
        builder.create();
        builder.show();
    }

    //wraps database access so that it is not done in the GUI thread.
    private class LoadCourses extends AsyncTask<Void, Object, Cursor> {
        DisCaddyDbAdapter mDbHelper = new DisCaddyDbAdapter(ScorecardSelectCourse.this);
        // perform the database access
        @Override
        protected Cursor doInBackground(Void... params) {
            mDbHelper.open();
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
                    new SimpleCursorAdapter(ScorecardSelectCourse.this, R.layout.list_row, c, from, to);
            ListView courseList = (ListView)findViewById(R.id.scorecard_course_list);
            courseList.setAdapter(courses);
            courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    new SelectCourse().execute(id);
                }

            });
            //c.close();
            mDbHelper.close();
        }
    }

    //wraps database access so that it is not done in the GUI thread.
    private class SelectCourse extends AsyncTask<Long, Void, String[]> {

        private DisCaddyDbAdapter mDbHelper= new DisCaddyDbAdapter(ScorecardSelectCourse.this);
        // perform the database access
        @Override
        //pass in the user's card name and create the cards.
        protected String[] doInBackground(Long... id) {
            mDbHelper.open();
            String[] courseParsArray = new String[18];
            Cursor c = mDbHelper.fetchCourse(id[0]);
            courseName = c.getString(2);
            for(int i = 0; i < 18; i++)
                courseParsArray[i] = c.getString(i+7);
            return courseParsArray;
        }

        protected void onPostExecute(String[] courseParArray){
            super.onPostExecute(courseParArray);
            Intent myIntent = new Intent(ScorecardSelectCourse.this, Scorecard.class);
            myIntent.putExtra("courseString", courseParArray);
            myIntent.putExtra("playerString", player_list);
            myIntent.putExtra("courseName", courseName);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            ScorecardSelectCourse.this.startActivity(myIntent);
        }
    }
}
