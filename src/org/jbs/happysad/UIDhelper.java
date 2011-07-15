package org.jbs.happysad;

import android.content.SharedPreferences;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class UIDhelper{
	private long myID;
	private String USER_DATA;

	public UIDhelper(){
		myID = -1;
	}
	
	public long getUID(){
		return myID;
	}
	
	public long getSetUID(SharedPreferences sp, Context ctx){
		if (myID >= 0) { return myID;}
		final AccountManager manager = AccountManager.get(ctx);
		final Account[] accounts = manager.getAccounts();   

		if (accounts.length > 1){

			//ok so we know there's at least one account being used. 
			//let's use it.
			String username = new String(accounts[0].name);

			//is the username is stored in shared preferences, we know that can get the UID
			//get the ID given the username (this way we can save multiple users
			long uid = sp.getLong(username, -1);
			if (uid < 0){	
				NetHelper NH = new NetHelper();
				long UID = NH.getID(username);
				SharedPreferences.Editor editor = sp.edit();
				editor.putLong("usernameLong", UID);
				editor.putString("usernameString", username); 
				editor.commit();
				//then we call nethelper methods set the id from the returned thing return
				myID = UID;
			}
			else{
				myID =  sp.getLong( "usernameLong", -1);
			}
		}
		/*else{
			Context context = ctx;
			String loginPrompt = getString(R.string.error_login_message);
			CharSequence text = loginPrompt;
			Toast toast = Toast.makeText(context, text, 1000);
			toast.show()

		}*/
		return myID;
	}
}

