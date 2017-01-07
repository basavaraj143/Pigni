package com.society.classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ServerData {

	public String executeServerQuery(final String script,String qry) throws Exception
	{
		AESCrypt aes = new AESCrypt();
		String hexqry = AESCrypt.toHexString(qry.getBytes());
		String eqry = aes.encrypt(hexqry);
		
		Log.i("Enc_Qry", eqry);
		String data = URLEncoder.encode("qry", "UTF-8") 
                + "=" +  URLEncoder.encode(eqry, "UTF-8");
		String ehexresult = null,hexresult = null,result = null;
		try
		{
			URL url = new URL(script);
			URLConnection conn = url.openConnection(); 
            conn.setDoOutput(true); 
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream()); 
            wr.write( data ); 
            wr.flush(); 
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            
            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                // Append server response in string
                sb.append(line + "\n");
            }
            reader.close();
            ehexresult =  sb.toString();
            hexresult =  aes.decrypt(ehexresult); 
            result =  AESCrypt.fromHexString(hexresult);         
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	
	static public boolean isURLReachable(Context context,String remoteMachine) {
	    ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        try {
	            URL url = new URL(remoteMachine);   // Change to "http://google.com" for www  test.
	            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
	            urlc.setConnectTimeout(05 * 1000);          // 5 s.
	            urlc.connect();
	            if (urlc.getResponseCode() == 200) {        // 200 = "OK" code (http connection is fine).
	                Log.wtf("Connection", "Success !");
	                return true;
	            } else {
	                return false;
	            }
	        } catch (MalformedURLException e1) {
	            return false;
	        } catch (IOException e) {
	            return false;
	        }
	    }
	    return false;
	}
	
	public static boolean isConnectingToInternet(Context context)
	{
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null) 
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null) 
                  for (int i = 0; i < info.length; i++) 
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
 
          }
          return false;
    }
}
