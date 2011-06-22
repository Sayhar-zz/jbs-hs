package org.jbs.happysad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
//import android.util.Log;

/**
 * Creates the Dashboard Activity
 * @author HS
 */
public class Dashboard extends Activity implements OnClickListener{
	//for debugging purposes, delete after debugging.
	//private static final String TAG = "dashboard";
	
	/**
	 * Initializes activity
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
      	setContentView(R.layout.dashboard);
      
		//Finds the update_button view
		View updateButton = findViewById(R.id.update_button);
		updateButton.setOnClickListener(this);
  	  

		//Finds the history_button view
  	  	View histButton = findViewById(R.id.history_button);
  	  	histButton.setOnClickListener(this);
	}
    
    /**
     * Invoked when a view is clicked
     */

	public void onClick(View v) {
		switch(v.getId()) {

		case R.id.update_button:
			Intent i = new Intent(this, Prompt.class);
			i.putExtra("Clicked", "Happy");
			startActivity(i);
			break;

		case R.id.history_button:
			Intent j = new Intent(this, History.class);
			startActivity(j);
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	/**
	 * Invoked when a option is clicked
	 */
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
