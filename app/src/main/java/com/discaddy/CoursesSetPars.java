package com.discaddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;


public class CoursesSetPars extends Activity {
    private static final String TAG = "CourseSetPar";
    private DisCaddyDbAdapter mDbHelper;
    private int[] course_pars;
    private boolean isEdit;
    ParCustomAdapter adapter;
    ParViewCustomAdapter viewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_set_pars);

        //open database
        mDbHelper = new DisCaddyDbAdapter(this);
        mDbHelper.open();

        //get profile info from intent
        Intent playerIntent = getIntent();
        course_pars = playerIntent.getIntArrayExtra("array");
        isEdit = playerIntent.getBooleanExtra("isEdit", true);
        if(course_pars != null)
            fillData();
    }

    protected void onDestroy(){
        super.onDestroy();
        mDbHelper.close();
    }

    @Override
    public void onBackPressed(){
        Intent myIntent = new Intent();
        if(isEdit)
            myIntent.putExtra("array", adapter.getPars());
        else
            myIntent.putExtra("array", viewAdapter.getPars());
        setResult(4832,myIntent);
        super.onBackPressed();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(isEdit) {
            getMenuInflater().inflate(R.menu.menu_courses_set_pars, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_pars_save) {
            Intent myIntent = new Intent();
            myIntent.putExtra("array", adapter.getPars());
            setResult(4832,myIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillData() {
        //create variables.
        ArrayList<String> hole_names = new ArrayList<String>();

        //add hole names.
        for(int i = 1; i <= 18; i++){
            hole_names.add("Hole #"+i);
        }
        ListView listView = (ListView) findViewById(R.id.course_new_pars_list);
        if(isEdit) {
            adapter = new ParCustomAdapter(this, hole_names, course_pars);
            //Attach the adapter to the ListView
            listView.setAdapter(adapter);
        }else {
            viewAdapter = new ParViewCustomAdapter(this, hole_names, course_pars);
            //Attach the adapter to the ListView
            listView.setAdapter(viewAdapter);
        }
    }
}
