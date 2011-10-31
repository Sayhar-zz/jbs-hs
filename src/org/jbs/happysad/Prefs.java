package org.jbs.happysad;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Prefs extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
	
	public static boolean getIncognito(Context context){
		//You can call this method (Prefs.getIncognito) anywhere. When you do, it return true if incognito mode is on, false if not.
		//Incognito mode = don't show on globalmap
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("privacy",false);
		//slick, eh?
	}

	public static boolean getFuzz(Context context){
		//Fuzz = add a bit of fuzz to GPS coordinates.
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("fuzz",false);
		
	}
	
	
}
