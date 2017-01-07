package com.society.gsaienter;

import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
public class BlueToothPrinterApp extends Activity       
{       
    /** Called when the activity is first created. */       
    String message;
    Button printbtn;

	byte FONT_TYPE;
	private static BluetoothSocket btsocket;
	private static OutputStream btoutputstream;
	private static int printsatus;
	TextView dispmessage;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blue_tooth_printer_app);
		message = getIntent().getExtras().getString("message");
		dispmessage = (TextView)findViewById(R.id.displayMessage);
		dispmessage.setText("Printing\n Please Wait....");
		
		//Changes to 0 once print success
		printsatus = -1;
		connect();
	}

	public static int getPrintsatus() {
		return printsatus;
	}
	
	protected void connect() {
		if(btsocket == null){
			Intent BTIntent = new Intent(getApplicationContext(), BTDeviceListActivity.class);
			this.startActivityForResult(BTIntent, BTDeviceListActivity.REQUEST_CONNECT_BT);
		}
		else{
            
			OutputStream opstream = null;
			try {
				opstream = btsocket.getOutputStream();
			} catch (IOException e) { 
				e.printStackTrace();
			}
			btoutputstream = opstream;
			print_bt();
		}

	}


	private void print_bt() {
		try {
			try {
				Thread.sleep(5*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			btoutputstream = btsocket.getOutputStream();

			byte[] printformat = { 0x1B, 0x21, FONT_TYPE };
			btoutputstream.write(printformat);
			String msg = message.toString();
			btoutputstream.write(msg.getBytes());
			btoutputstream.write(0x0D);
			btoutputstream.write(0x0D);
			btoutputstream.write(0x0D);
			btoutputstream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dispmessage.setText("Printing\n Finished\nGo Back");
		printsatus = 0;
	}
	
	private void closeScoket()
	{
		try {
			    btoutputstream.close();
			    btsocket.close();
			} catch (IOException e) {
				// e.printStackTrace();
			}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			if(btsocket!= null){
				btoutputstream.close();
				btsocket.close();
				btsocket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			btsocket = BTDeviceListActivity.getSocket();
			if(btsocket != null){
				print_bt();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}  
}