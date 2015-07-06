package com.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ecml.R;

/**
 * @class CalendarRehearsalProgramActivity : Not finished yet, it should 
 * be possible to choose a rehearsal program and add it to our own calendar. 
 *        
 * @author Anaïs
 */

/* 
 * The events should be add without having to be confirm.
 * This code should be used to do so : 
 * ContentValues values1 = new ContentValues();
			values1.put(Events.DTSTART, start);
			values1.put(Events.DTEND, start);
			values1.put(Events.RRULE, 
			      "FREQ=DAILY;COUNT=20;BYDAY=MO,TU,WE,TH,FR;WKST=MO");
			values1.put(Events.TITLE, "Some title");
			values1.put(Events.EVENT_LOCATION, "Music school");
			values1.put(Events.CALENDAR_ID, calId);
			values1.put(Events.EVENT_TIMEZONE, "Europe/Copenhage");
			values1.put(Events.DESCRIPTION, 
			      "The agenda or some description of the event");
			// reasonable defaults exist:
			values1.put(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
			values1.put(Events.SELF_ATTENDEE_STATUS,
			      Events.STATUS_CONFIRMED);
			values1.put(Events.ALL_DAY, 1);
			values1.put(Events.ORGANIZER, "some.mail@some.address.com");
			values1.put(Events.GUESTS_CAN_INVITE_OTHERS, 1);
			values1.put(Events.GUESTS_CAN_MODIFY, 1);
			values1.put(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
			Uri uri2 = 
			      getContentResolver().
			            insert(Events.CONTENT_URI, values1);
 * */
 

public class CalendarRehearsalProgramActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_listview);
		/**Create the list view*/
		populateListView();
		/**Allowed to add an action when on item from the list view is selected */
		registerClickCallback();
		
	}

	private void registerClickCallback() {
		ListView list = (ListView) findViewById(R.id.listView1);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg2 == 0) {

				}
				if (arg2 == 1) {

				}
			}
		});
	}

	private void populateListView() {
		// Create list of items
		String[] myItems = { "Normal rehearsal program for beginner", "Intensive rehearsal program for beginner",
				"Normal rehearsal program for intermediate student", "Intensive rehearsal program for intermediate student",
				"Normal rehearsal program for advanced student", "Intensive rehearsal program for advanced student" };

		// Build Adapter
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 							// Context for the activity
																R.layout.calendar_items, 		// Layout to use
																myItems); 						// Items to be displayed

		// Configure the list view.
		ListView list = (ListView) findViewById(R.id.listView1);
		list.setAdapter(adapter);
	}

	
	
}
