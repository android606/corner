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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import com.diabolicalschema.corner.Config;

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
	// Fields for SettingsView
	private TableLayout tableLayout1;
	private LayoutInflater inflater;
	
	//
	// onCreate()
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	Log.d(ACTIVITY_NAME, "onCreate()");
    	
    	ShowMainView();

    }
	
	//
	// onPause()
	// Called when the system pauses the activity
	// Save state
	@Override
	protected void onPause (){
    	Log.d(ACTIVITY_NAME, "onPause()");
    	super.onPause();
		Config.save();
	}

	//
	// onResume()
	// Called when the Activity resumes from pause.
	// Also called when the Activity is created, after onCreate()
	@Override
	protected void onResume(){
    	Log.d(ACTIVITY_NAME, "onResume()");
    	super.onResume();
		Config.load();
        //populateKidSelectorSpinner();		
    	ShowMainView();
	}

	/** listOfKidsForSpinner()
	 *  All this does is tack "Add/Edit Children..." to the end of the list, so it shows up in the kid selector
	 */
	private ArrayList<String> listOfKidsForSpinner(){
		 ArrayList <String> ListToReturn = new ArrayList<String>();
		 
		 ListToReturn.addAll(Config.listOfKidsNames());
		 ListToReturn.add(getString(R.string.add_edit_children));
		 	 
		 return ListToReturn;
	}

    /**
	 * populateKidSelectorSpinner()
     * Set up the kid selector spinner
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
	
	
    /**
     * addTestData()
     * Called when the user presses the "Add Test Data" button
     * @param view
     */ 
	public void addTestData(View view)
	{
		Config.addTestData();
		Config.save();
	}
	
    /**
     * clearConfig()
     * Called when the user presses the "Clear Config" button
     * @param view
     */ 
	public void clearTestData(View view)
	{
		Config.clearAllKids();
		Config.save();
	}

	/**
     * startStopTimer()
     * Called when the user presses the Start/Stop button
     * @param view
     */ 
	public void startStopTimer(View view)
	{
		timer.togglePause();
	}
	
	/**
	 * ShowMainView()
	 * changes the view to the "main" layout
	 * Used during onCreate or when someone returns from the Settings menu.
	 */
	public void ShowMainView(){
		currentlayout = R.layout.activity_main;
		this.setContentView(currentlayout);
		
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


	/**
	 * ShowKidsListView()
	 * Shows the list of kids so you can edit them 
	 */
	public void ShowKidsListView(){

		 Intent intent = new Intent(this, EditKidsActivity.class);
		    //EditText editText = (EditText) findViewById(R.id.edit_message);
		    //String message = editText.getText().toString();
		    //intent.putExtra(EXTRA_MESSAGE, message);
		    startActivity(intent);
		
	}


	
	/**
	 * 
	 * KidAction
	 * Holds the id of the Kid that we want to take an action on, and holds the action we want to take
	 * So's we can bundle them up together and make it neat and tidy to save them for later.
	 * 
	 */
	public enum kid_action {NO_CHANGE, CHANGE_NAME, CHANGE_TIMEOUT, DELETE_KID};
	public class KidAction 
	{
		public int id;
		public kid_action action;
		public KidAction(int kid_id, kid_action action){
			this.id = kid_id;
			this.action = action;
		};
		
	}
	
	
	// Override the "Back" button when a dialog is open and use it to close the dialog
	// Otherwise, let the "Back" button do whatever the system wants it to do
	@Override
	public void onBackPressed() {
	   Log.d(ACTIVITY_NAME, "onBackPressed() Called.  View ID:" + String.valueOf(currentlayout));

	   if(currentlayout == R.layout.activity_settings){

		   // Return to the main view
		   ShowMainView();
		   
	   } else {
		   
		   super.onBackPressed();
	   }

	}
	
	/*
	 * SpinnerEventHandler()
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

				ShowKidsListView();
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
}
