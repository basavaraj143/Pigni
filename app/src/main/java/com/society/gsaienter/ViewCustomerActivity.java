package com.society.gsaienter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.society.classes.CustomerAdapter;
import com.society.classes.Item;
import com.society.classes.MyEditTextDatePicker;

public class ViewCustomerActivity extends ActionBarActivity implements OnClickListener{

	EditText vdate,acctId,endDate;
	LinearLayout view,labels;
	TextView cdetails;
	Button print,submit,reset;
	ListView list_details;
	DBHelper db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_customer);
		
		vdate = (EditText)findViewById(R.id.vdate);
		new MyEditTextDatePicker(this,R.id.vdate);
		
		endDate = (EditText)findViewById(R.id.vEnddate);
		new MyEditTextDatePicker(this,R.id.vEnddate);
		
		acctId = (EditText)findViewById(R.id.vacctId);
		view = (LinearLayout)findViewById(R.id.detailsLayout);
		
		cdetails = (TextView)findViewById(R.id.cdetails);
		
		submit = (Button)findViewById(R.id.submit);
		reset = (Button)findViewById(R.id.reset);
		print = (Button)findViewById(R.id.print);
		list_details = (ListView)findViewById(R.id.listview);
		labels = (LinearLayout)findViewById(R.id.labels);
		
		submit.setOnClickListener(this);
		reset.setOnClickListener(this);
		print.setOnClickListener(this);
		view.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_customer, menu);
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
			case R.id.submit: viewDetails();
								break;
			case R.id.reset: resetTextBoxes();
								break;
			case R.id.print: printMessage();
		}
	}
	
	private void viewDetails()
	{
		db = new DBHelper(this);
		
		String acct_no = acctId.getText().toString().trim();
		String dat = vdate.getText().toString().trim();
		String end_date = endDate.getText().toString().trim();
		
		if(acct_no.equalsIgnoreCase("")||dat.equalsIgnoreCase("")||end_date.equalsIgnoreCase(""))
		{
			Toast.makeText(this, "Please Enter account no/date", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(dat.equalsIgnoreCase(end_date))
		{
			viewSingleDetail();
			return;
		}
		
		viewMultipleDetails();
		
	}
	
	private void viewMultipleDetails()
	{
		cdetails.setVisibility(View.GONE);
		print.setVisibility(View.GONE);
		list_details.setVisibility(View.VISIBLE);
		labels.setVisibility(View.VISIBLE);
		
		String acct_no = acctId.getText().toString().trim();
		String str_date = vdate.getText().toString().trim();
		String end_date = endDate.getText().toString().trim();
		
		ArrayList<Item> ItemArray = new ArrayList<Item>();
		
		List<String> dates = null;
		try
		{
			dates = getDatesBetweenDates(str_date,end_date);
		}
		catch(ParseException e)
		{
			e.printStackTrace();
		}
	
		for(String dat:dates)
		{
			String qry = "select * from transactions where acct_id='"+acct_no+"' and transactions.date='"+dat+"'";
			Cursor rs = db.executeQuery(qry);
			if(rs.getCount()==0)
			{
				continue;
			}
			rs.moveToFirst();
			
			Item item = new Item();
			item.setAmt(rs.getString(2));
			item.setLobal(rs.getString(3));
			item.setLcbal(rs.getString(4));
			item.setLdate(rs.getString(6));
			item.setLtime(rs.getString(7));
			ItemArray.add(item);
			
		}
		
		CustomerAdapter cust = new CustomerAdapter(this,ItemArray);
		
		list_details.setAdapter(cust);
	}
	
	private void viewSingleDetail()
	{
		list_details.setVisibility(View.GONE);
		cdetails.setVisibility(View.VISIBLE);
		print.setVisibility(View.VISIBLE);
		labels.setVisibility(View.GONE);
		
		String acct_no = acctId.getText().toString().trim();
		String dat = vdate.getText().toString().trim();
		String qry = "select * from transactions where acct_id='"+acct_no+"' and transactions.date='"+dat+"'";
		Cursor rs = db.executeQuery(qry);
		
		if(rs.getCount() == 0)
		{
			Toast.makeText(this, "NO SUCH ACCOUNT FOUND", Toast.LENGTH_SHORT).show();
			print.setVisibility(View.GONE);
			return;
		}
		rs.moveToFirst();
		
		
		String details = "C.Name:"+rs.getString(1) 
						+"\nAmount:"+rs.getString(2)
						+"\nO.Balance:"+rs.getString(3)
						+"\nC.Balance:"+rs.getString(4)
						+"\nDate:"+rs.getString(6)
						+"\nTime:"+rs.getString(7);
		cdetails.setText(details);
	}
	
	private void printMessage()
	{
		String message = "******DUPLICATE*****\n"+cdetails.getText().toString().trim();
		if(message.isEmpty())
		{
			Toast.makeText(this, "No Matter to print", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent printer = new Intent(this,BlueToothPrinterApp.class);
		printer.putExtra("message", message);
		startActivity(printer);
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
	
	private void resetTextBoxes()
	{
		vdate.setText("");
		endDate.setText("");
	}
	
}
