package com.wade12.youtubevideolist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	ListView videoList;
	// String[] videoArray = {"No Videos"};
	ArrayList<String> videoArrayList = new ArrayList<String>();
	ArrayAdapter<String> videoAdapter;
	Context context;
	String feedUrl = "http://gdata.youtube.com/feeds/api/users/sheriefh1/uploads?v=2&alt=jsonc&start-index=1&max-results=7";
	// version = 2, alt is the youtube format, jsonc is stripped-down version of json.
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		
		videoList = (ListView) findViewById(R.id.videoList);
		videoAdapter = new ArrayAdapter<String>(this, R.layout.video_list_item, videoArrayList);
		videoList.setAdapter(videoAdapter);
		
		VideoListTask loaderTask = new VideoListTask();
		loaderTask.execute();
	} // end method onCreate

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	} // end method onCreateOptionsMenu
	
	
	public class VideoListTask extends AsyncTask<Void, Void, Void> {
		
		ProgressDialog dialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(context);
			dialog.setTitle("Loading Videos");
			dialog.show();
		} // end method onPreExecute
		
		
		@Override
		protected Void doInBackground(Void... arg0) {
			/*
			try {
				Thread.sleep(3000);
			} // end try
			catch (InterruptedException exception) {
				exception.printStackTrace();
			} // end catch
			*/
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet getRequest = new HttpGet(feedUrl);
			
			try {
				HttpResponse response = httpClient.execute(getRequest);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				
				if (statusCode != 200) {
					// 200 is the code number for OK.
					return null;
				} // end if
				
				InputStream jsonStream = response.getEntity().getContent();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(jsonStream));
				StringBuilder builder = new StringBuilder();
				String line;
					
				while ( ( line = bufferedReader.readLine() ) != null) {
					builder.append(line);
				} // end while
					
				String jsonData = builder.toString();
				// Log.i("YouTubeJsonData", jsonData);
					
				JSONObject json = new JSONObject(jsonData);
				// got data, and jsondata is made into one giant json object.
				// json is like an array of arrays
				// json objects wrapped in "{" & "}", and json arrays wrapped in "[" & "]"
				JSONObject data = json.getJSONObject("data");
				// it looks for json object with key = "data"
					
				// now, from the inner data object we will get the items array
				JSONArray items = data.getJSONArray("items");
					
				for (int i=0; i<items.length(); i++) {
					JSONObject video = items.getJSONObject(i);
					// String title = video.getString("title");
					videoArrayList.add(video.getString("title"));
					// i.e. add it to out video array
				} // end for loop
										
			} // end try
			catch (ClientProtocolException cpException) {
				cpException.printStackTrace();
			} // end catch
			catch (IOException ioException) {
				ioException.printStackTrace();
			} // end catch	
			catch (JSONException jsonException) {
				jsonException.printStackTrace();
			} // end catch
			
			return null;
		} // end method doInBackground
		

		@Override
		protected void onPostExecute(Void result) {
			dialog.dismiss();
			videoAdapter.notifyDataSetChanged();
			// notifies change, and then it should re-load.
			super.onPostExecute(result);
		} // end method onPostExecute
		
	} // end private inner Class VideoListTask

} // end Class MainActivity
