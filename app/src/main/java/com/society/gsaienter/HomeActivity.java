package com.society.gsaienter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.society.classes.ServerData;

public class HomeActivity extends ActionBarActivity implements OnClickListener{
	
	Button collection;
	Button report;
	Button upload;
	Button logout;
	DBHelper db;
	boolean serverStat;
	String SERVER_ADDR;
	String result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		collection = (Button)findViewById(R.id.bcollections);
		report = (Button)findViewById(R.id.breport);
		upload = (Button)findViewById(R.id.bupload);
		logout = (Button)findViewById(R.id.blogout);
		db = new DBHelper(this);
		SERVER_ADDR = getString(R.string.SERVER_ADRESS);
		
		logout.setOnClickListener(this);
		collection.setOnClickListener(this);
		report.setOnClickListener(this);
		upload.setOnClickListener(this);
		
	}
	
	@Override
	public void onBackPressed()
	{
		Toast.makeText(this, "PLEASE LOGOUT", Toast.LENGTH_LONG).show();
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
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
			case R.id.bcollections : startCollection();
									  break;
			case R.id.breport : viewReports();
								break;
			case R.id.blogout : logout();
								break;
			case R.id.bupload : uploadData();
								break;
			
			default : break;
		}							
	}
	
	private void startCollection()
	{
		Intent collect = new Intent(this,CollectionActivity.class);
		startActivity(collect);
	}
	
	private void logout()
	{
		Intent main = new Intent(this,MainActivity.class);
		startActivity(main);
		this.finish();
	}
	
	private void viewReports()
	{
		Intent collect = new Intent(this,ReportsActivity.class);
		startActivity(collect);
	}
	
	private void uploadData()
	{
		if(!isServerReachable())
		{
			Toast.makeText(this, "SERVER DOWN", Toast.LENGTH_LONG).show();
			return;
		}
		String qry = "select * from transactions where status='F'";
		Cursor rs = db.executeQuery(qry);
		Log.i("TR.CNT",rs.getCount()+"");
		rs.moveToFirst();
		int cnt = 0;
		
		SharedPreferences spref = getSharedPreferences("details", Context.MODE_PRIVATE);
		String agent_id = spref.getString("agent_id","");
		String soc_id = spref.getString("soceity_id", "");
		ServerData sd = new ServerData();
		while(cnt != rs.getCount())
		{
			String uctid = rs.getString(0);
			String uname = rs.getString(1);
			String uamt = rs.getString(2);
			String obal = rs.getString(3);
			String cbal = rs.getString(4);
			String umob = rs.getString(5);
			String udat = rs.getString(6);
			String utime = rs.getString(7);
			String qry_pgmi_trasc = "insert into pigmi_transactions values"
					+ "('"+soc_id+"','"+agent_id+"','"+uctid+"','"+uamt+"','"+uname+"','"+obal+"','"+udat+"','"+utime+"','"+cbal+"')";
			
			try
			{
				loadToServer(sd,qry_pgmi_trasc);
				if(result.equalsIgnoreCase("failure"))
				{
					Toast.makeText(this, "Retry upload", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return;
			}
			
			String timestamp = getStamp(udat,utime);
			String sfdate = getServerFormatDate(udat);
			int type_deposite = 1;
			int status  = 1;
			String particular = "mCollect";
			String qry_tmp_transc = "INSERT INTO tmp_transaction "
					+ "(`agent_id`, `acc_no`, `date`, `deposit_amount`,`transaction_type`,`particular`, `status`,`society_id`, `EntDt`) "
					+ "VALUES('"+agent_id+"','"+uctid+"','"+sfdate+"','"+uamt+"','"+type_deposite+"','"+particular+"','"+status+"','"+soc_id+"','"+timestamp+"')";
			try
			{
				loadToServer(sd,qry_tmp_transc);
				if(result.equalsIgnoreCase("failure"))
				{
					Toast.makeText(this, "Retry upload", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return;
			}
			
			String updateQry = "update transactions SET status='T' where acct_id='"+uctid+"' AND date='"+udat+"' AND time='"+utime+"'";
			Cursor res1 = db.executeQuery(updateQry);
			rs.moveToNext();
			cnt++;
		}
		
		Toast.makeText(this, "UPDATED", Toast.LENGTH_SHORT).show();
	}


	
	private void loadToServer(final ServerData sd,final String qry)throws Exception
	{
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try
				{
					result = sd.executeServerQuery(SERVER_ADDR+"/gscripts/insertQuery.php",qry);
					Log.i("PhpInfo", result);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		t.start();
		try
		{
			t.join();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private boolean isServerReachable()
	{
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {

				serverStat =  ServerData.isURLReachable(getApplicationContext(), SERVER_ADDR);
			}
		});
		t.start();
		try
		{
			t.join();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
			serverStat = false;
		}
		return serverStat;
	}
	
	private String getStamp(String dat,String time)
	{
		String d[] = dat.split("/");
		String ndate = d[2]+"-"+d[1]+"-"+d[0]; //Date in YYYY-MM-DD 
		String stamp = ndate+" "+time;
		return stamp;
	}
	
	private String getServerFormatDate(String ldate)
	{
		String d[] = ldate.split("/");
		String ndate = d[2]+"-"+d[1]+"-"+d[0];
		return ndate;
	}
}

