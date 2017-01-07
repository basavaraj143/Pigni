package com.society.gsaienter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.society.classes.ServerData;
import com.society.utils.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CollectionActivity extends ActionBarActivity {

  EditText acctId;
  EditText name;
  EditText obal;
  EditText amt;
  EditText cbal;
  EditText mobile;
  DBHelper db;
  String qry;
  ProgressDialog pDialog;
  String result;
  Button save;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_collection);

    db = new DBHelper(this);
    db.createTableTransactions();

    acctId = (EditText) findViewById(R.id.tacctId);
    name = (EditText) findViewById(R.id.tname);
    obal = (EditText) findViewById(R.id.tobal);
    amt = (EditText) findViewById(R.id.tamt);
    cbal = (EditText) findViewById(R.id.tcbal);
    save = (Button) findViewById(R.id.save);
    save.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //uploadData();
        saveAndPrintData();
      }
    });
    mobile = (EditText) findViewById(R.id.tmobile);
    acctId.setOnFocusChangeListener(new OnFocusChangeListener() {

      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        // TODO Auto-generated method stub
        if (!hasFocus) {
          setTextBoxes();
        }
      }
    });


    amt.setOnFocusChangeListener(new OnFocusChangeListener() {

      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        // TODO Auto-generated method stub
        if (!hasFocus) {
          setCloseBalText();
        }
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.collection, menu);
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

  public void saveAndPrintData() {
    String lacctid = acctId.getText().toString().trim();
    String lname = name.getText().toString().trim();
    String lamt = amt.getText().toString().trim();
    String lmob = mobile.getText().toString().trim();
    String ldate = getDate();
    String ltime = getTime();
    String lobal = obal.getText().toString().trim();
    String close_bal = cbal.getText().toString().trim();

    if (lacctid.isEmpty() || lname.isEmpty() || lamt.isEmpty() || lobal.isEmpty() || close_bal.isEmpty()) {
      Toast.makeText(this, "ALL FIELDS SHOULD BE FILLED", Toast.LENGTH_SHORT).show();
      return;
    }


    qry = "insert into transactions values('" + lacctid + "','" + lname + "','" + lamt + "','" + lobal + "','" + close_bal + "','" + lmob + "','"
        + ldate + "','" + ltime + "','F')";

	/*	Log.i("QRY", qry);

		
		Toast.makeText(this, "ADDED", Toast.LENGTH_SHORT).show();
		
		String message = "SAI ENTER PIGMI SOLUTION\n**************************\n\n"+
				"ACCT NO:"+lacctid+
				"\nName:"+lname+
				"\nO.Balance:"+lobal+
				"\nAMOUT:"+lamt+
				"\nDATE:"+ldate+
				"\nTime:"+ltime+
				"\n"+
				"*********************"+
				"\n  THANK YOU\n"
				+"*********************\n";
		*//*
     * Print and Message if mob no provided
		 * *//*
    printMessage(message);*/
    Cursor rs = db.executeQuery(qry);
    uploadData();

    if (rs == null) {
      alertDisplay("RECORD ALREADY EXIST");
      return;
    }
    resetTextBox(acctId);
    if (lmob.equalsIgnoreCase("")) {
      Toast.makeText(this, "Please update mobile No", Toast.LENGTH_SHORT).show();
      return;
    }
  }

  private void uploadData() {
    String qry = "select * from transactions where status='F'";
    Cursor rs = db.executeQuery(qry);
    Log.i("TR.CNT", rs.getCount() + "");
    rs.moveToFirst();
    int cnt = 0;

    JSONArray jsonArray = new JSONArray();
    JSONObject jsonObject;


    SharedPreferences spref = getSharedPreferences("details", Context.MODE_PRIVATE);
    String agent_id = spref.getString("agent_id", "");
    String soc_id = spref.getString("soceity_id", "");
/*    while (rs.moveToNext()) {*/
    for (int i =0; i< rs.getCount(); i++){
      try {
        String uctid = rs.getString(0);
        String uname = rs.getString(1);
        String uamt = rs.getString(2);
        String obal = rs.getString(3);
        String cbal = rs.getString(4);
        String umob = rs.getString(5);
        String udat = rs.getString(6);
        String utime = rs.getString(7);
        String timestamp = getStamp(udat, utime);
        String sfdate = getServerFormatDate(udat);
        int type_deposite = 1;
        int status = 1;

        jsonObject = new JSONObject();
        jsonObject.put("soc_id", soc_id);
        jsonObject.put("agent_id", agent_id);
        jsonObject.put("uctid", uctid);
        jsonObject.put("uamt", uamt);
        jsonObject.put("obal", obal);
        jsonObject.put("cbal", cbal);
        jsonObject.put("umob", umob);
        jsonObject.put("udat", udat);
        jsonObject.put("utime", utime);
        jsonObject.put("uname", uname);
        jsonObject.put("sfdate", sfdate);
        jsonObject.put("type_deposite", type_deposite);
        jsonObject.put("status", status);
        jsonObject.put("timestamp", timestamp);
        jsonArray.put(jsonObject);
     //   cnt++;

      } catch (JSONException e) {
        e.printStackTrace();
      }

			/*String qry_pgmi_trasc = "insert into pigmi_transactions values"
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
			cnt++;*/
    }

    //Toast.makeText(this, "UPDATED", Toast.LENGTH_SHORT).show();
    try {
      uploadDataToServer(jsonArray);

    } catch (JSONException e) {
      e.printStackTrace();
    }

  }


  private void loadToServer(final ServerData sd, final String qry) throws Exception {
    Thread t = new Thread(new Runnable() {

      @Override
      public void run() {
        // TODO Auto-generated method stub
        try {
          result = sd.executeServerQuery(getString(R.string.SERVER_ADRESS) + "/gscripts/insertQuery.php", qry);
          Log.i("PhpInfo", result);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    t.start();
    try {
      t.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

  private String getStamp(String dat, String time) {
    String d[] = dat.split("/");
    String ndate = d[2] + "-" + d[1] + "-" + d[0]; //Date in YYYY-MM-DD
    String stamp = ndate + " " + time;
    return stamp;
  }

  private String getServerFormatDate(String ldate) {
    String d[] = ldate.split("/");
    String ndate = d[2] + "-" + d[1] + "-" + d[0];
    return ndate;
  }

  private void uploadDataToServer(JSONArray array) throws JSONException {
    String tag_json_arry = "json_array_req";
    String url = getString(R.string.SERVER_ADRESS) + "/gscripts/insert_transactions.php";
    pDialog = new ProgressDialog(CollectionActivity.this);
    pDialog.setMessage("Loading...");
    pDialog.show();

    JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST, url, array,
        new Response.Listener<JSONArray>() {
          @Override
          public void onResponse(JSONArray response) {
            try {
              String status = response.getJSONObject(0).getString("code");
              //String status1 = response.getJSONObject(0).getString("messge2");
              pDialog.dismiss();
              if (status.equals("1")) {
                String updateQry = "update transactions SET status='T'";
                Cursor cr = db.executeQuery(updateQry);
                Toast.makeText(CollectionActivity.this, "Success", Toast.LENGTH_LONG).show();
              } else {
                Toast.makeText(CollectionActivity.this, "Uploading fail try again", Toast.LENGTH_LONG).show();
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }


          }
        }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        VolleyLog.d("error", "Error: " + error.getMessage());
        error.fillInStackTrace();
        Toast.makeText(CollectionActivity.this, error.toString(), Toast.LENGTH_LONG).show();
        pDialog.hide();
      }
    });
// Adding request to request queue
    AppController.getInstance().addToRequestQueue(req, tag_json_arry);
  }

  private void printMessage(String message) {
    Intent printer = new Intent(this, BlueToothPrinterApp.class);
    printer.putExtra("message", message);
    startActivityForResult(printer, 2);
  }

  public void resetTextBox(View V) {
    acctId.setText("");
    name.setText("");
    amt.setText("");
    mobile.setText("");
    obal.setText("");
    cbal.setText("");
  }

  private String getDate() {
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Date date = new Date();
    String pdate = dateFormat.format(date);
    return pdate;
  }

  private String getTime() {
    Date date = new Date();
    return new SimpleDateFormat("hh:mm:ss").format(date);
  }


  private void setCloseBalText() {
    String openBal = obal.getText().toString().trim();
    String tamt = amt.getText().toString().trim();

    if (openBal.equalsIgnoreCase("") || tamt.equalsIgnoreCase(""))
      return;

    int iopenBal = 0;
    int iamt = 0;
    try {
      iopenBal = Integer.parseInt(openBal);
      iamt = Integer.parseInt(tamt);
      int icloseBal = iopenBal + iamt;
      cbal.setText(icloseBal + "");
    } catch (NumberFormatException n) {
      alertDisplay("Dont enter fraction");
      cbal.setText("");
    }
  }

  private void setTextBoxes() {
    String acct_no = acctId.getText().toString().trim();
    String qry = "SELECT * from customerdetails where acct_no = '" + acct_no + "'";
    Cursor rs = db.executeQuery(qry);
    if (rs.getCount() == 0) {
      alertDisplay("Please Enter valid account");
      return;
    }
    rs.moveToFirst();
    String cname = rs.getString(1);
    String obalance = rs.getString(2);
    String mob = rs.getString(3);

    //Returns correct opening balance
    obalance = getCorrectObal(obalance);

    name.setText(cname);
    obal.setText(obalance);
    mobile.setText(mob);
  }

  private String getCorrectObal(String obal) {
    String acct_no = acctId.getText().toString().trim();
    String qry = "SELECT MAX(cbalance) FROM transactions WHERE acct_id='" + acct_no + "'";
    Cursor rs = db.executeQuery(qry);
    if (rs.getCount() == 0)
      return obal;
    rs.moveToFirst();
    String loc_obal = (rs.getString(0) == null ? "0" : rs.getString(0));

    try {
      int iobal = Integer.parseInt(obal);
      int liobal = Integer.parseInt(loc_obal);
      if (liobal > iobal)
        return liobal + "";
      else
        return iobal + "";
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private void alertDisplay(String message) {
    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle("Alert");
    alertDialog.setMessage(message);
    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });
    alertDialog.show();
  }

  private void sendMessage(String mobNo, String message) {
    try {
      SmsManager smsManager = SmsManager.getDefault();
      smsManager.sendTextMessage(mobNo, null, message, null, null);
    } catch (Exception e) {
      Toast.makeText(this, "MESSAGE NOT SENT", Toast.LENGTH_SHORT).show();
      return;
    }

    Toast.makeText(this, "MESSAGE SENT", Toast.LENGTH_SHORT).show();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    int status = BlueToothPrinterApp.getPrintsatus();
    if (status == 0) {
      Cursor rs = db.executeQuery(qry);
      if (rs == null) {
        alertDisplay("RECORD ALREADY EXIST");
        return;
      }
    }
  }

}
