package org.jbs.happysad;

import static android.provider.BaseColumns._ID;
import static org.jbs.happysad.Constants.EMO;
import static org.jbs.happysad.Constants.LAT;
import static org.jbs.happysad.Constants.LONG;
import static org.jbs.happysad.Constants.MSG;
import static org.jbs.happysad.Constants.TABLE_NAME;
import static org.jbs.happysad.Constants.TIME;
import android.app.Activity;
import android.content.Context;
import android.content.ContentValues;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class More extends Activity implements OnKeyListener, OnClickListener {
	private static final String TAG = "there's more screen";
	private float latitude = 5;
	private float longitude = 5;
	private HappyData dataHelper;
	int emotion = -1;
	String extradata;
	long myID = 1;
	
	

	/**
	 * Sets up the views and buttons, gets the location, displays the location.
	 * 
	 */
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more);
		
		//figure out whether they clicked happy or sad
		Intent sender = getIntent();
		extradata = sender.getExtras().getString("Clicked");
		//emotion is an int, Clicked gets you a string
		emotion = sender.getExtras().getInt("Emotion");
		


		EditText textField = (EditText)findViewById(R.id.more_textbox);
		TextView t = (TextView) findViewById(R.id.more_text);
		TextView locationView = (TextView) findViewById(R.id.location);
		View submitButton = findViewById(R.id.more_to_dash);
		
		
		t.append(extradata);
		textField.setOnKeyListener(this);
		locationView.setText("unknown");
		submitButton.setOnClickListener(this);
		locationStuff();
		
		
	}
	
	//There used to be a bunch of stuff in oncreate dealing with location. This moves the code to a helper method for more readability.
	private void locationStuff(){
		
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		Log.d(TAG, "creating a new location listener");
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location
				// provider.
				makeUseOfNewLocation(location);}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};

		// Register the listener with the Location Manager to receive location
		// updates
		Log.d(TAG, "Registration  of listener");
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,	0, locationListener);
		
		//SAHAR STORE FROM HERE!!
		
		try {
			Location location = new Location(LocationManager.GPS_PROVIDER);
			longitude = (float) location.getLongitude();
			latitude =  (float) location.getLatitude();
			makeUseOfNewLocation(location);
		}
		catch (Exception e){
		// Remove the listener you previously added
			Log.d(TAG, "Error: " + e);
		}
		
		locationManager.removeUpdates(locationListener);
	}
		
	
	
	/**
	 * Taha wrote this so I'm not sure what it does.
	 * Seems like it sets up the textview to show your lat/long
	 * @param location
	 */
	private void makeUseOfNewLocation(Location location) {
		Log.d(TAG, "entering makeuseofnewlocation");
		int x = 0;
		
		//redundant V
		double longitude = location.getLongitude();
		double latitude = location.getLatitude();
		TextView locationView = (TextView) findViewById(R.id.location);
		locationView.setText("unknown");
		locationView.setText("lat = " + latitude + " long = " + longitude);
		locationView.invalidate();
	}

	public void onClick(View v) {
		//Log.d(TAG, "clicked" + v.getId());
		
		switch (v.getId()) {
		case R.id.more_to_dash:
			Intent i = new Intent(this, Dashboard.class);
			String userstring = ((TextView) findViewById(R.id.more_textbox)).getText().toString();
			saveUpdate(userstring); 			    
			i.putExtra("textboxmessage", userstring);
			i.putExtra("happysaddata", extradata);
			//Log.d(TAG, "adding " + userstring + " to intent");
			startActivity(i);
			break;
		}
	}
	
	private void saveUpdate(String msg){
		
		HappyBottle b = new HappyBottle(myID, latitude, longitude, emotion, msg, System.currentTimeMillis());
		dataHelper = new HappyData(this);
		dataHelper.addBottle(b);
	}

	// got following code from
	// http://stackoverflow.com/questions/2004344/android-edittext-imeoptions-done-track-finish-typing
	public boolean onKey(View v, int keyCode, KeyEvent event) {

		if ((event.getAction() == KeyEvent.ACTION_DOWN)
				&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
			// Done pressed! Do something here.
			EditText t = (EditText) v;
			Log.d(TAG, "text entered: " + t.getText());
			this.onClick(findViewById(R.id.more_to_dash));
			// Intent i = new Intent(this, prompt.class);
			// startActivity(i);
		}
		// Returning false allows other listeners to react to the press.
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, Prefs.class));
			return true;
			// More items go here (if any) ...
		}
	return false;
	}

}
