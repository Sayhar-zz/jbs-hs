package org.jbs.happysad;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Creates the More activity
 * @author HappyTrack
 */
public class More extends Activity implements OnClickListener {
	private static final String TAG = "more";
	private static final int FUZZFACTOR = 300; //the higher the factor, the more fuzz is applied to the GPS. 
	//Eventually this should be a slider-option in the preference screen.
	
	
	//fields
	private LocationManager gpsLocationManager;
	private LocationManager networkLocationManager;
	private LocationListener networkLocationListener;
	private LocationListener gpsLocationListener;
	private int GPS_latitude;
	private int GPS_longitude;
	private int Network_latitude;
	private int Network_longitude;
	private HappyData dataHelper;
	short emotion = -1;
	String extradata;
	private UIDhelper UIDh;
	private long myID;
	private Syncer s;
	private Thread t;
	Handler handler = new Handler();
	Runnable running;
	private TextView warning;
	
	/**
	 * Initializes activity
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "hello!");
		//Intent to figure out whether they clicked happy or sad from Prompt.java
		extradata = getIntent().getExtras().getString("Clicked");
		emotion = (short) getIntent().getExtras().getInt("Emotion");
		
		if(emotion == 1){
			setContentView(R.layout.morehappy);
		}
		else if(emotion == 0){
			setContentView(R.layout.moresad);
		}

		//prevent text edit from being focused onCreate
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		//Finds the submit_button view
		View submitButton = findViewById(R.id.more_to_map);
		submitButton.setOnClickListener(this);
		warning=  (TextView) findViewById(R.id.privacy_warning);
		warning.setOnClickListener(this);
		
		
		 UIDh = new UIDhelper();
		 myID =UIDh.getUID();
	}  	

	/**
     * Invoked when a view is clicked
     */
	public void onClick(View v) {		
		switch (v.getId()) {
		case R.id.more_to_map:
			String shareString = ((TextView) findViewById(R.id.more_textbox)).getText().toString();
			if (!shareString.equals("")) {
				Intent j = new Intent(this, MyMap.class);
				j.putExtra("GoToMyLocation", true);
				j.putExtra("Run", true);
				j.putExtra("Happy", 1);
				j.putExtra("Sad", 1);
				
				HappyBottle b = saveUpdate(shareString);
				j.putExtra("BottleLat", b.getLat());
				j.putExtra("BottleLong", b.getLong());
				j.putExtra("BottleMsg", b.getMsg());
				j.putExtra("BottleEmo", b.getEmo());
				j.putExtra("BottleTime", b.getTime());
				j.putExtra("id", myID);
				
				s = new Syncer( this); //here we are starting a new "Syncer" thread. All syncer does is upload your recent update(s) and calls getMyHistory
				t = new Thread(s);
				t.start();
				startActivity(j);
				finish();
			} else {
				handler.removeCallbacks(running);
				Runnable runnable = new Runnable(){
					@Override
					public void run(){
						Toast toast = Toast.makeText(getApplicationContext(), "Please Enter a Reason", Toast.LENGTH_SHORT);
						toast.show();
					}
				};
				running = runnable;
				handler.postDelayed(runnable, 1000);
			}
			break;
		case R.id.privacy_warning:
			Intent ii = new Intent(this, Prefs.class);
			ii.putExtra("latitude", GPS_latitude);
			ii.putExtra("longitude", GPS_latitude);
			startActivity(ii);
			break;
		}
		
	}
	
	/**
	 * Helper method to deal with location.
	 */
	private void locationStuff(){
		// Acquire a reference to the system Location Manager
		gpsLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		networkLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a GPS listener that responds to location updates
		gpsLocationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location
				// provider.
				//updateNetworkLocation(location);
				makeUseOfNewLocation(location);}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};

		// Define a Network listener that responds to location updates
		networkLocationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location
				// provider.
				updateNetworkLocation(location);
				//makeUseOfNewLocation(location);
				}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};

		//registers the location managers
		gpsLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,	0, gpsLocationListener);
		networkLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,	0, networkLocationListener);

		try {
			Location locationGPS = new Location(LocationManager.GPS_PROVIDER);
			Location locationNetwork = new Location(LocationManager.NETWORK_PROVIDER);			
			makeUseOfNewLocation(locationGPS);
			updateNetworkLocation(locationNetwork);

		} catch (Exception e){}
	}
	
	private void removeLocation() {
		gpsLocationManager.removeUpdates(gpsLocationListener);
		networkLocationManager.removeUpdates(networkLocationListener);
		gpsLocationManager = null;
		networkLocationManager = null;
	}

	/**
	 * Updates GPS location
	 * @param location
	 */
	private void makeUseOfNewLocation(Location location) {		
		if(GPS_longitude == 0 && GPS_latitude == 0){
			GPS_latitude = (int) (location.getLatitude()*1E6);
			GPS_longitude = (int) (location.getLongitude()*1E6);
		}
	}

	/**
	 * Updates Network location
	 * @param location
	 */
	private void updateNetworkLocation(Location location) {
		if(Network_longitude == 0 && Network_latitude == 0){
			Network_latitude = (int) (location.getLatitude()*1E6);
			Network_longitude = (int) (location.getLongitude()*1E6);
		}
	}

	/**
	 * Saves the update as a bottle and adds the bottle to the DB
	 * @param msg
	 */
	private HappyBottle saveUpdate(String msg){
		if (GPS_longitude == 0 && GPS_latitude == 0){
			GPS_longitude = Network_longitude;
			GPS_latitude = Network_latitude;
		}
		//Sahar added this - if it still thinks we are at 0,0:
		if (GPS_longitude == 0 && GPS_latitude ==0){
			//then find the last known gps and network location, and set the gps to the most recent of these two locations.
			Location lastKnownGPSLocation = gpsLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			Location lastKnownNetworkLocation = networkLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			Location lastBestKnownLocation;
			try{
			if(lastKnownGPSLocation.getTime() > lastKnownNetworkLocation.getTime()){
				lastBestKnownLocation = lastKnownGPSLocation;
			}
			else{
				lastBestKnownLocation = lastKnownNetworkLocation;
			}
			GPS_latitude = (int) (lastBestKnownLocation.getLatitude()*1E6);
			GPS_longitude = (int) (lastBestKnownLocation.getLongitude()*1E6);
			}
			catch (Exception e){
				//this will happen if either last known locations are null. In that case, then you'll have to resign yourself to 0,0 sorry.
				//TODD ADD STUFF HERE
			}
		}
		fuzzify();
		HappyBottle b = new HappyBottle(myID, GPS_latitude, GPS_longitude, emotion, msg, System.currentTimeMillis());
		//if we are in incognito mode
		if(Prefs.getIncognito(this)){
			b.setPrivacy(true);
		}
		dataHelper = new HappyData(this);
		dataHelper.addBottle(b);
		return b;
	}

	//If the Fuzz option in sharedpreferences is set, then fuzz the GPS location a bit.
	protected void fuzzify(){
		if (Prefs.getFuzz(this)){
			Log.v(TAG, "Fuzz is true!");
			Log.v(TAG, "Old lat:" + GPS_latitude);
			Log.v(TAG, "Old long:" + GPS_longitude);
			
			Random generator = new Random();
			int plusminus = generator.nextBoolean()?-1:1;
			GPS_longitude += plusminus * generator.nextInt(FUZZFACTOR);
			plusminus = generator.nextBoolean()?-1:1;
			GPS_latitude += plusminus * generator.nextInt(FUZZFACTOR);
			
			
			Log.v(TAG, "New lat:" + GPS_latitude);
			Log.v(TAG, "New long:" + GPS_longitude);
		}
	}

	//only show the warning if you actually are in incognito mode	
	protected void fixWarning(){
		
		//if we are not in incognito mode, don't show this warning. That is all.
		if(!Prefs.getIncognito(this)){
			warning.setVisibility(View.INVISIBLE);
			
		}
		else{
			warning.setVisibility(View.VISIBLE);
		}
		
	}
	
	//Disables GPS Managers and Listeners
	protected void onPause() {
		super.onPause();
		removeLocation();
	}

	//Enables GPS Managers and Listeners
	protected void onResume() {
		super.onResume();
		//only show the warning if you actually are in incognito mode
		//We need to recheck onResume because the settings could've changed, then they hit the back button
		fixWarning();
		
		locationStuff();
	}
	
}

