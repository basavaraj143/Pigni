package com.society.gsaienter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.society.classes.ServerData;
import com.society.utils.AppController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements OnClickListener {

  private static final long IMAGE_DELAY = 3000;
  ImageView mainImg;
  EditText tuname;
  EditText tpassword;
  EditText soc_id;
  Button login, config;
  String json_str = null;
  String agent_id = null;
  ServerData data;
  DBHelper db;
  SharedPreferences spref;
  String agent_query;
  boolean serverStat;
  ProgressDialog progDailog;
  ProgressDialog pDialog;
  private String cust_details = null;
  private String SERVER_ADDR;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    loadImage();

    SERVER_ADDR = getString(R.string.SERVER_ADRESS);
    db = new DBHelper(getApplicationContext());
    data = new ServerData();

    if (isIMEIValid()) {
      return;
    }

    //Initialization
    tuname = (EditText) findViewById(R.id.tuname);
    tpassword = (EditText) findViewById(R.id.tpassword);
    soc_id = (EditText) findViewById(R.id.tsocid);
    login = (Button) findViewById(R.id.blogin);
    config = (Button) findViewById(R.id.bconfigure);
    login.setOnClickListener(this);
    config.setOnClickListener(this);
    spref = getSharedPreferences("details", Context.MODE_PRIVATE);

    if (!ServerData.isConnectingToInternet(this)) {
      config.setVisibility(View.GONE);
    } else {
      config.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
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
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.blogin:
        login();
        break;
      case R.id.bconfigure:
        configure();
        break;
      default:
        break;
    }

  }


  private void configure() {
    String utname = tuname.getText().toString().trim();
    String password = tpassword.getText().toString().trim();
    String socid = soc_id.getText().toString().trim();
    if (socid.equalsIgnoreCase("")) {
      Toast.makeText(this, "Socity ID Should not be Empty", Toast.LENGTH_SHORT).show();
      return;
    }

    if (utname.equalsIgnoreCase("")) {
      Toast.makeText(this, "UserName Should not be Empty", Toast.LENGTH_SHORT).show();
      return;
    }

    if (password.equalsIgnoreCase("")) {
      Toast.makeText(this, "Password Should not be Empty", Toast.LENGTH_SHORT).show();
      return;
    }

    if (!ServerData.isConnectingToInternet(this)) {
      Toast.makeText(this, "NO INTERNET", Toast.LENGTH_SHORT).show();
      return;
    }

    if (!isServerReachable()) {
      Toast.makeText(this, "SEREVR NOT REACHABLE", Toast.LENGTH_SHORT).show();
      return;
    }

    /*progDailog = ProgressDialog.show(this, "CONFIGURATION",
        "Configuring.... \nPlease wait....", true);
    new Thread() {
      public void run() {
        try {
          loadAgentCustomerDetails();
        } catch (Exception e) {
        }
        progDailog.dismiss();
      }
    }.start();*/
    try {

      getAgentDetails(soc_id.getText().toString(), tuname.getText().toString(), tpassword.getText().toString());
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
  /*
   * Loads image when app opened
	 */

  private void loadImage() {
    mainImg = (ImageView) findViewById(R.id.mainImg);
    mainImg.postDelayed(new Runnable() {

      @Override
      public void run() {
        // TODO Auto-generated method stub
        mainImg.setVisibility(View.GONE);
      }
    }, IMAGE_DELAY);
  }

  private boolean isIMEIValid() {
    TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    String imei = manager.getDeviceId();
    Log.i("IMEI", imei);
    return false;
  }

  private void login() {
    if (validateUser()) {
      Intent home = new Intent(this, HomeActivity.class);
      startActivity(home);
      this.finish();
    }
  }

  private boolean validateUser() {
    DBHelper db = new DBHelper(this);
    String uname = tuname.getText().toString().trim();
    String password = tpassword.getText().toString().trim();

    if (uname.equalsIgnoreCase("") || password.equalsIgnoreCase("")) {
      alertDisplay("USER NAME/PASS WORD SHOULd NOT BE EMPTY");
      return false;
    }

    String pass = db.getPassWord(uname);

    if (!password.equals(pass) || pass == null) {
      alertDisplay("ENTER VALID USERNAME/PASSWORD");
      return false;
    }
    agent_id = db.getAgentID(uname);

    SharedPreferences.Editor edit = spref.edit();
    edit.putString("agent_id", agent_id);
    edit.commit();
    /* GET SOCIETY ID THAT WAS PREV ADDED */
    String socid = spref.getString("soceity_id", "");


    if (!socid.equalsIgnoreCase(soc_id.getText().toString().trim())) {
      alertDisplay("ENTER VALID SOCIETY ID");
      return false;
    }

    Log.i("AGENT_ID", agent_id);
    return true;
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

  private void loadAgentCustomerDetails() {
    final String socid = soc_id.getText().toString().trim();

    db.createAgentDetails();

    agent_query = "select *from agents where society_id ='" + socid + "'";
    Thread t = new Thread(new Runnable() {

      @Override
      public void run() {

        try {
          json_str = data.executeServerQuery(SERVER_ADDR + "/gscripts/get_agents.php", socid);
        } catch (Exception e) {
          e.printStackTrace();
        }

      }
    });
    t.start();
    try {
      t.join();
    } catch (Exception e) {
      e.printStackTrace();
    }
    Log.i("JSONOBJ", json_str);

    try {
      JSONArray agents = new JSONArray(json_str);
      if (agents.length() == 0) {
        Toast.makeText(this, "Enter valid Soceity Id", Toast.LENGTH_SHORT).show();
        return;
      }

			/*
			 * Add soceity id to Shared Pref
			 */
      SharedPreferences.Editor edit = spref.edit();
      edit.clear();
      edit.putString("soceity_id", socid);
      edit.commit();

      for (int i = 0; i < agents.length(); i++) {
        JSONObject agt = agents.getJSONObject(i);
        String agent_id = agt.getString("agent_id");
        String uname = agt.getString("user_name");
        String pass = agt.getString("password");
        String imei = agt.getString("imei");
        String qry = "insert into agentdetails values ('" + agent_id + "','" + uname + "','" + pass + "','" + imei + "')";
        db.executeQuery(qry);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (!validateUser()) {
      return;
    }

    loadCustomerDetails();
    Log.i("ROW COUNT", db.numberOfRows("agentdetails") + "");
    Toast.makeText(this, "SUCCESS CONFIGURE", Toast.LENGTH_SHORT).show();
  }

  //get Agent Details
  private void getAgentDetails(String socId, String userName, String pwd) throws JSONException {
    String tag_json_arry = "json_array_req";
    String url = SERVER_ADDR + "/gscripts/get_agents.php";
    pDialog = new ProgressDialog(MainActivity.this);
    pDialog.setMessage("Loading...");
    pDialog.show();
    JSONObject object = null;
    JSONArray jsonArray = new JSONArray();
    object = new JSONObject();
    object.put("society_id", socId);
    object.put("user_name", userName);
    object.put("password", pwd);
    jsonArray.put(object);

    SharedPreferences.Editor edit = spref.edit();
    edit.clear();
    edit.putString("soceity_id", socId);
    edit.commit();

    JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST, url, jsonArray,
        new Response.Listener<JSONArray>() {
          @Override
          public void onResponse(JSONArray response) {
            pDialog.hide();
            // Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
            if (response.length() >= 0) {
              try {
                db.createAgentDetails();
                for (int i = 0; i < response.length(); i++) {
                  JSONObject agt = response.getJSONObject(i);
                  String agent_id = agt.getString("agent_id");
                  String uname = agt.getString("user_name");
                  String pass = agt.getString("password");
                  String imei = agt.getString("imei");
                  String qry = "insert into agentdetails values ('" + agent_id + "','" + uname + "','" + pass + "','" + imei + "')";
                  db.executeQuery(qry);
                }
                if (validateUser()) {
                  getCustomersDetailsByAgent(agent_id);
                }
              } catch (JSONException e) {
                e.printStackTrace();
              }
            }
          }
        }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        VolleyLog.d("", "Error: " + error.getMessage());
        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
        pDialog.hide();
      }
    });
// Adding request to request queue
    AppController.getInstance().addToRequestQueue(req, tag_json_arry);
  }

  //getCustomer by agents

  private void getCustomersDetailsByAgent(String agent_id) throws JSONException {
    String tag_json_arry = "json_array_req";
    String url = SERVER_ADDR + "/gscripts/get_customers_by_agent.php";
    JSONObject object = null;
    JSONArray jsonArray = new JSONArray();
    object = new JSONObject();
    object.put("agent_id", agent_id);
    jsonArray.put(object);
    JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST, url, jsonArray,
        new Response.Listener<JSONArray>() {
          @Override
          public void onResponse(JSONArray response) {
            if (response.length() >= 0) {
              try {
                db.createTableCustomerDetails();
                for (int i = 0; i < response.length(); i++) {
                  JSONObject customer = response.getJSONObject(i);
                  String acc_no = customer.getString("acc_no");
                  String first_name = customer.getString("first_name");
                  String mob_no = customer.getString("mobile_number");
                  String open_bal = customer.getString("OPEN_BAL");
                  String qry2 = "INSERT into customerdetails values('" + acc_no + "','" + first_name + "','" + open_bal + "','" + mob_no + "')";
                  db.executeQuery(qry2);
                }
              } catch (JSONException e) {
                e.printStackTrace();
              }
            }
          }
        }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        VolleyLog.d("", "Error: " + error.getMessage());
        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
        pDialog.hide();
      }
    });
// Adding request to request queue
    AppController.getInstance().addToRequestQueue(req, tag_json_arry);
  }


  private void loadCustomerDetails() {
    if (!validateUser())
      return;
    db.createTableCustomerDetails();
    final String qry = "SELECT user_account.acc_no,user.first_name,user.mobile_number,transaction.balance as OPEN_BAL " +
        "FROM user,user_account,transaction " +
        "where user.user_id = user_account.user_id and user_account.agent_id ='" + agent_id + "' AND " +
        "transaction.agent_id = user_account.agent_id and transaction.acc_no = user_account.acc_no AND " +
        "transaction.balance_status = 1 " +
        "ORDER BY `user_account`.`acc_no` ASC";
    try {
      Thread t = new Thread(new Runnable() {

        @Override
        public void run() {
          // TODO Auto-generated method stub
          try {
            cust_details = data.executeServerQuery(SERVER_ADDR + "/gscripts/retrive_data.php", qry);
            Log.i("CUST_DET", cust_details);
          } catch (Exception e) {
            e.printStackTrace();
          }

        }
      });
      t.start();
      t.join();
    } catch (Exception e) {
      e.printStackTrace();
    }

    Log.i("userData", qry);

    try {
      JSONArray custs = new JSONArray(cust_details);
      for (int i = 0; i < custs.length(); i++) {
        JSONObject customer = custs.getJSONObject(i);
        String acc_no = customer.getString("acc_no");
        String first_name = customer.getString("first_name");
        String mob_no = customer.getString("mobile_number");
        String open_bal = customer.getString("OPEN_BAL");
        String qry2 = "INSERT into customerdetails values('" + acc_no + "','" + first_name + "','" + open_bal + "','" + mob_no + "')";
        db.executeQuery(qry2);
      }

      Cursor cr = db.executeQuery("select * from customerdetails");
      Log.i("AG CNT", cr.getCount() + "");
    } catch (JSONException j) {
      j.printStackTrace();
    }
  }

  private boolean isServerReachable() {
    Thread t = new Thread(new Runnable() {

      @Override
      public void run() {

        serverStat = ServerData.isURLReachable(getApplicationContext(), SERVER_ADDR);
      }
    });
    t.start();
    try {
      t.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
      serverStat = false;
    }
    return serverStat;
  }

}
