package com.society.gsaienter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ReportsActivity extends ActionBarActivity implements OnClickListener{

	Button creports;
	Button datereport;
	Button other_report;
	Button commission;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reports);
		
		creports = (Button)findViewById(R.id.custreport);
		datereport = (Button)findViewById(R.id.datereport);
		other_report = (Button)findViewById(R.id.others);
		commission = (Button)findViewById(R.id.commcalc);
		
		other_report.setOnClickListener(this);
		creports.setOnClickListener(this);
		datereport.setOnClickListener(this);
		commission.setOnClickListener(this);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.reports, menu);
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
		switch (v.getId()) 
		{
			case R.id.custreport: viewCustomerReports();
								break;
								
			case R.id.datereport:viewDateWiseReport();
								 break;

			case R.id.others: otherReports();	
								break;
			case R.id.commcalc: commissionCalculation();
								break;
			default: 
				break;
		}
		
	}
	
	private void viewCustomerReports()
	{
		Intent intent = new Intent(this,ViewCustomerActivity.class);
		startActivity(intent);
	}
	
	private void viewDateWiseReport()
	{
		Intent intent = new Intent(this,ViewDateWiseReport.class);
		startActivity(intent);
	}
	
	private void otherReports()
	{
		Intent intent = new Intent(this,OtherReportActivity.class);
		startActivity(intent);
	}
	
	private void commissionCalculation()
	{
		Intent intent = new Intent(this,ComCalculationActivity.class);
		startActivity(intent);
	}
}
