package com.society.classes;

import java.util.ArrayList;

import com.society.gsaienter.R;
import com.society.gsaienter.R.id;
import com.society.gsaienter.R.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CompleteDetailsAdapter extends BaseAdapter 
{
	  private LayoutInflater inflater;
	  private ArrayList<Item> objects;

	   private class ViewHolder {
	      TextView textView1;
	      TextView textView2;
	      TextView textView3;
	      TextView textView4;
	      TextView textView5;
	      TextView textView6;
	      TextView textView7;
	   }

	   public CompleteDetailsAdapter(Context context, ArrayList<Item> objects) {
	      inflater = LayoutInflater.from(context);
	      this.objects = objects;
	   }

	   public int getCount() {
	      return objects.size();
	   }

	   public Item getItem(int position) {
	      return objects.get(position);
	   }

	   public long getItemId(int position) {
	      return position;
	   }

	   public View getView(int position, View convertView, ViewGroup parent) {
	      ViewHolder holder = null;
	      if(convertView == null) {
	         holder = new ViewHolder();
	         convertView = inflater.inflate(R.layout.row_details,null);
	         holder.textView1 = (TextView) convertView.findViewById(R.id.acc_no);
	         holder.textView2 = (TextView) convertView.findViewById(R.id.cust_name);
	         holder.textView3 = (TextView) convertView.findViewById(R.id.lamt);
	         holder.textView4 = (TextView) convertView.findViewById(R.id.lobal);
	         holder.textView5 = (TextView) convertView.findViewById(R.id.lcbal);
	         holder.textView6 = (TextView) convertView.findViewById(R.id.ldate);
	         holder.textView7 = (TextView) convertView.findViewById(R.id.ltime);
	         convertView.setTag(holder);
	      } else {
	         holder = (ViewHolder) convertView.getTag();
	      }
	      holder.textView1.setText(objects.get(position).getAcc_no());
	      holder.textView2.setText(objects.get(position).getCust_name());
	      holder.textView3.setText(objects.get(position).getAmt());
	      holder.textView4.setText(objects.get(position).getLobal());
	      holder.textView5.setText(objects.get(position).getLcbal());
	      holder.textView6.setText(objects.get(position).getLdate());
	      holder.textView7.setText(objects.get(position).getLtime());
	      return convertView;
	   }

}
