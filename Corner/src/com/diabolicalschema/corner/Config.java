package com.diabolicalschema.corner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


/** Config
 * Static class that holds all of the settings for the program and deals with keeping them in 
 * persistent storage.
 * 
 * Holds individual settings, as well as the list of Kid objects that the program uses to store kid-specific settings.
 * 
 * For now, it stores everything using Android's SharedPreferences.
 * 
 * @author android606
 * 
 */
public class Config {
	private static Context context;
	private static final String CLASS_NAME = "Config()";
	private static List<Kid> _kids = new ArrayList<Kid>();
	private static SharedPreferences prefs;
	private static int nextID;
	private static int selectedKid; 
	
	public static int getSelectedKid() {
		
		// If there are no kids in the config, then anything they're asking for is wrong.  Return -1.
		if(_kids.isEmpty()){ return -1; }
		
		// If they try to getSelectedKid, and it's not set, or it's set to something dumb, 
		// just return the ID of the first kid in the config
		if(selectedKid < 0 || Config.getKid(selectedKid) == null){
			return _kids.get(0).id;
		}
		return selectedKid;
	}
	public static void setSelectedKid(int selectedKid) {
		Config.selectedKid = selectedKid;
	}

	/** The ID of the last kid the user "selected" */
	
	static {


	}
	// Create a variable to hold a reference to our one and only Config object
	private static final Config _theInstance = new Config();
	private Config()
	{
		context = ContextProvider.getContext();
		try {}
		catch (Exception e) {}
	}
	/**
	 * Get the instance of the Config object
	 * 
	 * @return Config
	 */
	public static Config getInstance()
	{
		if (_theInstance == null)
		{
			throw new RuntimeException("Something unpleasant happened trying to get the Config().");
		} else {
			return _theInstance;
		}
	}

	// Removes all kids from the config (mainly for debugging)
	public static void clearAllKids(){
		_kids.clear();
		
	}

	//
	// printKidsToLog()
	// prints out a list of the kids in the current config to the log
	public static void printKidsToLog(){
        // Print current list of kids to the log
        Log.i(CLASS_NAME, "Kids in the config:");
        for(Kid kid: _kids){
        	Log.i(CLASS_NAME, kid.id + ": " + kid.name + "     " + kid.timeout);
        }
		
	}
	
	
	/** 
	 * Methods dealing with persistent storage
	 * @param context Context of the caller, will be used to get application context
	 */	
	///
	/// save()
	/// Saves kids and settings to persistent storage
	public static void save(){
		// Then, save all of the general config options
		// Get the handle on our SharedPreferences
		prefs = PreferenceManager.getDefaultSharedPreferences(context);

		// Clear the SharedPreferences and then save each preference
		SharedPreferences.Editor spedit = prefs.edit();
		spedit.clear();
		spedit.putInt("nextID", nextID);
		spedit.putInt("selectedKid", selectedKid);
		spedit.commit();

		// Save all of the kids in the config
		saveKids();
		
	}
	

	///
	/// saveKids()
	/// saves the kids to persistent storage
	private static void saveKids(){
		String settingKey;
		
		// Get the handle on our SharedPreferences
		prefs = PreferenceManager.getDefaultSharedPreferences(context);

		// Save each of the kids
		SharedPreferences.Editor spedit = prefs.edit();
		
		for (Kid x : _kids){
			settingKey = "____kids("+_kids.indexOf(x)+").id";
			spedit.putInt(settingKey, x.id);

			settingKey = "____kids("+_kids.indexOf(x)+").name";
			spedit.putString(settingKey, x.name);

			settingKey = "____kids("+_kids.indexOf(x)+").timeout";
			spedit.putInt(settingKey, x.timeout);

		}
		spedit.commit();

		printKidsToLog();
	}

	///
	/// load ()
	/// Loads kids and settings from persistent storage
	public static void load(){
		// Get the handle on our SharedPreferences
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		// Load general settings
		// nextID is the ID we're going to assign next time we create a Kid
		nextID = prefs.getInt("nextID", 0);
		
		// selectedKid is the id of the kid that the user last selected.  We'll use this at startup to initialize the settings.
		selectedKid = prefs.getInt("selectedKid", -1);
		
		// load all the kids from the config
		loadKids();
	}
		
	///
	/// loadKids()
	/// loads the kids up from persistent storage
	///
	/// Careful, don't mix up ID and Offset in here.  Each kid has a unique ID, but that's not used to
	/// store and retrieve them from storage.  They're stored by
	/// offset in the SharedPreferences, and that's how they're reconstructed.
	private static void loadKids(){
		String pattern = ".{8}\\((\\d+)\\)(.*)";
		Pattern p = Pattern.compile(pattern);
		Kid tempKid;
		int tempOffset;
		
		// Ditch all the kids we have in memory
		_kids.clear();
		
		// Get the handle on our SharedPreferences
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		Map<String, ?> prefsMap = prefs.getAll();
		
		for(String key : prefsMap.keySet()){
			if (key == null || key.length() == 0) {
				break;
			}
			
			if (key.length() > 8 && key.substring(0,8).equals("____kids")){
				Matcher m = p.matcher(key);
				if(m.find()){
					
					// Extract the Offset from the match
					tempOffset = Integer.parseInt(m.group(1));

					// If this ID is beyond the end of the list, extend the list
					while(tempOffset >= _kids.size()){
						_kids.add(new Kid());
					}
					
					// Now that we know the list is big enough, get a kid with this offset
					tempKid = _kids.get(tempOffset);
					
					// If we fail to get a kid, something's very wrong.  Just give up and try the next key.
					if (tempKid == null){
						Log.e("Config()","loadKids(): Couldnt get a kid from the collection, config is corrupted");
						break;
					}
					
					// Find out what property this key represents and stick it's data in the tempKid
					if(m.group(2).equals(".name")){
						tempKid.name = prefs.getString(key,"");
					}
					else if(m.group(2).equals(".timeout")){
						tempKid.timeout = prefs.getInt(key,0);
					}
					else if(m.group(2).equals(".id")){
						tempKid.id = prefs.getInt(key,0);
					}
				
					_kids.set(tempOffset, tempKid);
					//setKid(Integer.parseInt(m.group(1)), tempKid);
				
				}
			}
		}
		printKidsToLog();
	}
	
	
	//
	// addKid()
	// adds a kid to the configuration
	public static int addKid(Kid kid){
		return addKid(kid.name, kid.timeout);
	}
	public static int addKid(String kidsName, int timeoutLength){
		
		//Place new kid in the collection
		Kid newKid = new Kid();
		
		newKid.name = kidsName;
		newKid.timeout = timeoutLength;
		newKid.id = nextID;
		nextID++;

		_kids.add(newKid);
		
		//Return the index of the kid we just added in case anyone cares
		return newKid.id;
	}
	
	/**
	 * getKid(int)
	 * 
	 * Give it the int ID of a Kid, it returns the Kid.
	 * return null if the kid doesn't exist
	 * 
	 * @param id
	 * @return Kid
	 */
	public static Kid getKid(int id){
		for (Kid x : _kids){
			if (x.id == id){
				return x;
			}
		}
		return null;
	}

	/** getKid(String)
	 * 
	 * Same as getKid(int), except it accepts a String representation of the Kid's ID.
	 * 
	 * @param id
	 * @return Kid
	 */
	public static Kid getKid(String id){
		return getKid(Integer.parseInt(id));
	}
	
	//
	// setKid()
	// modifies the settings of an existing kid
	public static boolean setKid(int id, Kid kid){
		for (Kid x : _kids){
			if (x.id == id){
				x = kid;
				return true;
			}
		}
		return false;
	}
	
	//
	// getTimeout(int)
	// Given the id of the kid, returns the timeout amount
	public static long getTimeout(int index){
		return _kids.get(index).timeout;
	}
	
	//
	// delKid(int idOfKidToRemove)
	// removes the kid from the configuration with the matching ID
	// returns false on failure (if the kid doesn't exist)
	public static boolean delKid(int idOfKidToRemove){
		boolean rv = false;
		
		for(Kid x : _kids){
			if(x.id == idOfKidToRemove){
				_kids.remove(x);
				rv = true;
				break;
			}
		}
		return rv;		
	}
	
	//
	// listKids()
	// returns a list of the kids
	public static List<Kid> listKids(){
		return _kids;
	}
	
	//
	// listOfKidsNames()
	// returns a List<String> formatted primarily for the list of kids spinner/selector
	public static List<String> listOfKidsNames(){
		List<String> _NamesList = new ArrayList<String>();

		
		for (Kid x : _kids){
			String strTemp = x.name + " : " + x.timeout + " mins";
			_NamesList.add(strTemp);
		}

		return _NamesList;
	}
	
	//
	// getAddNewKidIndex
	// Returns the index of the menu item corresponding to "Add new child..."
	public static int getAddNewKidIndex(){
		return _kids.size();
	}
}
