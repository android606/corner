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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TableLayout.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
	private TextView tvSelectedKid;
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

		   super.onBackPressed();

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
		
		// TODO: DEBUG
        // If there are no kids, add one.
        // Later, we will change this so that if there are no kids
        // Take the user to the "add kids" dialog
        if(Config.listKids().isEmpty()){
        	Kid tempkid = new Kid();
        	tempkid.name = "Susie Test";
        	tempkid.timeout = 1;
        	Config.addKid(tempkid);
        }
        
        // Set up the Selected Kid name TextView
        tvSelectedKid = (TextView) findViewById(R.id.textView_kids_name);
        int selectedKidId = Config.getSelectedKid();
        tvSelectedKid.setText(Config.getKid(selectedKidId).name);
        tvSelectedKid.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// If the user clicks the kid's name, take them to the list of kids.
				showConfigActivity();
			}
		});
        
		
        // Set up the time remaining display TextView 
        countdownTime = Config.getKid(selectedKidId).timeout * 1000 * 60;
		tbTimeRemaining = (TextView) findViewById(R.id.tbTimeRemaining);
		String dateFormatted = formatterTimer.format(new Date(countdownTime));
		tbTimeRemaining.setText(dateFormatted);
		tbTimeRemaining.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// When the user clicks the timer, it should start/pause
				timer.togglePause();
			}
		});
		tbTimeRemaining.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				// When the user long-clicks the timer, it should reset
				timer.cancel();
				setUpTimer(countdownTime);
				return true;
			}
		});
		
		// Initialize the timer object
		setUpTimer(countdownTime);
		
	}

	private void setUpTimer(long countdownTime){
		tbTimeRemaining = (TextView) findViewById(R.id.tbTimeRemaining);
		String dateFormatted = formatterTimer.format(new Date(countdownTime));
		tbTimeRemaining.setText(dateFormatted);
		
    	timer = new CDownTimerWithPause(countdownTime + 20, 100){
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

	
	/*
	 * DEBUG WIDGETS
	 */
	/** addDebugWidgets()
	 * Adds buttons 'n stuff to the View for to help debugging.
	 * It makes a layout, puts buttons in it, then adds the layout to the main view
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
