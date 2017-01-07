package com.society.classes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;

public class MyEditTextDatePicker implements OnClickListener, OnDateSetListener {
	EditText _editText;
	private int _day;
	private int _month;
	private int _birthYear;
	private Context _context;

	public MyEditTextDatePicker(Context context, int editTextViewID) {
		Activity act = (Activity) context;
		this._editText = (EditText) act.findViewById(editTextViewID);
		this._editText.setOnClickListener(this);
		this._context = context;
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		_birthYear = year;
		_month = monthOfYear;
		_day = dayOfMonth;
		updateDisplay();
	}

	@Override
	public void onClick(View v) {
		
		int day = 0;
		int month = 0;
		int	year = 0;
		
		String[] present_date = getDateAndTime();
		
		try
		{
			 day = Integer.parseInt(present_date[0]);
			 month = Integer.parseInt(present_date[1])-1;
			 year = Integer.parseInt(present_date[2]);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		DatePickerDialog dialog = new DatePickerDialog(_context, this, year, month, day);
		dialog.show();

	}
	
	private String[] getDateAndTime()
    {
    	String[] DateTime = new String[2];
    	Date date = new Date();
    	
    	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    	String dates[] = dateFormat.format(date).split("/");
    	return dates;
    }
    

	// updates the date in the birth date EditText
	private void updateDisplay() {

		if(_day < 10 && _month < 10)
		{
			_editText.setText(new StringBuilder()
			// Month is 0 based so add 1
			.append("0").append(_day).append("/").append("0").append(_month + 1).append("/")
			.append(_birthYear).append(" "));
			return;
		}
		
		if(_day < 10 )
		{
			_editText.setText(new StringBuilder()
			// Month is 0 based so add 1
			.append("0").append(_day).append("/").append(_month + 1).append("/")
			.append(_birthYear).append(" "));
			return;
		}
		if(_month < 10)
		{
			_editText.setText(new StringBuilder()
			// Month is 0 based so add 1
			.append(_day).append("/").append("0").append(_month + 1).append("/")
			.append(_birthYear).append(" "));
			return;
		}
		
		_editText.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(_day).append("/").append(_month + 1).append("/")
				.append(_birthYear).append(" "));
	}
}
