package com.society.gsaienter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.society.classes.CompleteDetailsAdapter;
import com.society.classes.Item;
import com.society.classes.MyEditTextDatePicker;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class ViewDateWiseReport extends Activity implements OnClickListener{

	Button submit;
	Button reset;
	EditText stdate,endate;
	LinearLayout detailLayout;
	ListView lstdetails; 
	DBHelper db;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_date_wise_report);
		
		db = new DBHelper(this);
		
		stdate = (EditText)findViewById(R.id.startdt);
		new MyEditTextDatePicker(this,R.id.startdt);
		
		endate = (EditText)findViewById(R.id.enddt);
		new MyEditTextDatePicker(this,R.id.enddt);
		
		submit = (Button)findViewById(R.id.submit);
		reset = (Button)findViewById(R.id.reset);
		
		submit.setOnClickListener(this);
		reset.setOnClickListener(this);
		
		detailLayout = (LinearLayout)findViewById(R.id.detailsLayout);
		lstdetails = (ListView)findViewById(R.id.listview);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_date_wise_report, menu);
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
		case R.id.submit: showCompleterDetails();
						 break;
		default:
		}
	}
	
	private void showCompleterDetails()
	{
		String str_date = stdate.getText().toString().trim();
		String end_date = endate.getText().toString().trim();
		List<String> dates = new ArrayList<String>();
		ArrayList<Item> ItemArray = new ArrayList<Item>();
		detailLayout.setVisibility(View.VISIBLE);
		
		
		if(str_date.equalsIgnoreCase("") && end_date.equalsIgnoreCase(""))
		{
			Toast.makeText(this, "Enter atleast start date", Toast.LENGTH_SHORT).show();
		}
		
		try
		{
			dates = getDatesBetweenDates(str_date, end_date);
		}
		catch(ParseException e)
		{
			Toast.makeText(this, "Date format wrong", Toast.LENGTH_LONG).show();
		}
		
		for(String dat:dates)
		{
			String qry = "select * from transactions where transactions.date='"+dat+"'";
			Cursor rs = db.executeQuery(qry);
			if(rs.getCount()==0)
			{
				continue;
			}
			rs.moveToFirst();
			
			while(!rs.isAfterLast())
			{
				Item item = new Item();
				item.setAcc_no(rs.getString(0));
				item.setCust_name(rs.getString(1));
				item.setAmt(rs.getString(2));
				item.setLobal(rs.getString(3));
				item.setLcbal(rs.getString(4));
				item.setLdate(rs.getString(6));
				item.setLtime(rs.getString(7));
				ItemArray.add(item);
				rs.moveToNext();
			}
			
		}
		
		CompleteDetailsAdapter adapter = new CompleteDetailsAdapter(this, ItemArray);
		lstdetails.setAdapter(adapter);
		
	}
	
	static private List<String> getDatesBetweenDates(String str_date,String end_date) throws ParseException
	{
		List<Date> dates = new ArrayList<Date>();
		List<String> string_dates = new ArrayList<String>();
		DateFormat formatter ; 

		formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date  startDate = (Date)formatter.parse(str_date); 
		Date  endDate = (Date)formatter.parse(end_date);
		long interval = 24*1000 * 60 * 60; // 1 hour in millis
		long endTime =endDate.getTime() ; // create your endtime here, possibly using Calendar or Date
		long curTime = startDate.getTime();
		while (curTime <= endTime) {
		    dates.add(new Date(curTime));
		    curTime += interval;
		}
		
		for(int i=0;i<dates.size();i++){
		    Date lDate =(Date)dates.get(i);
		    String ds = formatter.format(lDate);   
		    string_dates.add(ds);
		}
		return string_dates;
	}
}
