package com.society.gsaienter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper 
{
	public static final String DATABASE_NAME = "GSaiPigmi15.db";
	
	public DBHelper(Context context)
	{
		super(context, DATABASE_NAME, null, 1);

	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		db.execSQL("DROP TABLE IF EXISTS transactions");
	      onCreate(db);
	}
	
	public boolean insertTransaction(String cust_id,String cust_name,String amount,String mobile_no,String date,String time)
	{
	      SQLiteDatabase db = this.getWritableDatabase();
	      ContentValues contentValues = new ContentValues();
	      contentValues.put("cust_id", cust_id);
	      contentValues.put("cust_name", cust_name);
	      contentValues.put("amount", amount);	
	      contentValues.put("mobile_no", mobile_no);
	      contentValues.put("date", date);
	      contentValues.put("time", time);
	      long flag = db.insert("transactions", null, contentValues);
	      
	      if(flag == -1)
	    	  return false;
	      return true;
	}
	
	public int numberOfRows(String tableName)
	{
	      SQLiteDatabase db = this.getReadableDatabase();
	      int numRows = (int) DatabaseUtils.queryNumEntries(db, tableName);
	      return numRows;
	}

	public Cursor getData(String id)
	{
	     SQLiteDatabase db = this.getReadableDatabase();
	     String qry = "select * from transactions where cust_id='"+id+"'"; 
	     Log.i("QRY",qry);
	     Cursor res =  db.rawQuery(qry, null );
	     return res;
	}
	
	public boolean deleteTable(String tableName)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		try
		{
			db.execSQL("DROP TABLE IF EXISTS transactions");
			return true;
		}
		catch(SQLException e)
		{
			return false;
		}
		
	}
	
	public boolean createAgentDetails()
	{
		
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS agentdetails");
		db.execSQL(
			      "create table if not exists agentdetails " +
			      "(agent_id text primary key, uname text,password text,imei text)"
			      );
		db.close();
		return true;
	}
	
	
	public boolean createTableCustomerDetails()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS customerdetails");
		db.execSQL(
			      "create table if not exists customerdetails " +
			      "(acct_no text primary key, cust_name text,open_bal text, mobile_no text)"
			      );
		return true;
	}
	
	public boolean createTableTransactions()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(
			      "create table if not exists transactions " +
			      "(acct_id text, cust_name text,amount text,obalance text,cbalance text,mobile_no text,date text,time text,status text, "
			      + "primary key(acct_id,date))"
			      );
		db.close();
		return true;
	}
	
	public Cursor getColumnVals(String table,String column)
	{
		 SQLiteDatabase db = this.getReadableDatabase();
	     String qry = "select "+column+" from "+table; 
	     Log.i("QRY",qry);
	     Cursor res =  db.rawQuery(qry, null);
	     Log.i("COUNT_CUST",res.getCount()+"");
	     
	     return res;
	}
	
	public Cursor executeQuery(String qry)
	{
		 SQLiteDatabase db = this.getReadableDatabase();
	     Log.i("QRY",qry);
	     Cursor res =  db.rawQuery(qry, null);
	     try
	     {
	    	 Log.i("COUNT_CUST",res.getCount()+"");
	     }
	     catch(SQLiteConstraintException e)
	     {
	    	 return null;
	     }
	     return res;
	}
	
	public boolean existTable(String tab_Name)
	{
		return false;
	}
	
	public String getPassWord(String uname)
	{
		String qry = "select password from agentdetails where uname='"+uname+"'";
		Cursor res = executeQuery(qry);
		try{
			res.moveToFirst();
			return res.getString(0);
		}
		catch(CursorIndexOutOfBoundsException e)
		{
			return null;
		}
	}
	
	public String getAgentID(String uname)
	{
		String qry = "select agent_id from agentdetails where uname='"+uname+"'";
		Cursor res = executeQuery(qry);
		try{
			res.moveToFirst();
			return res.getString(0);
		}
		catch(CursorIndexOutOfBoundsException e)
		{
			return null;
		}
	}
}
