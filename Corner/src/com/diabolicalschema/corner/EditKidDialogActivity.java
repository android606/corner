package com.diabolicalschema.corner;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class EditKidDialogActivity extends Activity {
	private boolean newKidJustEntered = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_kid_dialog);
		// Show the Up button in the action bar.
//		setupActionBar();
		// TODO: This is just debug shit.  
		// TODO: Take this out and make it really get this flag from the calling Activity.
		newKidJustEntered = true;
		
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
			int timeoutLength = Integer.parseInt(newKidTimeout.getText().toString());
			
		if(newKidJustEntered){
			// Create a new kid in the config with the values the user entered
			Config.addKid(kidsName, timeoutLength);
		}
		
			Config.save();
			Config.printKidsToLog();
			
			Log.i("fsfsdf","back pressed, added the new kid to the DB :" + kidsName + " " + timeoutLength);
			
			
			// We're done saving the new kid, so, like, we can reset the flag.
			newKidJustEntered = false;
		 
//		else if(existingKidJustEdited){
//			Log.i("fsfsdf","black pressed, put code here to change the existing kid in the DB");
//			//TODO Add code to change kid in config
//			existingKidJustEdited = false;
//		}
			super.onBackPressed();
	}
}
