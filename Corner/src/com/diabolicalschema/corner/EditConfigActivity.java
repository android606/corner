package com.diabolicalschema.corner;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.ListActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.content.Context;

/** EditConfigActivity
 * 
 * This provides the user's primary interface to modify the configuration stored in the Config class.
 * 
 * At this time, that includes selecting, modifying, adding, and removing children.
 * 
 * @author android606
 * @see com.diabolicalschema.corner.Config
 * 
 */
public class EditConfigActivity extends ListActivity {

	/*
	 * ACTIVITY EVENT HANDLERS
	 */
	/** onCreate
	 * Happens when the Activity is first created
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * 
	 * Sets up the KidsListArrayAdapter and binds it to this ListActivity
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_kids);

		// Instantiate kidsListAdapter and bind it to this ListActivity so it can provide populated ListRows when the
		// Activity asks for them.
		ConfigListArrayAdapter kidsListAdapter = new ConfigListArrayAdapter(this, getConfigList());
		this.setListAdapter(kidsListAdapter);

	}

	/** onListItemClick
	 * Happens when an item in the list is clicked
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override 
	public void onListItemClick(ListView l, View v, int position, long id){
		// Do stuff when an item is clicked
		Log.d(this.getClass().getName(),"Clicked");

	}

	/** onCreateOptionsMenu()
	 * Happens when the "Menu" button is pressed
	 * @return true - Always has to return true;
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// This only happens when the user presses the "Menu" button
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.edit_kids, menu);
		return true;
	}

	/** onOptionsItemSelected()
	 * Happens if someone actually selects an item in the Options Menu
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/** getConfigList() 
	 * This generates the list of the items for the ListView 
	 * 
	 */
	public List<ConfigListItem> getConfigList() {
		List<ConfigListItem> listItems = new ArrayList<ConfigListItem>();
		List<Kid> listKids = Config.listKids();
		
		for(Kid x: listKids){
			ConfigListItem tempItem = new ConfigListItem();
			tempItem.kid = x;
			listItems.add(tempItem);
		}
		
		return listItems;		
	}
	
	static class ConfigListItem {
		public Kid kid;
		public boolean isKid() {
			if(kid == null){
				return false;
			}
			return true;
		}
		public TextView tvTitle;
		public TextView tvDescription;
		public ImageView ivEditIcon;
		public ImageView ivDeleteIcon;
	}

	/** KidsListArrayAdapter(Context, String[])
	 * @author android606
	 * 
	 * Gets all of the Kid IDs from Config and returns generated ListView rows, one row per kid.
	 * Also attaches the onClick event handlers to each list item, so it can do stuff when you click it.
	 * 
	 * Used by EditKidsActivity()
	 */
	public class ConfigListArrayAdapter extends ArrayAdapter<ConfigListItem> {
		private final Context context;
		//private final List<String> kidsIDs;
		private final List<ConfigListItem> configList;

		public ConfigListArrayAdapter(Context context, List<ConfigListItem> configList) {
			super(context, R.layout.list_item_edit_kids, configList);
			Log.d("ConfigListArrayAdapter()", "");
			this.context = context;
			this.configList = configList;	
		}

		/** getView(int, View, ViewGroup)
		 * 
		 * Pass it an index (position), it returns the View that's supposed to be at that place in the list.
		 * Generates/returns a single row for a ListView
		 * 
		 * The first part of the list is a list of kids.  Following that is an "Add" button, and then other preferences.
		 * 
		 * Seems like serious overkill for what I'm doing, but okay.
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// The RowView we're going to return, once we're done filling it out.
			View rowView = new View(parent.getContext());
							
			if(configList.get(position).isKid()){
				// This is a kid, so we need to ask getViewFromKid to make a ListItem for us.
				rowView = getViewFromKid(position, convertView, parent, configList);
			}
			else {
				// TODO logic to draw other types of list items
			
				Log.d("ConfigListArrayAdapter()","getView() position:" + position + " is a regular config list item." );
//
//				// Create a LayoutInflater, use it to make a View from the layout XML
//				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				rowView = inflater.inflate(R.layout.list_item_add_kid, parent, false);
//				rowView.setTag(position);
//
//				// Use the IDs of the Views within the layout to get handles on them so we can manipulate them
//				TextView textView_name = (TextView) rowView.findViewById(R.id.kid_name);
//				TextView textView_minutes = (TextView) rowView.findViewById(R.id.kid_minutes);
//				ImageView imageView_delete = (ImageView) rowView.findViewById(R.id.kid_delete);
//
//
//				// ***** Set up all of the various Views in each row of the list *****
//				// Set the TextView textView_name to the Kid's name,
//				// Set a tag so we can differentiate this TextView when someone clicks it
//				// and set up the onClick event handler so we can do stuff when the name is clicked.
//				textView_name.setText(kid.name);
//				textView_name.setTag(kid);
//				textView_name.setOnClickListener(new View.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						// TODO Something when the kid's name is clicked
//						Log.d("KidsListArrayAdapter()","name.onClick():" + ((Kid)v.getTag()).name);
//
//					}
//				});
//
//				// Do the same setup for textView_minutes
//				textView_minutes.setText(String.valueOf(kid.timeout) + " minutes");
//				textView_minutes.setTag(kid);
//				textView_minutes.setOnClickListener(new View.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						// TODO Something when the kid's minutes is clicked
//						Log.d("KidsListArrayAdapter()","minutes.onClick():" + ((Kid)v.getTag()).name);
//
//					}
//				});
//				// And also for imageView_delete
//				imageView_delete.setImageResource(R.drawable.delete_icon);
//				imageView_delete.setTag(position);
//				imageView_delete.setOnClickListener(new View.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						// Get the item number in the list
//						int listItemNumber = Integer.parseInt(v.getTag().toString());
//						// Use the item number to find the Kid ID
//						int kidIDNumber = (kidsList.get(listItemNumber).id);
//						Log.d("KidsListArrayAdapter()","delete.onClick() position:" + listItemNumber + " id:" + kidIDNumber);
//
//
//						// The user clicked the "Delete" icon, so let's delete the kid.
//						// The penalty for accidentally deleting is really not very high,
//						// so I'm not going to bother confirming the deletion.
//						// Delete the kid from the config
//						Config.delKid(kidIDNumber);
//						Config.save();
//
//						// Tell everyone that the data changed, so they will refresh/redraw as needed.
//						notifyDataSetChanged();
//
//					}
//				});
			}
				// Yay, we're done building all that stuff, let's return a nice fat happy RowView
				return rowView;
		}

		/** getViewFromKid(int, View, ViewGroup, List<Kid>)
		 * 
		 * Pass it an index (position), it returns the View that's supposed to be at that place in the list.
		 * Generates/returns a single row for a ListView
		 * 
		 * Seems like serious overkill for what I'm doing, but okay.
		 */
		private View getViewFromKid(int position, View convertView, ViewGroup parent, final List<ConfigListItem> listItems) {

			// The RowView we're going to return, once we're done filling it out.
			//View rowView = new View(parent.getContext());
			
			// Find the kid in the list via position
			//ConfigListItem listItem = listItems.get(position);
			ConfigListItem listItem;
			

			if(convertView == null){
				// Create a LayoutInflater, use it to make a View from the layout XML for the kid list item
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.list_item_edit_kids, parent, false);

				// Make a new place to store our list item goodies
				listItem = listItems.get(position);
				
				// Use the IDs of the Views within the layout to get handles on them so we can manipulate them
				listItem.tvTitle = (TextView) convertView.findViewById(R.id.kid_name);
				listItem.tvDescription = (TextView) convertView.findViewById(R.id.kid_minutes);
				listItem.ivDeleteIcon = (ImageView) convertView.findViewById(R.id.kid_delete);
				listItem.ivEditIcon = (ImageView) convertView.findViewById(R.id.kid_edit);
				convertView.setTag(listItem);
			} else {
				// The goodies were already stored before, so let's use them.
				listItem = (ConfigListItem) convertView.getTag();
			}
			// Log it
			Log.d("KidsListArrayAdapter()","getViewFromKid() position:" + position + " id:" + listItem.kid.id + " name:" + listItem.kid.name);


			// ***** Set up all of the various Views in each row of the list *****
			// Set the TextView textView_name to the Kid's name,
			// Set a tag so we can differentiate this TextView when someone clicks it
			// and set up the onClick event handler so we can do stuff when the name is clicked.
			listItem.tvTitle.setText(listItem.kid.name);
			listItem.tvTitle.setTag(listItem);
			listItem.tvTitle.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Something when the kid's name is clicked
					Log.d("KidsListArrayAdapter()","name.onClick():" + ((ConfigListItem)v.getTag()).kid.name);

				}
			});

			// Do the same setup for textView_minutes
			listItem.tvDescription.setText(String.valueOf(listItem.kid.timeout) + " minutes");
			listItem.tvDescription.setTag(listItem);
			listItem.tvDescription.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Something when the kid's minutes is clicked
					Log.d("KidsListArrayAdapter()","minutes.onClick():" + ((ConfigListItem)v.getTag()).kid.name);

				}
			});
			
			// And for imageView_edit
			listItem.ivEditIcon.setImageResource(R.drawable.edit_icon);
			listItem.ivEditIcon.setTag(listItem);
			listItem.ivEditIcon.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					ConfigListItem tag = ((ConfigListItem)v.getTag());
					
					// Get the item number in the list
					int listItemNumber = listItems.indexOf(tag);

					// Find the Kid ID
					int kidIDNumber = tag.kid.id;
					
					// debug log
					Log.d("KidsListArrayAdapter()","delete.onClick() position:" + listItemNumber + " id:" + kidIDNumber);

					// TODO Do some junk here to allow the user to edit settings...

					// Tell everyone that the data changed, so they will refresh/redraw as needed.
					notifyDataSetChanged();

				}
			});
			
			// And also for imageView_delete
			listItem.ivDeleteIcon.setImageResource(R.drawable.delete_icon);
			listItem.ivDeleteIcon.setTag(listItem);
			listItem.ivDeleteIcon.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					ConfigListItem tag = ((ConfigListItem)v.getTag());
					
					// Get the item number in the list
					int listItemNumber = listItems.indexOf(tag);

					// Use the item number to find the Kid ID
					int kidIDNumber = tag.kid.id;
					
					
					// debug log
					Log.d("KidsListArrayAdapter()","delete.onClick() position:" + listItemNumber + " id:" + kidIDNumber);

					// The user clicked the "Delete" icon, so let's delete the kid.
					// The penalty for accidentally deleting is really not very high,
					// so I'm not going to bother confirming the deletion.
					// Delete the kid from the config
					Config.delKid(kidIDNumber);
					Config.save();
					configList.remove(listItemNumber);

					// Tell everyone that the data changed, so they will refresh/redraw as needed.
					notifyDataSetChanged();

				}
			});

			// Yay, we're done building all that stuff, let's return a nice fat happy RowView
			return convertView;
		}
	} 
}
