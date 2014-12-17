package com.discaddy;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;


public class CoursesWebView extends Activity {

    public WebView wView;
    public String course_parm;
    public String course_name;
    public String course_web;
    public String course_address;
    public String course_phone_number;
    public String course_rating;
    public DisCaddyDbAdapter mDbHelper;
    private static final String TAG = "CoursesWebView";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_web_view);
        mDbHelper = new DisCaddyDbAdapter(this);
        mDbHelper.open();

        Intent courseIntent = getIntent();
        course_parm = courseIntent.getStringExtra("parameter");

        wView = (WebView) findViewById(R.id.webview);
        new SetWebView().execute(course_parm);
    }

    protected void onDestroy(){
        super.onDestroy();
        mDbHelper.close();
    }

    private class CourseWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_courses_web_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_find_course) {
            //do some adding items to the database and send to the newedit class.
            addCourse();
        }

        return super.onOptionsItemSelected(item);
    }

    private void addCourse(){
        int[] default_pars = new int[18];
        for(int i = 0; i < default_pars.length; i++) {
            default_pars[i] = 3;
        }
        long createdAt = Calendar.getInstance().getTimeInMillis();
        long id = mDbHelper.createCourse(createdAt, course_name, course_web, course_address, course_phone_number, course_rating, default_pars);

        Intent myIntent = new Intent(CoursesWebView.this, CoursesNewEdit.class);
        myIntent.putExtra("id", id);
        CoursesWebView.this.startActivity(myIntent);
    }

    private class SetWebView extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... placesURL) {
            StringBuilder placesBuilder = new StringBuilder();
            //fetch places
            for (String placeSearchURL : placesURL) {
                //execute search
                HttpClient placesClient = new DefaultHttpClient();
                try {
                    //try to fetch the data
                    HttpGet placesGet = new HttpGet(placeSearchURL);
                    HttpResponse placesResponse = placesClient.execute(placesGet);
                    StatusLine placeSearchStatus = placesResponse.getStatusLine();
                    if (placeSearchStatus.getStatusCode() == 200) {
                        //we have an OK response
                        HttpEntity placesEntity = placesResponse.getEntity();
                        InputStream placesContent = placesEntity.getContent();
                        InputStreamReader placesInput = new InputStreamReader(placesContent);
                        BufferedReader placesReader = new BufferedReader(placesInput);
                        //return new JsonReader(new InputStreamReader(placesContent, "UTF-8"));
                        String lineIn;
                        while ((lineIn = placesReader.readLine()) != null) {
                            placesBuilder.append(lineIn);
                        }

                    } else
                        Log.v(TAG, "Issue somewhere!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return placesBuilder.toString();
        }

        @Override
        protected void onPostExecute(String input) {
            super.onPostExecute(input);
            try {
                JSONObject jsonObject = new JSONObject(input);
                JSONObject results = jsonObject.getJSONObject("result");
                String course_url = results.getString("url");
                try {course_web = results.getString("website");
                }catch (JSONException e){course_web = results.getString("url");}
                try {course_name = results.getString("name");
                }catch (JSONException e){course_name = "";}
                try {course_address = results.getString("formatted_address");
                }catch (JSONException e){course_address = "";}
                try {course_phone_number = results.getString("formatted_phone_number");
                }catch (JSONException e){course_phone_number = "";}
                try {course_rating = results.getString("rating");
                }catch (JSONException e){course_rating = "";}


                wView.getSettings().setJavaScriptEnabled(true);
                wView.loadUrl(course_url);
                wView.setWebViewClient(new CourseWebViewClient());


            }catch(JSONException e){
                Log.v(TAG, "problem with the json object");
            }
        }
    }
}
