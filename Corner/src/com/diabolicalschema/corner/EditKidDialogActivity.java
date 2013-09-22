package com.diabolicalschema.corner;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class EditKidDialogActivity extends Activity {
	// Class variables
	private boolean addingANewKid = false;
	private Integer idOfKidToEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_kid_dialog);
		// Show the Up button in the action bar.
//		setupActionBar();

		// Set the default values in the edit kid dialog
		TextView newKidName = (TextView) findViewById(R.id.edit_kid_name);
		TextView newKidTimeout = (TextView) findViewById(R.id.edit_kid_timeout);
		TextView newKidDialogTitle = (TextView) findViewById(R.id.edit_kid_title);

		String kidsName;
		int timeoutLength;
		
		// Grab the id of the kid we should be editing, or -1 if it's a new kid
		idOfKidToEdit = getIntent().getIntExtra("ID_OF_KID_TO_EDIT", -1);
		
		// Clear or populate the form fields
		if (idOfKidToEdit == -1){
			Log.d("EditKidDialogActivity()","ID of kid to edit: -1, adding a new kid");

			// Set the flag so everyone knows this is a new kid
			addingANewKid = true;
			
			// Set the title of the dialog
			newKidDialogTitle.setText("Add a New Child");
			
			// Set the input fields empty
			kidsName = "";
			timeoutLength = 5;
			
			
		} else {
			Log.d("EditKidDialogActivity()","ID of kid to edit: " + idOfKidToEdit);
			
			// Set the flag so everyone knows this is an existing kid
			addingANewKid = false;

			// Set the title of the dialog
			newKidDialogTitle.setText("Edit a Child");
			
			// Use the ID passed in the extras to get the kid's name and stuff to populate form
			Kid kidToEdit = Config.getKid(idOfKidToEdit);
			kidsName = kidToEdit.name;
			timeoutLength = kidToEdit.timeout;
			
		}
		
		// Prepopulate values in the form using our local variables
		newKidName.setText(kidsName);
		newKidTimeout.setText(String.valueOf(timeoutLength));

	}

	// FU ActionBar
//	/**
//	 * Set up the {@link android.app.ActionBar}, if the API is available.
//	 */
//	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
//	private void setupActionBar() {
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//			getActionBar().setDisplayHomeAsUpEnabled(true);
//		}
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.edit_kid_dialog, menu);
		return true;
	}

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
	/** onBackPressed()
	 * 
	 * Happens when the "back" button is pressed.
	 * If the user just entered a new kid, we want to save the kid and take 
	 * them back to the kids list
	 * 
	 * If they were already at the kids list, let the system default action 
	 * happen, which will take them back to the Main activity.
	 */
	@Override
	public void onBackPressed(){

		// Grab the values from the edit kid dialog
		TextView newKidName = (TextView) findViewById(R.id.edit_kid_name);
		TextView newKidTimeout = (TextView) findViewById(R.id.edit_kid_timeout);
		String kidsName = newKidName.getText().toString();
		String timeoutLengthString = newKidTimeout.getText().toString();
		Integer timeoutLength;


		if(addingANewKid){
			// Grab the timeout the user entered, with a default of 5 if they enter ""
			if (timeoutLengthString.length() == 0){
				timeoutLength = 5;
			}
			else {
				timeoutLength = Integer.parseInt(timeoutLengthString);
			}
			// Create a new kid in the config with the values the user entered
			Config.addKid(kidsName, timeoutLength);
		} 
		else {
			// Or, pull up an existing kid and change the properties to the settings the user entered
			Kid kid = Config.getKid(idOfKidToEdit);

			// Grab the timeout the user entered, if they entered one.
			// If they didn't enter one, skip all this and the kid's timeout will stay the same.
			if(timeoutLengthString.length() > 0){
				timeoutLength = Integer.parseInt(timeoutLengthString);
				// If the timeout length is insanely big, don't try to change it. 
				if( timeoutLength < 1440){
					kid.timeout = 1440;
				}
				kid.timeout = timeoutLength;
			}
			
			// Change the kids name to whatever they entered, even if it's blank or something dumb
			kid.name = kidsName;
		}
			// We changed the config, and it's likely we'll be exiting now, so save the config.
			Config.save();

			// Do whatever else the system wants to do when the back button is pressed
			super.onBackPressed();
	}
}
