package com.society.gsaienter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.database.Cursor;
import android.nfc.FormatException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.society.classes.Item;
import com.society.classes.MonthAbstarctAdapter;
import com.society.classes.TodayCollectionAdapter;

public class OtherReportActivity extends Activity implements OnClickListener,OnItemSelectedListener
{
	final String MONTH_ABSTRACT = "MONTHLY ABSTRACT";
	final String PENDING_COLLN = "PENDING COLLECTION";
	
	Spinner actions,months,years;
	Button babstract;
	LinearLayout abstractLayout;
	LinearLayout pendCollnLayout;
	ListView abstractlist ;
	DBHelper db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_other_report);
		
		actions = (Spinner)findViewById(R.id.action);
	    ArrayAdapter<String> action_array_Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.actions));
	    actions.setAdapter(action_array_Adapter);
	    actions.setOnItemSelectedListener(this);
	    
	    
	    db = new DBHelper(this);
	    
	    abstractLayout = (LinearLayout)findViewById(R.id.abstractLayout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.other_report, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			case R.id.babstract: viewMonthlyAbstract();
								 break;
			default:break;
		}
	}
	
	private void showProperLayout()
	{
			abstractLayout.setVisibility(View.VISIBLE);
			
			months = (Spinner)findViewById(R.id.month);
			ArrayAdapter<String> array_Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.months));
			months.setAdapter(array_Adapter);
			
			years = (Spinner)findViewById(R.id.year);
			array_Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.years));
			years.setAdapter(array_Adapter);
			
			babstract = (Button)findViewById(R.id.babstract);
			babstract.setOnClickListener(this);
			
			abstractlist = (ListView)findViewById(R.id.listview);
		
	}
	
	private void viewMonthlyAbstract()
	{
		String month = months.getSelectedItem().toString().trim();
		int imonth = 0;
		try
		{
			imonth = Integer.parseInt(month);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(imonth<10)
			month = "0"+imonth;
		
		String year = years.getSelectedItem().toString().trim();
		ArrayList<Item> items = new ArrayList<Item>();
		
		String abqry = "SELECT transactions.date as DAY,SUM(amount) as COLLECTION FROM transactions "
						+ "WHERE transactions.date LIKE '%/"+month+"/"+year+"' GROUP BY transactions.date";
		Log.i("AB_QRY", abqry);
		
		Cursor rs = db.executeQuery(abqry);
		if(rs.getCount() == 0)
		{
			Toast.makeText(this, "DATA NOT AVAILABLE", Toast.LENGTH_SHORT).show();
			return;
		}
		rs.moveToFirst();
		
		while(!rs.isAfterLast())
		{
			Item item = new Item();
			item.setLdate(rs.getString(0));
			item.setAmt(rs.getString(1));
			items.add(item);
			rs.moveToNext();
		}
		
		MonthAbstarctAdapter adpter = new MonthAbstarctAdapter(this, items);
		abstractlist.setAdapter(adpter);
	}
	
	
	private String getTodayDate()
	{
		Date date = new Date();
    	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    	String today = dateFormat.format(date);
    	return today;
	}

	@Override
	public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position,
			long arg3) 
	{
		String itemSelcted = parentView.getItemAtPosition(position).toString().trim();
		
		if(itemSelcted.equalsIgnoreCase(MONTH_ABSTRACT))
		{
			showProperLayout();
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}
