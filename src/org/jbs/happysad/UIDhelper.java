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
	
	private String USER_DATA;
	public UIDhelper(){
		//
	}
	
	public long getUID(){
		return UIDtoken.INSTANCE.getUID();
	}
	
	public long getSetUID(SharedPreferences sp, Context ctx){
		long tempID = this.getUID(); 
		if ( tempID >=0 ){ return tempID;}
		final AccountManager manager = AccountManager.get(ctx);
		final Account[] accounts = manager.getAccounts();   
        String username = getGmail(accounts); 
		if (!username.equals("no")){

			//ok so we know there's at least one account being used. 
			//let's use it.
			//is the username is stored in shared preferences, we know that can get the UID
			//get the ID given the username (this way we can save multiple users
			long uid = sp.getLong("usernameLong", -1);
			UIDtoken.INSTANCE.setUID(uid);
			if (uid < 0){	
				NetHelper NH = new NetHelper();
				long UID = NH.getID(username);
				SharedPreferences.Editor editor = sp.edit();
				editor.putLong("usernameLong", UID);
				editor.putString("usernameString", username); 
				editor.commit();
				//then we call nethelper methods set the id from the returned thing return
				UIDtoken.INSTANCE.setUID(UID);
				return UID;
			}
			else{
				//myID =  sp.getLong( "usernameLong", -1);
				return -1;
			}
		}
		else{
			Context context = ctx;
			String loginPrompt = ctx.getString(R.string.error_login_message);
			CharSequence text = loginPrompt;
			Toast toast = Toast.makeText(context, text, 1000);
			toast.show();
		}
		return -5;
		
		
	}
	private String getGmail(Account[] accounts){
		String username = "";
		String gmail = "no";
		for(int i = 0; i<accounts.length; i++){
			username = new String(accounts[i].name);
			username.toLowerCase();
			if (username.contains("gmail.com")){
				gmail = username;
			}	
		}
		return gmail;
	}
}

