package org.jbs.happysad;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.achartengine.ChartFactory;
import org.achartengine.renderer.DefaultRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

/**
 * Day Pie Chart.
 */
public class ChartDay extends AbstractChart {
  /**
   * Executes the chart.
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
	
	//Forms array of all personal(local) bottles.
	HappyData datahelper = new HappyData(context);
	ArrayList<HappyBottle> plottables = datahelper.getMyHistory();
	
	//Saves values.
    double[] values = percentages(plottables);
    
    //Saves colors.
    int[] colors = new int[] { Color.YELLOW, Color.CYAN };
    
    //Creates renderer and sets specifications.
    DefaultRenderer renderer = buildCategoryRenderer(colors);
    renderer.setZoomButtonsVisible(true);
    renderer.setZoomEnabled(true);    
    renderer.setChartTitle("TODAY");
    renderer.setChartTitleTextSize(50);
	renderer.setLabelsTextSize(20);
	renderer.setLegendTextSize(40);
	renderer.setMargins(new int[] {20, 30});
    
    return ChartFactory.getPieChartIntent(context, buildCategoryDataset("Happy Pie", values),
        renderer, "MyChart");
  }
  
  /**
   * Builds array with values per section of pie chart.
   * @param array of bottles
   * @return array of values for chart
   */
  public double [] percentages(ArrayList<HappyBottle> plottables){
		
	  	Iterator<HappyBottle> itr = plottables.iterator(); 
		double happy = 0;
		double sad = 0;
		double [] values = new double[2];
		
		//If there are more bottles.
		while(itr.hasNext()) {     
		   	
			//Call next bottle.
			HappyBottle element = itr.next();
			
			//Saves date, month and year of current bottle.
			int x = new Timestamp (element.getTime()).getMonth() + 1;
			int y = new Timestamp (element.getTime()).getYear() + 1900;
			int z = new Timestamp (element.getTime()).getDate();
			
			//Saves current date, month and year.
			int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
			int year = Calendar.getInstance().get(Calendar.YEAR);
			int date = Calendar.getInstance().get(Calendar.DATE);
		     
			//If they correspond.
		   	if (x == month && y == year && z == date){
		   		
		   		//If emotion = happy.
		   		if (element.getEmo() == 1){
		   			happy += 1; 
		   		//If emotion = sad.
			    } else {
			    	sad += 1; 
			    }		
		   	}	
		} 
		
		//Get percentages of happy's and sad's.
		double happyprctg = (happy * 100) / (happy + sad);
		double sadprctg = 100 - happyprctg;
		
		//Round them both to 2 decimals.
		int happytransf = (int) (happyprctg * 100);
		int sadtrans = (int) (sadprctg * 100);
		happyprctg = (double) happytransf / 100;
		sadprctg = (double) sadtrans / 100;
		
		//Add them to the array.
		values[0] = happyprctg;
		values[1] = sadprctg;
		
		return values;
	}
}	
