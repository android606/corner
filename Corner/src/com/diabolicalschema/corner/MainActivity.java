package com.diabolicalschema.corner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RelativeLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import com.diabolicalschema.corner.Config;

/** class MainActivity
 * Okay, I know, that class name sucks.
 * 
 * Corner is an Android app intended to be a custom timer to help you put your kids in time out.
 * 
 * For now, it displays a countdown timer on the screen with start/stop and reset buttons.  The timer is preset to the 
 * time associated with the currently selected child.
 * 
 * When they select one of the children, the timer is reset to the number of minutes associated with that child.
 * 
 * The list of childrens' names, times, and other information is retrieved from the Config class.
 * 
 * The user accesses the configuration (to select, modify, add and remove children) using the view provided by the 
 * EditConfigActivity class.
 * 
 * @see com.diabolicalschema.corner.EditConfigActivity 
 * @author android606
 */
@SuppressLint("SimpleDateFormat")
public class MainActivity extends Activity {
	@SuppressLint("SimpleDateFormat")
	// Fields for everyone
	public static final String EXTRA_MESSAGE = "com.diabolicalschema.corner.MESSAGE";
	public static final String ACTIVITY_NAME = "MainActivity()";
	public int currentlayout;
	// Fields for MainView
	private CDownTimerWithPause timer;
	private TextView tbTimeRemaining;
	private Spinner spSelectKid;
	private DateFormat formatterTimer = new SimpleDateFormat("mm:ss");
	private long countdownTime = 0;

	/*
	 * ACTIVITY EVENT HANDLERS
	 */
	/** onCreate()
	 * Called when the activity is created
	 * 
	 * Don't need to do anything here, because we do it all in onResume, which is always called after onCreate().
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//    	Log.d(ACTIVITY_NAME, "onCreate()");

    }
	
	/** onPause()
	 * Called when the system pauses the activity.
	 * This is supposed to happen any time this activity is completely obscured onscreen by another one.
	 *
	 * There's also a good chance that the system will stop this activity after onPause(), so it's a good
	 * idea to store our state and save it for later.
	 * 
	 * Saves state
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause (){
//    	Log.d(ACTIVITY_NAME, "onPause()");
    	super.onPause();
		Config.save();
	}

	/** onResume()
	 * Called when the Activity resumes from pause.
	 *
	 * Also called when the Activity is created, after onCreate()
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume(){
    	Log.d(ACTIVITY_NAME, "onResume()");
    	super.onResume();
		Config.load();
    	ShowMainView();

	}

	/** onBackPressed()
	 * Called when the user presses the "back" button
	 * 
	 * Sometimes we want to capture this to prevent them from exiting the app
	 */
	@Override
	public void onBackPressed() {
	   Log.d(ACTIVITY_NAME, "onBackPressed() Called.  View ID:" + String.valueOf(currentlayout));

// Commented this out because I'm not using the activity_settings layout anymore.
// But I still want to keep an example of how to block the 'back' button conditionally...
//
//	   // If the user is currently looking at the settings activity and they press "back"
//	   // bring them back to the main activity, rather than doing the system default of just exiting.
//	   if(currentlayout == R.layout.activity_settings){
//
//		   // Return to the main view
//		   ShowMainView();
//		   
//	   } 
//	   // If they *aren't* looking at the settings activity, go ahead and do the system default action
//	   else {
//		   
		   super.onBackPressed();
//	   }

	}
	
	/*
	 * GUI ELEMENTS
	 */
	/**
	 * ShowMainView()
	 * changes the view to the "main" layout
	 * Used during onResume()
	 */
	public void ShowMainView(){
		this.setContentView(R.layout.activity_main);
		// Add any buttons and crap I might have for debugging
        addDebugWidgets();
		
		// Add the kids to the spinner
        populateKidSelectorSpinner();
		
        //Set up the time remaining display TextView 
		tbTimeRemaining = (TextView) findViewById(R.id.tbTimeRemaining);
		String dateFormatted = formatterTimer.format(new Date(countdownTime));
		tbTimeRemaining.setText(dateFormatted);
		
        // Set up the timer (1000 = 1 second)
    	timer = new CDownTimerWithPause(countdownTime, 1000){
    		@Override
			public void _onFinish(){
				tbTimeRemaining.setText(R.string.timer_finished_value);
			}
			@Override
			public void _onTick(long millisUntilFinished){
				
				String timeFormatted = formatterTimer.format(new Date(millisUntilFinished));
				tbTimeRemaining.setText(timeFormatted);
			}
    	};
	}
	/*
	 * GUI ELEMENT - Kid Selector
	 */
	/** listOfKidsForSpinner()
	 *  All this does is tack "Add/Edit Children..." to the end of the list, so it shows up in the kid selector
	 */
	private ArrayList<String> listOfKidsForSpinner(){
		 ArrayList <String> ListToReturn = new ArrayList<String>();
		 
		 ListToReturn.addAll(Config.listOfKidsNames());
		 ListToReturn.add(getString(R.string.add_edit_children));
		 	 
		 return ListToReturn;
	}

    /** populateKidSelectorSpinner()
     * Attaches an adapter to the kid selector spinner
     */
	private void populateKidSelectorSpinner(){
		// Get the handle to the spinner
        spSelectKid = (Spinner) findViewById(R.id.spinner1);
        // Make the adapter to the list of kids
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, 
        		android.R.layout.simple_spinner_item,
        		listOfKidsForSpinner()
        		);        
        
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        spSelectKid.setAdapter(dataAdapter);
        
        /* Set up the Spinner event handler
         * We have to do this in .post() because if we set up the event handler *before* we populate the Spinner,
         * the system calls the event handler (SpinnerActivity) once, as if the user selected "item 0" on the Spinner.
         * this is irritating default behavior on the part of Android, but whatever.  We deal with it by populating
         * the Spinner first, *then* instantiating and setting the OnItemSelectedListener.
         * 
         * Thanks, Stack Overflow: 
         * {@link http://stackoverflow.com/questions/2562248/android-how-to-keep-onitemselected-from-firing-off-on-a-newly-instantiated-spin }
         */	 
        spSelectKid.post(new Runnable() {
        	@Override
			public void run() {
    	    		spSelectKid.setOnItemLongClickListener(new SpinnerEventHandler());
        	    	spSelectKid.setOnItemSelectedListener(new SpinnerEventHandler());
        	    	spSelectKid.setLongClickable(true);
        		}
        	});
        
        
	}

	/** showConfigActivity()
	 * Shows the Configuration activity so you can change config options, including the list of kids.
	 * @see com.diabolicalschema.corner.EditConfigActivity 
	 */
	public void showConfigActivity(){

		 Intent intent = new Intent(this, EditConfigActivity.class);
		    //EditText editText = (EditText) findViewById(R.id.edit_message);
		    //String message = editText.getText().toString();
		    //intent.putExtra(EXTRA_MESSAGE, message);
		    startActivity(intent);
		
	}

	/** SpinnerEventHandler()
	 * Triggered when an item is selected in the "child selector" Spinner
	 */
	public class SpinnerEventHandler extends Activity 
								implements OnItemSelectedListener, OnItemLongClickListener {
		
		//
		// onItemSelected()
		// Triggered when an item in the selector is selected
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
			// An item was selected in a spinner somewhere.  Figure out which spinner it came from, and which 
			// item was selected on that spinner.  Then, get the kid's timeout from the
			// config and set the timer and display to match that.
			Log.d(ACTIVITY_NAME + ":SpinnerEventHandler()","onItemSelected(" + pos + ")");

			// If the selected menu item is "Add new child..."
			// start up the PreferencesActivity so the user can enter the new kid's data
			if(pos == Config.getAddNewKidIndex()){
				view.getTag();

				showConfigActivity();
				//ShowSettingsView();

			} 
			// Otherwise, one of the existing kids was selected, so load up their settings
			else {

				timer.setTimeRemaining((int)Config.getTimeout(pos) * 60 * 1000);
				String timeFormatted = formatterTimer.format(new Date((int)Config.getTimeout(pos) * 60 * 1000));
				tbTimeRemaining.setText(timeFormatted);
			}
		}
		
		//
		// A Spinner event was triggered, but nothing was selected
		// Why would this ever happen?
		// I don't know, but the Android dev. guide says to include this.
		// I guess I'll figure this out later, when I'm a l337 h4x0r.
		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			Log.d(ACTIVITY_NAME + ":SpinnerEventHandler()","onNothingSelected()");
		}
		
		// Someone long-pressed an item in the spinner
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
			Log.d(ACTIVITY_NAME + ":SpinnerEventHandler()","onItemLongClick(" + pos + ")");
			return true;
		}
	
	}

	/*
	 * GUI ELEMENT - Timer Controls
	 */
	/** startStopTimer()
     * Called when the user presses the Start/Stop button
     * @param view
     */ 
	public void startStopTimer(View view)
	{
		timer.togglePause();
	}	

	/*
	 * DEBUG WIDGETS
	 */
	/** addDebugWidgets()
	 * Adds buttons 'n stuff to the View for to help debugging.
	 * Just keeping this in one place so it's easy to turn on and off
	 */
	public void addDebugWidgets()
	{
		// Get main layout
		LinearLayout layout = (LinearLayout) findViewById(R.id.layoutMainActivity);
		
		// Set up container for debug controls
		LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.weight = 1.0f;
		layoutParams.gravity = (Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

		LinearLayout layoutDebug = new LinearLayout(this);		
		layoutDebug.setOrientation(LinearLayout.HORIZONTAL);
		layoutDebug.setLayoutParams(layoutParams);

		// "Add Test Data" button
		Button buttonATD = new Button(this);
		buttonATD.setText("Add Test Data");
		buttonATD.setEnabled(true);
		buttonATD.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Add test data to the config
				// This just adds some fake data to the conrfiguration so that we have somehting to test and debug with

				Config.addKid("Nora", 7); // 7 minutes
			    Config.addKid("Emma", 5); // 5 minutes
			    Config.addKid("Zoe", 10); // 10 minutes        			
				Config.save();
				
			}
		});
		buttonATD.setLayoutParams(layoutParams);
		layoutDebug.addView(buttonATD);

		
		// "Clear Kids" button
		Button buttonCK = new Button(this);
		buttonCK.setText("Clear Kids");
		buttonCK.setEnabled(true);
		buttonCK.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Clear all of the kids from the config
				Config.clearAllKids();
				Config.save();
			}
		});
		buttonCK.setLayoutParams(layoutParams);
		
		layoutDebug.addView(buttonCK);
		
		
		// Finally, add the layout full of buttons to the main layout
		layout.addView(layoutDebug);
		
	}

}
