package com.diabolicalschema.corner;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * KidsListArrayAdapter(Context, String[])
 * @author android
 * 
 * Takes a String[] full of the Kid IDs and returns generated ListView rows, one row per kid.
 * Also attaches the onClick event handlers to each list item, so it can do stuff when you click it.
 * 
 * Used by EditKidsActivity()
 * 
 */
public class KidsListArrayAdapter_old extends ArrayAdapter<Kid> {
	private final Context context;
	private final List<String> kidsIDs;
	private final List<Kid> kidsList;

	public KidsListArrayAdapter_old(Context context, List<Kid> kidsList) {
		super(context, R.layout.list_item_edit_kids, kidsList);
		Log.d("KidsListArrayAdapter()", "");
		this.context = context;
		this.kidsIDs = kids2IDsList(kidsList);
		this.kidsList = kidsList;
		
	}

	/**
	 * kids2array(List<Kid>)
	 * returns an array with the IDs of the kids in the List
	 */
	private List<String> kids2IDsList(List<Kid> kidsListIn){
		// Get the list of kids' IDs and convert them into strings
		// Stick them in an array and return it.

		ArrayList<String> kidIDsList = new ArrayList<String>();
		for (Kid x : kidsListIn)
			{
				kidIDsList.add(String.valueOf(x.id));
				Log.d("kidIDsList",String.valueOf(x.id));
				
			}
		
		// Return the kids' IDs in an array
		//return (String[]) kidIDsList.toArray(new String[0]);
		return kidIDsList;
	}
	
	/**
	 * getView(int, View, ViewGroup)
	 * 
	 * Pass it an index, it finds the ID at that offset in the String[] values
	 * Then, it uses the ID to look up information about the Kid with that ID
	 * and generates/returns a single row for a ListView
	 * 
	 * Seems like serious overkill for what I'm doing, but whatever.
	 * 
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// Get the Kid's ID from the array, using the position passed to us
		Kid kid = kidsList.get(position);
		String kid_id = String.valueOf(kid.id);
		
		Log.d("KidsListArrayAdapter()","getView() position:" + position + " id:" + kid_id + " name:" + kid.name);

		
		// Create a LayoutInflater, use it to make a View from the layout XML
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_item_edit_kids, parent, false);
		rowView.setTag(position);
		
		// Use the IDs of the Views within the layout to get handles on them so we can manipulate them
		TextView textView_name = (TextView) rowView.findViewById(R.id.kid_name);
		TextView textView_minutes = (TextView) rowView.findViewById(R.id.kid_minutes);
		ImageView imageView_delete = (ImageView) rowView.findViewById(R.id.kid_delete);
		

		
		// ***** Set up all of the various Views in each row of the list *****
		// Set the TextView textView_name to the Kid's name,
		// Set a tag so we can differentiate this TextView when someone clicks it
		// and set up the onClick event handler so we can do stuff when the name is clicked.
		textView_name.setText(kid.name);
		textView_name.setTag(kid);
		textView_name.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("KidsListArrayAdapter()","name.onClick():" + ((Kid)v.getTag()).name);

			}
		});
		
		// Do the same setup for textView_minutes
		textView_minutes.setText(String.valueOf(kid.timeout) + " minutes");
		textView_minutes.setTag(kid);
		textView_minutes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("KidsListArrayAdapter()","minutes.onClick():" + ((Kid)v.getTag()).name);

			}
		});
		// And also for imageView_delete
		imageView_delete.setImageResource(R.drawable.delete_icon);
		imageView_delete.setTag(position);
		imageView_delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Get the item number in the list
				int listItemNumber = Integer.parseInt(v.getTag().toString());
				// Use the item number to find the Kid ID
				int kidIDNumber = (kidsList.get(listItemNumber).id);
				Log.d("KidsListArrayAdapter()","delete.onClick() position:" + listItemNumber + " id:" + kidIDNumber);
				
				
				// The user clicked the "Delete" icon, so let's delete the kid.
				// The penalty for accidentally deleting is really not very high,
				// so I'm not going to bother confirming the deletion.
				// Delete the kid from the config
				Config.delKid(kidIDNumber);
				Config.save();

				// Tell everyone that the data changed, so they will refresh/redraw as needed.
				notifyDataSetChanged();
				
			}
		});
		
		// Yay, we're done building all that stuff, let's return a nice fat happy RowView
		return rowView;
	}
} 

