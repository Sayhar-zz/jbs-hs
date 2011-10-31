package org.jbs.happysad;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;


/**
 * Creates a text-based list of a user's updates, sorted by date created
 * @author HappyTrack
 */
public class History extends ListActivity {
	private HappyData dataHelper;
	private static int[] TO = {R.id.item_text1, R.id.item_text2};
	private static String[] FROM = { "line1","line2" };	
	private SimpleAdapter adapter;
	StringBuilder result;
	private long myID;
	private UIDhelper UIDh;
	private ArrayList<HappyBottle> list;
	
	/**
	 * Initializes Activity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);

		UIDh = new UIDhelper();
		myID =UIDh.getUID();
		 
		dataHelper = new HappyData(this);
		list = dataHelper.getMyHistory(); 
		showUpdates(list);
		
		 
	}
	
	//shows the updates
	private void showUpdates(ArrayList<HappyBottle> l){
		//for each Bottle pull data that will be placed in the hash map
		ArrayList<HashMap<String,String>> newList = new ArrayList<HashMap<String,String>>();
		for (HappyBottle b : l){
			HashMap<String, String> m = new HashMap<String, String>();
			String e = (b.getEmo()>0)?"Happy":"Sad";
			m.put("line1", e + ": " +  b.getMsg());
			m.put("line2", new Timestamp(b.getTime()).toLocaleString() );

			newList.add(m);
		}
		//the adapter makes the updates look nice
		adapter = new SimpleAdapter(this, newList, R.layout.item, FROM, TO); 
		setListAdapter(adapter);
		
		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		//Object o = this.getListAdapter().getItem(position);
		HappyBottle b = list.get(position);
		Intent i = createBundle(b);
		startActivity(i);
	}
	
	protected Intent createBundle(HappyBottle b){
		Intent j = new Intent(this, MyMap.class);
		j.putExtra("GoToMyLocation", true);
		j.putExtra("Run", true);
		j.putExtra("Happy", 1);
		j.putExtra("Sad", 1);
		
		
		j.putExtra("BottleLat", b.getLat());
		j.putExtra("BottleLong", b.getLong());
		j.putExtra("BottleMsg", b.getMsg());
		j.putExtra("BottleEmo", b.getEmo());
		j.putExtra("BottleTime", b.getTime());
		j.putExtra("id", myID);
		return j;
	}
}