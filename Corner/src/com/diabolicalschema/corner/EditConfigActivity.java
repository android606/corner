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

	/** ConfigListItem
	 * Represents the data needed to draw a single list item
	 * @author android
	 */
	static class ConfigListItem {
		public Kid kid;
		public boolean isKid = true;
		public boolean rendered = false;
		public int layout;
		public TextView tvTitle;
		public TextView tvDescription;
		public ImageView ivEditIcon;
		public ImageView ivDeleteIcon;
		public String sTitle;
		public String sDescription;
		public int iIcon1;
		public int iIcon2;
		public int kid_id;	
	}

	/** getConfigList() 
	 * This generates the list of the items for the ListView 
	 * 
	 */
	public List<ConfigListItem> getConfigList() {
		List<ConfigListItem> listItems = new ArrayList<ConfigListItem>();

		// Add all of the kids to the list
		for(Kid x: Config.listKids()){
			ConfigListItem tempItem = new ConfigListItem();
			tempItem.kid = x;
			tempItem.kid_id = x.id;
			tempItem.sTitle = x.name;
			tempItem.sDescription = String.valueOf(x.timeout)  + " minutes";
			tempItem.iIcon1 = R.drawable.edit_icon;
			tempItem.iIcon2 = R.drawable.delete_icon;
			tempItem.layout = R.layout.list_item_edit_kids;
			listItems.add(tempItem);
		}
		
		// The "Add Kid" button
		ConfigListItem tempItem = new ConfigListItem();
		tempItem.layout = R.layout.list_item_add_kid;
		tempItem.isKid = false;
		listItems.add(tempItem);
		
		return listItems;		
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
		private final List<ConfigListItem> listItems;

		public ConfigListArrayAdapter(Context context, List<ConfigListItem> configList) {
			super(context, R.layout.list_item_edit_kids, configList);
			Log.d("ConfigListArrayAdapter()", "");
			this.context = context;
			this.listItems = configList;	
		}

		/** getView(int position, View convertView, ViewGroup parent)
		 * 
		 * Pass it an index (position), it returns the View that's supposed to be at that position in the list.
		 * 
		 * For example: System is trying to draw a ListView, it needs to know what the third item in the list looks 
		 * like.  It calls getView(3, null, parent).  getView then returns the ListItem that belongs in position 
		 * 3 in the list.  System inserts that into the list, draws it, and everything is happy.
		 *  
		 * @return ListItem a single row for a ListView
		 * 
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			

			// Draw Kid list items
			if(listItems.get(position).isKid){
//				Log.d("ConfigListArrayAdapter()","getView() position:" + position + " is a kid config list item." );

				// This is a kid, so we need to ask getViewFromKid to make a ListItem for us.
				convertView = getViewFromKid(position, convertView, parent);
			}
			// Draw static list items
			else 
			{
				Log.d("ConfigListArrayAdapter()","getView() position:" + position + " is a regular config list item." );
				
				ConfigListItem listItem = listItems.get(position);

				// Create a LayoutInflater, use it to inflate a View from the layout resource we stored earlier in listItem.layout
				// See getConfigList() about that
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(listItem.layout, parent, false);
				
				// Use the IDs of the Views within the layout to get handles on them so we can manipulate them
//				listItem.tvTitle = (TextView) convertView.findViewById(R.id.kid_name);
//				listItem.tvDescription = (TextView) convertView.findViewById(R.id.kid_minutes);
//				listItem.ivDeleteIcon = (ImageView) convertView.findViewById(R.id.kid_delete);
//				listItem.ivEditIcon = (ImageView) convertView.findViewById(R.id.kid_edit);
				convertView.setTag(listItem);
				
				// Store our changes
				listItems.set(position, listItem);

			}
				// Yay, we're done building all that stuff, let's return a nice fat happy RowView
				return convertView;
		}

		/** getViewFromKid(int, View, ViewGroup, List<Kid>)
		 * 
		 * Pass it an index (position), it returns the View that's supposed to be at that place in the list.
		 * Generates/returns a single row for a ListView
		 * 
		 * Seems like serious overkill for what I'm doing, but okay.
		 */
		private View getViewFromKid(int position, View convertView, ViewGroup parent) {
			
			// Find the kid in the list via position
			ConfigListItem listItem;
			listItem = listItems.get(position);
			

			// Create a LayoutInflater, use it to inflate a View from the layout resource we stored earlier in listItem.layout
			// See getConfigList() about that
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(listItem.layout, parent, false);

			// Use the IDs of the Views within the layout to get handles on them so we can manipulate them
			listItem.tvTitle = (TextView) convertView.findViewById(R.id.kid_name);
			listItem.tvDescription = (TextView) convertView.findViewById(R.id.kid_minutes);
			listItem.ivDeleteIcon = (ImageView) convertView.findViewById(R.id.kid_delete);
			listItem.ivEditIcon = (ImageView) convertView.findViewById(R.id.kid_edit);
			convertView.setTag(listItem);

			// Mark list item as "rendered", Store our changes
			listItem.rendered = true;
			listItems.set(position, listItem);

			Log.d("KidsListArrayAdapter()","getViewFromKid() (added to cache) position:" + position + " id: " + listItem.kid_id + " name: " + listItem.sTitle);


			// ***** Set up all of the various Views in each row of the list *****
			// Set the TextView textView_name to the Kid's name,
			// Set a tag so we can differentiate this TextView when someone clicks it
			// and set up the onClick event handler so we can do stuff when the name is clicked.
			listItem.tvTitle.setText(listItem.sTitle);
			listItem.tvTitle.setTag(listItem);
			listItem.tvTitle.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Something when the kid's name is clicked
					Log.d("KidsListArrayAdapter()","name.onClick():" + ((ConfigListItem)v.getTag()).kid.name);

				}
			});

			// Do the same setup for textView_minutes
			listItem.tvDescription.setText(listItem.sDescription);
			listItem.tvDescription.setTag(listItem);
			listItem.tvDescription.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Something when the kid's minutes is clicked
					Log.d("KidsListArrayAdapter()","minutes.onClick():" + ((ConfigListItem)v.getTag()).kid.name);

				}
			});
			
			// And for imageView_edit
			listItem.ivEditIcon.setImageResource(listItem.iIcon1);
			listItem.ivEditIcon.setTag(listItem);
			listItem.ivEditIcon.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					ConfigListItem tag = ((ConfigListItem)v.getTag());
					
					// Get the item number in the list
					int listItemNumber = listItems.indexOf(tag);

					// Find the Kid ID
					int kidIDNumber = tag.kid_id;
					
					// debug log
					Log.d("KidsListArrayAdapter()","edit.onClick() position:" + listItemNumber + " id:" + kidIDNumber);

					// TODO Do some junk here to allow the user to edit settings...

					// Tell everyone that the data changed, so they will refresh/redraw as needed.
					notifyDataSetChanged();
				}
			});
			
			// And also for imageView_delete
			listItem.ivDeleteIcon.setImageResource(listItem.iIcon2);
			listItem.ivDeleteIcon.setTag(listItem);
			listItem.ivDeleteIcon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					ConfigListItem tag = ((ConfigListItem)v.getTag());
					
					// Get the item number in the list
					int listItemNumber = listItems.indexOf(tag);

					// Use the item number to find the Kid ID
					int kidIDNumber = tag.kid_id;
					
					
					// debug log
					Log.d("KidsListArrayAdapter()","delete.onClick() position:" + listItemNumber + " id:" + kidIDNumber);

					// The user clicked the "Delete" icon, so let's delete the kid.
					// The penalty for accidentally deleting is really not very high,
					// so I'm not going to bother confirming the deletion.
					// Delete the kid from the config
					Config.delKid(kidIDNumber);
					Config.save();
					listItems.remove(tag);

					// Tell everyone that the data changed, so they will refresh/redraw as needed.
					notifyDataSetChanged();

				}
			});

			// Yay, we're done building all that stuff, let's return a nice fat happy RowView
			return convertView;
		}
	} 
}
