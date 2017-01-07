package com.society.classes;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.society.gsaienter.R;


public class MonthAbstarctAdapter extends BaseAdapter 
{
	private LayoutInflater inflater;
	  private ArrayList<Item> objects;

	   private class ViewHolder {
	      TextView textView1;
	      TextView textView2;
	   }

	   public MonthAbstarctAdapter(Context context, ArrayList<Item> objects) {
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
	         convertView = inflater.inflate(R.layout.abstract_row, null);
	         holder.textView1 = (TextView) convertView.findViewById(R.id.dat);
	         holder.textView2 = (TextView) convertView.findViewById(R.id.collection);
	         convertView.setTag(holder);
	      } else {
	         holder = (ViewHolder) convertView.getTag();
	      }
	      holder.textView1.setText(objects.get(position).getLdate());
	      holder.textView2.setText(objects.get(position).getAmt());
	      return convertView;
	   } 
}
