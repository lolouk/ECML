package com.calendar;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.format.Time;

/**
 * @class CalendarPreferencesActivity The user can change settings for the
 *        calendar such as: - The preferred hour to start practice -
 *        Availability - Whether the guests can invite other guests - Whether
 *        the guests can see the list of attendees - If they want a reminder -
 *        Which alarm method they choose - Number of minutes prior to the event
 *        that the reminder should fire - Default location
 *        
 * @author Anaïs
 */

public class CalendarPreferencesActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

	private Preference restoreDefaults;
	/** Restore default settings */
	private CheckBoxPreference reminders;
	/** Boolean reminder or not */
	private ListPreference alarmMethod;
	/** Alarm method */
	private ListPreference minutesPriorAlarm;
	/** Minutes prior to the event that the reminder should fire */
	private CheckBoxPreference guestOtherGuest;
	/** Guests can invite other guests */
	private CheckBoxPreference GuestSeeAttendee;
	/** Guests can see the list attendees */
	private CheckBoxPreference availability;
	/** Mark as busy time */
	private EditTextPreference location;
	/** Default location */
	private TimePreference timePicker;
	/** TimePicker to choose preferred hour to start practicing */
	private Time time;
	/** Preferred hour to start practicing */
	
	/***Different settings for the Calendar that can be chosen in the calendar preferences */
	public static String location_string;
	public static boolean availability_boolean;
	public static Time preferred_time;
	public static boolean reminder;
	public static String alarmMethod_string;
	public static int minutePriorEvent;
	public static boolean guestsOtherGuests;
	public static boolean guestsSeeAttendees;

	/**
	 * Create the Calendar Settings activity. Retrieve the initial option
	 * values.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().getDecorView().setBackgroundColor(Color.WHITE);
//		preferred_time.hour = 17;
//		preferred_time.minute = 0;
		createView();
	}

	/** Create all the preference widgets in the view */
	private void createView() {
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
		createRestoreDefaultPrefs(root);

		/******************* GENERAL SETTINGS ******************/
		// Create the part title
		PreferenceCategory generalTitle = new PreferenceCategory(this);
		generalTitle.setTitle("General settings");
		root.addPreference(generalTitle);

		createAvailability(root);
		createLocation(root);
		createHour(root);

		/******************* REMINDERS *************************/
		// Create the part title
		PreferenceCategory remindersTitle = new PreferenceCategory(this);
		remindersTitle.setTitle("Reminders");
		root.addPreference(remindersTitle);

		createreminders(root);
		createAlarmMethod(root);
		createMinutesPriorAlarm(root);

		/******************* CONCERTS AND MEETINGS *************/

		// Create the part title
		PreferenceCategory meetingsTitle = new PreferenceCategory(this);
		meetingsTitle.setTitle("Concerts and meetings");
		root.addPreference(meetingsTitle);

		createGuestOtherGuest(root);
		createGuestSeeAttendee(root);

		setPreferenceScreen(root);
	}

	/**
	 * For each list dialog, we display the value selected in the "summary"
	 * text. When a new value is selected from the list dialog, update the
	 * summary to the selected entry.
	 */
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		ListPreference list = (ListPreference) preference;
		int index = list.findIndexOfValue((String) newValue);
		CharSequence entry = list.getEntries()[index];
		preference.setSummary(entry);
		return true;
	}

	/**
	 * When the 'restore defaults' preference is clicked, restore the default
	 * settings
	 */
	public boolean onPreferenceClick(Preference preference) {
		updateOptions();
		if (preference == restoreDefaults) {
			defaultSettings();
			createView();
		}
		return true;
	}

	/********************************** GENERAL SETTINGS FUNCTIONS *********************************/
	/** Create the "Availability" preference */
	private void createAvailability(PreferenceScreen root) {
		availability = new CheckBoxPreference(this);
		availability.setTitle("Mark as a busy time");
		availability.setChecked(availability_boolean);
		root.addPreference(availability);
	}

	/** Create the "default location" preference */
	private void createLocation(PreferenceScreen root) {
		location = new EditTextPreference(this);
		location.setTitle("Choose a default location");
		root.addPreference(location);
	}

	/** Create the "preferred hour to start practicing" preference */
	private void createHour(PreferenceScreen root) {
		time = new Time();
		timePicker = new TimePreference(this, null);
		timePicker.setTitle("Choose the preferred hour to start practicing");
		time.hour = timePicker.getLastHour();
		time.minute = timePicker.getLastMinute();
		root.addPreference(timePicker);
	}

	/********************************* END OF GENERAL SETTINGS FUNCTIONS **************************/

	/********************************** REMINDERS FUNCTIONS *********************************/

	/** Create the "Reminders" part. */
	private void createreminders(PreferenceScreen root) {
		reminders = new CheckBoxPreference(this);
		reminders.setTitle("Have a reminder");
		reminders.setChecked(reminder);
		root.addPreference(reminders);
	}

	/**
	 * Create the "Select alarm method " lists.
	 */
	private void createAlarmMethod(PreferenceScreen root) {
		alarmMethod = new ListPreference(this);
		alarmMethod.setOnPreferenceChangeListener(this);
		alarmMethod.setEntries(AlarmMethod);
		alarmMethod.setEntryValues(AlarmMethod);
		alarmMethod.setTitle("AlarmMethod");
		alarmMethod.setSummary(alarmMethod.getEntry());
		root.addPreference(alarmMethod);
	}

	/**
	 * Create the "Select minutes prior event for alarm " lists.
	 */
	private void createMinutesPriorAlarm(PreferenceScreen root) {
		minutesPriorAlarm = new ListPreference(this);
		minutesPriorAlarm.setOnPreferenceChangeListener(this);
		minutesPriorAlarm.setEntries(MinutesPriorAlarm);
		minutesPriorAlarm.setEntryValues(MinutesPriorAlarm);
		minutesPriorAlarm.setTitle("Minutes prior to the event that the reminder should fire");
		minutesPriorAlarm.setSummary(minutesPriorAlarm.getEntry());
		root.addPreference(minutesPriorAlarm);
	}

	/********************************* END OF REMINDERS FUNCTIONS **************************/

	/********************************** CONCERTS & MEETINGS FUNCTIONS *********************************/

	/** Create the "Guests can invite other guests" preference */
	private void createGuestOtherGuest(PreferenceScreen root) {
		guestOtherGuest = new CheckBoxPreference(this);
		guestOtherGuest.setTitle("Guests can invite other guests");
		guestOtherGuest.setChecked(guestsOtherGuests);
		root.addPreference(guestOtherGuest);
	}

	/** Create the "Guests can see the list of attendees" preference */
	private void createGuestSeeAttendee(PreferenceScreen root) {
		GuestSeeAttendee = new CheckBoxPreference(this);
		GuestSeeAttendee.setTitle("Guests can see the list of attendees");
		GuestSeeAttendee.setChecked(guestsSeeAttendees);
		root.addPreference(GuestSeeAttendee);
	}

	/********************************* END OF CONCERTS & MEETINGS FUNCTIONS **************************/

	/* Create the "Restore Default Settings" preference */
	private void createRestoreDefaultPrefs(PreferenceScreen root) {
		restoreDefaults = new Preference(this);
		restoreDefaults.setTitle("Restore default settings");
		restoreDefaults.setOnPreferenceClickListener(this);
		root.addPreference(restoreDefaults);
	}

	/** Update the MidiOptions based on the preferences selected. */
	private void updateOptions() {
		reminder = reminders.isChecked();
		guestsOtherGuests = guestOtherGuest.isChecked();
		guestsSeeAttendees = GuestSeeAttendee.isChecked();
		availability_boolean = availability.isChecked();
		location_string = location.getText();
		minutePriorEvent = StringToInt(minutesPriorAlarm.getValue());
		alarmMethod_string = alarmMethod.getValue();
		preferred_time = new Time();
		preferred_time.hour 
		=
		time.hour;
		preferred_time.minute = time.minute;
	}

	//Different choices for the alarm method
	public static String[] AlarmMethod = { "Default alarm", "Email", "alert" };

	//Different choices for the number of minutes prior to the event that the reminder should fire
	public static String[] MinutesPriorAlarm = { "10 min", "20 min", "30 min", "40 min", "50 min", "60 min", "70 min", "80 min", "90 min", "100 min",
			"110 min", "120 min" };

	//Convert String to int
	public static int StringToInt(String minutes) {
		int i;
		int ans = 0;
		for (i = 0; i == 11; i++) {
			if (minutes == MinutesPriorAlarm[i]) {
				ans = (i + 1) * 10;
			}
		}
		return ans;
	}

	//When we closed the activity, the settings are updated
	@Override
	public void onPause() {
		// Always call the superclass first
		super.onPause();

		// Save the settings update
		updateOptions();
	}
	
	public static void defaultSettings() {
		location_string = "home";
		availability_boolean = true;
		preferred_time.hour=17;
		preferred_time.minute=0;
		//Log.i("hour", "" + getHour());
		reminder = true;
		alarmMethod_string = "alert";
		minutePriorEvent = 10;
		guestsOtherGuests = false;
		guestsSeeAttendees = false;
	}

	public static Time getHour() {
		return preferred_time;
	}

	public static void setHour(Time hour) {
		CalendarPreferencesActivity.preferred_time = hour;
	}
}
